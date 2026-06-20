package org.dtt.msauthpublic.service;

import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.dto.AuthResponse;
import org.dtt.msauthpublic.Utils.CustomerUserDetails;
import org.dtt.msauthpublic.Utils.JwtProperties;
import org.dtt.msauthpublic.Utils.JwtUtils;
import org.dtt.msauthpublic.model.RefreshToken;
import org.dtt.msauthpublic.model.User;
import org.dtt.msauthpublic.repository.RefreshTokenRepository;
import org.dtt.msauthpublic.repository.UserRepository;
import org.dtt.msauthpublic.repository.projections.AuthorityProjection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final JwtUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username == null) throw new RuntimeException("Username cannot be null");

        User userBd = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Collection<SimpleGrantedAuthority> authorities = userRepository
                .findAuthorities(username)
                .stream()
                .map(AuthorityProjection::getAuthority)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new CustomerUserDetails(
                userBd.getId(),
                userBd.getEmail(),
                userBd.getUserDetails().getName(),
                userBd.getUserDetails().getSurname(),
                userBd.getUsername(),
                userBd.getPassword(),
                userBd.isEnabled(),
                userBd.isAccountNonExpired(),
                userBd.isCredentialsNonExpired(),
                userBd.isAccountNonLocked(),
                authorities
        );
    }

    public AuthResponse authentication(String username, String password) {

        CustomerUserDetails userDetailsService = (CustomerUserDetails) loadUserByUsername(username);

        if (!passwordEncoder.matches(password,userDetailsService.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        System.out.println("USER ID: " + userDetailsService.getId());
        User user = userRepository.getReferenceById(userDetailsService.getId());

        String jwtRefreshToken = generateRefreshToken(user);

        String jwt = generateAccessToken(userDetailsService);

        return new AuthResponse(jwt,jwtRefreshToken);
    }

    private String generateRefreshToken(User user){
        String jwtRefreshToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken
                .builder()
                .refreshToken(jwtRefreshToken)
                .user(user)
                .expirationDate(
                        Instant
                                .now()
                                .plus(jwtProperties.refreshExpirationTime(), ChronoUnit.MINUTES)
                )
                .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getRefreshToken();
    }

    private String generateAccessToken(CustomerUserDetails userDetails){
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        String jwt = jwtUtils.generateToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwt;
    }

    public AuthResponse refreshToken(String refreshToken){

        UUID uuid = UUID.fromString(refreshToken);

        RefreshToken refreshTokenBd = refreshTokenRepository
                .findByRefreshToken(refreshToken)
                .orElseThrow(()->new RuntimeException("Refresh token not found"));

        if(refreshTokenBd.getExpirationDate().isBefore(Instant.now())){
            throw new RuntimeException("Refresh token expired");
        }

        CustomerUserDetails userDetailsService = (CustomerUserDetails) loadUserByUsername(refreshTokenBd.getUser().getUsername());

        String jwt = generateAccessToken(userDetailsService);

        return new AuthResponse(jwt,refreshToken);
    }
}
