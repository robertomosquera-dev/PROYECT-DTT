
CREATE
OR REPLACE VIEW vista_catalogo AS

SELECT sp.id,
       'SALE'::VARCHAR(20)         AS sku_type, pe.name,
       pe.description,
       sp.price,
       sp.stock,
       STRING_AGG(DISTINCT c.slug, ',') AS category_slugs,
       img_p.url,
       sp.status
FROM sale_products sp
         JOIN products pe ON sp.product_id = pe.id
         LEFT JOIN product_categories pc ON pe.id = pc.product_id
         LEFT JOIN categories c ON pc.category_id = c.id
         LEFT JOIN images img_p
                   ON img_p.owner_id = pe.id
                       AND img_p.owner_type = 'PRODUCT'
                       AND img_p.img_order = 0
WHERE sp.deleted = false
GROUP BY sp.id, pe.name, pe.description,
         sp.price, sp.stock, img_p.url, sp.status

UNION ALL

SELECT rp.id,
       'RENTAL'::VARCHAR(20)       AS sku_type, pe.name,
       pe.description,
       rp.monthly_price                 AS price,
       rp.stock,
       STRING_AGG(DISTINCT c.slug, ',') AS category_slugs,
       img_p.url,
       rp.status
FROM rental_products rp
         JOIN products pe ON rp.product_id = pe.id
         LEFT JOIN product_categories pc ON pe.id = pc.product_id
         LEFT JOIN categories c ON pc.category_id = c.id
         LEFT JOIN images img_p
                   ON img_p.owner_id = pe.id
                       AND img_p.owner_type = 'PRODUCT'
                       AND img_p.img_order = 0
WHERE rp.deleted = false
GROUP BY rp.id, pe.name, pe.description,
         rp.monthly_price, rp.stock, img_p.url, rp.status

UNION ALL

SELECT pb.id,
       'BUNDLE'::VARCHAR(20)       AS sku_type, pb.name,
       pb.description,
       pb.price,
       pb.stock,
       STRING_AGG(DISTINCT c.slug, ',') AS category_slugs,
       img_b.url,
       pb.status
FROM product_bundles pb
         LEFT JOIN bundle_categories bc ON pb.id = bc.bundle_id
         LEFT JOIN categories c ON bc.category_id = c.id
         LEFT JOIN images img_b
                   ON img_b.owner_id = pb.id
                       AND img_b.owner_type = 'BUNDLE'
                       AND img_b.img_order = 0
WHERE pb.deleted = false
GROUP BY pb.id, pb.name, pb.description,
         pb.price, pb.stock, img_b.url, pb.status;

