package org.dtt.mscatalog.application.service;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.request.ImageRequest;
import org.dtt.mscatalog.application.dto.request.ProductImgRequest;
import org.dtt.mscatalog.application.dto.response.ImageResponse;
import org.dtt.mscatalog.application.exception.ImageNotFoundException;
import org.dtt.mscatalog.application.port.out.ImageRepositoryPort;
import org.dtt.mscatalog.application.port.out.S3Port;
import org.dtt.mscatalog.domain.model.Enum.OwnerType;
import org.dtt.mscatalog.domain.model.Image;
import org.dtt.mscatalog.infrastructure.mapper.ImageMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling image-related operations.
 * It interacts with an S3 bucket for file storage, a repository for database operations,
 * and a mapper for transforming entities into response objects.
 */
@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Port s3Port;
    private final ImageRepositoryPort imageRepositoryPort;
    private final ImageMapper imageMapper;

    /**
     * Uploads an image for a specified owner and saves it in the repository.
     * This method interacts with an S3 storage to upload the image file and updates the cache accordingly.
     * The uploaded image is assigned an order based on the number of existing images for the owner.
     *
     * @param imageRequest the request object containing details about the image,
     *                     including the owner ID, owner type, and the image file.
     * @return the response object containing information about the uploaded image,
     *         such as the image URL, owner details, and order.
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "images-by-owner", key = "#imageRequest.ownerId() + '_' + #imageRequest.ownerType()"),
            @CacheEvict(value = "images-primary",  key = "#imageRequest.ownerId() + '_' + #imageRequest.ownerType()"),
            @CacheEvict(value = "images-primary-map", allEntries = true)
    })
    public ImageResponse uploadImage(ImageRequest imageRequest) {
        List<Image> existingImages = imageRepositoryPort.findByOwnerIdAndOwnerType(
                imageRequest.ownerId(),
                imageRequest.ownerType()
        );

        Short nextOrder = (short) existingImages.size();
        String pathFile = s3Port.uploadFile(imageRequest.image());

        Image newImage = Image.builder()
                .ownerId(imageRequest.ownerId())
                .ownerType(imageRequest.ownerType())
                .build();

        newImage.setOrder(nextOrder);
        newImage.refreshUrl(s3Port.getUrl(pathFile));

        Image savedImage = imageRepositoryPort.save(newImage);
        return imageMapper.toResponse(savedImage);
    }

    /**
     * Uploads product images for a specific owner.
     *
     * @param ownerId the unique identifier of the owner
     * @param imgRequests the list of image requests containing image data to be uploaded
     * @param ownerType the type of the owner (e.g., individual, organization)
     * @return a list of ImageResponse objects containing details of the uploaded images
     * @throws IllegalArgumentException if the imgRequests is null or empty
     */
    public List<ImageResponse> uploadProductImages(UUID ownerId, List<ProductImgRequest> imgRequests,OwnerType ownerType) {
        if( imgRequests== null || imgRequests.isEmpty() ){
            throw new IllegalArgumentException("Image requests cannot be null or empty");
        }
        List<ImageRequest> imageRequests = imgRequests.stream()
                .map(img -> ImageRequest.builder()
                        .ownerId(ownerId)
                        .ownerType(ownerType)
                        .image(img.image())
                        .build())
                .toList();
        return uploadImages(imageRequests);
    }


    /**
     * Uploads a list of image requests and returns the corresponding list of image responses.
     *
     * Each image from the provided list of {@code ImageRequest} objects is uploaded,
     * processed, and converted into an {@code ImageResponse} object.
     *
     * @param imagesRequest the list of {@code ImageRequest} objects to be uploaded
     * @return a list of {@code ImageResponse} objects representing the uploaded images
     */
    private List<ImageResponse> uploadImages(List<ImageRequest> imagesRequest) {
        return imagesRequest.stream()
                .map(this::uploadImage)
                .toList();
    }

    /**
     * Retrieves a list of images associated with a specified owner ID and owner type.
     *
     * @param ownerId the unique identifier of the owner whose images are to be retrieved
     * @param ownerType the type of the owner (e.g., USER, ORGANIZATION) used to filter images
     * @return a list of ImageResponse objects representing the images associated with the given owner
     */
    @Cacheable(value = "images-by-owner", key = "#ownerId + '_' + #ownerType")
    public List<ImageResponse> listImagesByOwnerId(UUID ownerId, OwnerType ownerType) {
        return imageRepositoryPort
                .findByOwnerIdAndOwnerType(ownerId, ownerType)
                .stream()
                .map(imageMapper::toResponse)
                .toList();
    }


    /**
     * Retrieves a map of primary images for a given list of owner IDs and owner type.
     * The map associates each owner ID with its corresponding primary image response.
     *
     * @param ownerIds the list of unique owner IDs for which primary images are to be retrieved
     * @param ownerType the type of the owner (e.g., PRODUCT, CATEGORY, etc.)
     * @return a map where the key is the owner ID and the value is the primary image response
     */
    @Cacheable(value = "images-primary-map", key = "#ownerIds.toString() + '_' + #ownerType")
    public Map<UUID, ImageResponse> getPrimaryImagesMap(List<UUID> ownerIds, OwnerType ownerType) {
        return imageRepositoryPort.findPrimaryImagesByOwnerIds(ownerIds, ownerType)
                .stream()
                .map(imageMapper::toResponse)
                .collect(Collectors.toMap(
                        ImageResponse::ownerId,
                        img -> img,
                        (existing, replacement) -> existing
                ));
    }

    /**
     * Deletes an image from the database and storage by its unique identifier.
     * The method also adjusts the order of the remaining images associated with the same owner,
     * ensuring the sequence remains consistent.
     * Caches related to images are automatically evicted upon successful deletion.
     *
     * @param imageId the unique identifier of the image to be deleted
     * @throws ImageNotFoundException if no image is found with the given identifier
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "images-by-owner",    allEntries = true),
            @CacheEvict(value = "images-primary",      allEntries = true),
            @CacheEvict(value = "images-primary-map", allEntries = true)
    })
    public void deleteImageById(UUID imageId) {
        Image imageToDelete = imageRepositoryPort.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException(imageId));

        UUID ownerId = imageToDelete.getOwnerId();
        OwnerType ownerType = imageToDelete.getOwnerType();
        Short deletedOrder = imageToDelete.getOrder();

        s3Port.deleteFile(imageToDelete.getUrl());
        imageRepositoryPort.delete(imageId);

        List<Image> remainingImages = imageRepositoryPort.findByOwnerIdAndOwnerType(ownerId, ownerType);
        for (Image img : remainingImages) {
            if (img.getOrder() > deletedOrder) {
                img.setOrder((short) (img.getOrder() - 1));
            }
        }
        imageRepositoryPort.saveAll(remainingImages);
    }

    /**
     * Deletes all images associated with a specific owner based on the provided owner ID and owner type.
     * Removes the associated cache entries to ensure cache consistency.
     *
     * @param ownerId   the unique identifier of the owner whose images are to be deleted
     * @param ownerType the type of the owner (e.g., USER, ORGANIZATION) whose images are to be deleted
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "images-by-owner",    allEntries = true),
            @CacheEvict(value = "images-primary",      allEntries = true),
            @CacheEvict(value = "images-primary-map", allEntries = true)
    })
    public void deleteImagesByOwner(UUID ownerId, OwnerType ownerType) {
        List<Image> images = imageRepositoryPort.findByOwnerIdAndOwnerType(ownerId, ownerType);
        for (Image image : images) {
            s3Port.deleteFile(image.getUrl());
        }
        imageRepositoryPort.deleteByOwnerIdAndOwnerType(ownerId, ownerType);
    }


    public Map<UUID, String> findPrimaryImageUrlsByOwnerIds(List<UUID> ownerIds, OwnerType ownerType) {
        return imageRepositoryPort.findPrimaryImagesByOwnerIds(ownerIds, ownerType)
                .stream()
                .collect(Collectors.toMap(Image::getOwnerId, Image::getUrl));
    }
}