package com.apartmentbuilding.PTIT.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.ptitB22CN539.LaptopShop.Config.ConstantConfig;
import com.ptitB22CN539.LaptopShop.Config.JwtGenerator;
import com.ptitB22CN539.LaptopShop.Config.UserStatus;
import com.ptitB22CN539.LaptopShop.DTO.APIResponse;
import com.ptitB22CN539.LaptopShop.Domains.JwtEntity;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import com.ptitB22CN539.LaptopShop.Repository.JwtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Value(value = "${signerKey}")
    private String signingKey;
    private final JwtRepository jwtRepository;
    private final JwtGenerator jwtGenerator;
    @Value(value = "${api.prefix}")
    private String apiPrefix;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(request -> request
                .requestMatchers(HttpMethod.POST, "/%s/users/login/{social}".formatted(apiPrefix)).permitAll()
                .requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.POST, "/%s/users/(register|login)".formatted(apiPrefix))).permitAll()
                .requestMatchers(HttpMethod.POST, "/%s/users/refresh-token".formatted(apiPrefix)).permitAll()
                .requestMatchers(HttpMethod.POST, "/%s/users/logout".formatted(apiPrefix))
                .access(new WebExpressionAuthorizationManager("not isAnonymous()"))
                .requestMatchers(HttpMethod.PUT, "/%s/users/avatar".formatted(apiPrefix))
                .access(new WebExpressionAuthorizationManager("not isAnonymous()"))

                .requestMatchers(HttpMethod.POST, "/%s/apartments/".formatted(apiPrefix)).hasRole(ConstantConfig.ADMIN_ROLE)
                .requestMatchers(HttpMethod.POST, "/%s/apartments/rental/apartment/{apartmentId}/user/{userId}".formatted(apiPrefix))
                .access(new WebExpressionAuthorizationManager("not isAnonymous()"))
                .requestMatchers(HttpMethod.GET, "/%s/apartments/".formatted(apiPrefix)).permitAll()
                .requestMatchers(HttpMethod.GET, "/%s/users/resident".formatted(apiPrefix)).hasAuthority(ConstantConfig.PERMISSION_READ)

                .requestMatchers("/%s/waters/{id}".formatted(apiPrefix)).permitAll()
                .requestMatchers("/%s/waters/**".formatted(apiPrefix)).hasRole(ConstantConfig.ADMIN_ROLE)

                .requestMatchers("/%s/electrics/**".formatted(apiPrefix)).permitAll()

                .requestMatchers("/ws**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                .requestMatchers("/faker/**").permitAll()

                .anyRequest().authenticated());
        http.cors(cors -> corsFilter());
        http.oauth2ResourceServer(oauth2 ->
                oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint((httpServletRequest, httpServletResponse, authException) -> {
                            APIResponse response = APIResponse.builder()
                                    .code(HttpStatus.UNAUTHORIZED.value())
                                    .message(authException.getMessage())
                                    .build();
                            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(response));
                        }));
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            try {
                // check jwt exists in database
                if (!jwtGenerator.verify(token)) {
                    throw new DataInvalidException(ExceptionVariable.TOKEN_INVALID);
                }
                JWTClaimsSet jwtClaimsSet = jwtGenerator.getSignedJWT(token).getJWTClaimsSet();
                JwtEntity jwt = jwtRepository.findById(jwtClaimsSet.getJWTID())
                        .orElseThrow(() -> new DataInvalidException(ExceptionVariable.TOKEN_INVALID));
                UserEntity user = jwt.getUser();
                if (user.getStatus().equals(UserStatus.INACTIVE)) {
                    throw new DataInvalidException(ExceptionVariable.USER_LOCKED);
                }
                SecretKeySpec spec = new SecretKeySpec(signingKey.getBytes(), MacAlgorithm.HS512.getName());
                return NimbusJwtDecoder.withSecretKey(spec)
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build()
                        .decode(token);
            } catch (Exception e) {
                throw new DataInvalidException(ExceptionVariable.TOKEN_INVALID);
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3002", "http://localhost:3000", "http://127.0.0.1:5500", "http://localhost:5500"));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
