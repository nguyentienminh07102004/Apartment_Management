package com.ptitB22CN539.LaptopShop.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.ptitB22CN539.LaptopShop.Config.ConstantConfig;
import com.ptitB22CN539.LaptopShop.Config.JwtGenerator;
import com.ptitB22CN539.LaptopShop.DTO.APIResponse;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import com.ptitB22CN539.LaptopShop.Redis.Repository.JwtRedisRepository;
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

@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Value(value = "${signerKey}")
    private String signingKey;
    private final JwtRedisRepository jwtRedisRepository;
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
                .requestMatchers(HttpMethod.POST, "/%s/users/logout".formatted(apiPrefix))
                .access(new WebExpressionAuthorizationManager("not isAnonymous()"))
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
                if (!jwtRedisRepository.hasExists(jwtClaimsSet.getSubject(), jwtClaimsSet.getJWTID())) {
                    throw new DataInvalidException(ExceptionVariable.TOKEN_INVALID);
                }
                SecretKeySpec spec = new SecretKeySpec(signingKey.getBytes(), MacAlgorithm.HS512.getName());
                return NimbusJwtDecoder.withSecretKey(spec)
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build()
                        .decode(token);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new DataInvalidException(ExceptionVariable.TOKEN_INVALID);
            }
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(ConstantConfig.ROLE_PREFIX);
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000/");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
