package org.dtt.msauthpublic.service;

import com.nimbusds.jose.proc.SecurityContext;
import jakarta.persistence.OrderBy;
import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.dto.*;
import org.dtt.msauthpublic.model.*;
import org.dtt.msauthpublic.repository.RoleRepository;
import org.dtt.msauthpublic.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SendCodeVerificationService sendCodeVerification;

    public UserResponse getUserById(UUID id){
        User user = userRepository
                .findByIdAndEnabledIsTrue(id)
                .orElseThrow(()->new RuntimeException("User not found or disabled"));
        if(!user.isVerified()){
            throw new RuntimeException("User not verified");
        }
        return  UserResponse
                .builder()
                .id(user.getId())
                .name(user.getUserDetails().getName())
                .surname(user.getUserDetails().getSurname())
                .phone(user.getUserDetails().getPhone())
                .address(user.getUserDetails().getAddress())
                .roles(user.getRoles().stream().map(Roles::getName).toList())
                .build();
    }

    public void deleteUser(UUID id){
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("User not found"));
        if(!user.isVerified()){
            throw new RuntimeException("User not verified");
        }
        user.setEnabled(false);
        userRepository.save(user);
    }

    public List<UserResponse> FindAllUsers(int page, int size, Sort sort){


        Pageable pageable = PageRequest.of(page,size,sort);


        List<User> users = userRepository.findAllByRoles_NameInAndEnabledTrueAndVerifiedTrue(Set.of(RoleName.USER),pageable);
        return users
                .stream()
                .map(user -> UserResponse
                    .builder()
                    .id(user.getId())
                    .name(user.getUserDetails().getName())
                    .surname(user.getUserDetails().getSurname())
                    .phone(user.getUserDetails().getPhone())
                    .address(user.getUserDetails().getAddress())
                    .roles(user.getRoles().stream().map(Roles::getName).toList())
                    .build()
                ).toList();
    }

    public UserResponse update(UserRequestUpdate request) {

        JwtAuthenticationToken jwt = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UUID uuid = UUID.fromString(jwt.getToken().getClaimAsString("userId"));

        User user = userRepository
                .findByIdAndEnabledIsTrue(uuid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.isVerified()){
            throw new RuntimeException("User not verified");
        }

        if (request.username() != null) user.setUsername(request.username());
        if (request.name() != null)     user.getUserDetails().setName(request.name());
        if (request.surname() != null)  user.getUserDetails().setSurname(request.surname());
        if (request.phone() != null)    user.getUserDetails().setPhone(request.phone());
        if (request.address() != null)  user.getUserDetails().setAddress(request.address());

        userRepository.save(user);

        return UserResponse
                .builder()
                .id(user.getId())
                .name(user.getUserDetails().getName())
                .surname(user.getUserDetails().getSurname())
                .phone(user.getUserDetails().getPhone())
                .address(user.getUserDetails().getAddress())
                .roles(user.getRoles().stream().map(Roles::getName).toList())
                .build();
    }

    public void changePassword(ChangePassword changePassword){
        JwtAuthenticationToken jwt =(JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String id = jwt.getToken().getClaimAsString("userId");
        UUID userId = UUID.fromString(id);
        User user = userRepository.findByIdAndEnabledIsTrue(userId).orElseThrow(()->new RuntimeException("User not found"));
        if(!user.isVerified()){
            throw new RuntimeException("User not verified");
        }
        if(!passwordEncoder.matches(changePassword.oldPassword(),user.getPassword())){
            throw new RuntimeException("Old password is incorrect");
        }
        String newPasswordHash = passwordEncoder.encode(changePassword.newPassword());
        user.setPassword(newPasswordHash);
        userRepository.save(user);
    }

    public void verifyUser(VerifyRequest verifyRequest){
        User user = userRepository
                .findByEmail(verifyRequest.email())
                .orElseThrow(()->new RuntimeException("User not found"));
        if(user.isVerified()){
            throw new RuntimeException("User already verified");
        }
        if(Instant.now().isAfter(user.getVerificationCodeExpiration())){
            throw new RuntimeException("Verification code expired");
        }
        if(!passwordEncoder.matches(verifyRequest.code(),user.getVerificationCode())){
            throw new RuntimeException("Invalid verification code");
        }
        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeIssuedAt(null);
        user.setVerificationCodeExpiration(null);
        userRepository.save(user);
    }

    public void resendCode(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified())
            throw new RuntimeException("User already verified");

        Instant now = Instant.now();
        String code = sendCodeVerification.sendCodeVerification(email, user.getUserDetails().getName());

        user.setVerificationCode(passwordEncoder.encode(code));
        user.setVerificationCodeIssuedAt(now);
        user.setVerificationCodeExpiration(now.plusSeconds(600));
        userRepository.save(user);
    }

    public RegisterResponse saveUser(RegisterRequest registerRequest){

        if (registerRequest.roles().isEmpty())
            throw new RuntimeException("User must have at least one role");

        Set<Roles> roles = roleRepository.findAllByIdIn(registerRequest.roles());

        String rolesString = roles
                .stream()
                .map(Roles::getName)
                .map(RoleName::name)
                .distinct()
                .collect(Collectors.joining(","));

        String permissions = roles
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permissions::getName)
                .map(PermissionsNames::name)
                .distinct()
                .collect(Collectors.joining(","));

        UserDetails userDetails = UserDetails
                .builder()
                .name(registerRequest.name())
                .surname(registerRequest.surname())
                .phone(registerRequest.phone())
                .address(registerRequest.address())
                .build();

        String code = sendCodeVerification.sendCodeVerification(registerRequest.email(),registerRequest.name());
        Instant now = Instant.now();

        User user = User
                .builder()
                .email(registerRequest.email())
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .verificationCode(passwordEncoder.encode(code))
                .verificationCodeIssuedAt(now)
                .verificationCodeExpiration(now.plusSeconds(600))
                .userDetails(userDetails)
                .roles(roles)
                .build();

        user = userRepository.save(user);

        return RegisterResponse
                .builder()
                .id(user.getId())
                .name(user.getUserDetails().getName())
                .surname(user.getUserDetails().getSurname())
                .phone(user.getUserDetails().getPhone())
                .address(user.getUserDetails().getAddress())
                .rol(rolesString)
                .permissions(permissions)
                .build();
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanUnverifiedUsers() {
        Instant limit = Instant.now().minus(7, ChronoUnit.DAYS);
        userRepository.deleteAllByVerifiedFalseAndCreatedAtBefore(limit);
    }

}
