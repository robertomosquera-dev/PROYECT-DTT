package org.dtt.msauthpublic.service;

import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.dto.RegisterRequest;
import org.dtt.msauthpublic.dto.RegisterResponse;
import org.dtt.msauthpublic.dto.UserResponse;
import org.dtt.msauthpublic.model.*;
import org.dtt.msauthpublic.repository.RoleRepository;
import org.dtt.msauthpublic.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUserById(UUID id){
        User user = userRepository
                .findByIdAndEnabledIsTrue(id)
                .orElseThrow(()->new RuntimeException("User not found or disabled"));
        return  UserResponse
                .builder()
                .id(user.getId())
                .name(user.getUserDetails().getName())
                .surname(user.getUserDetails().getSurname())
                .phone(user.getUserDetails().getPhone())
                .address(user.getUserDetails().getAddress())
                .build();
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

        User user = User
                .builder()
                .email(registerRequest.email())
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
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

}
