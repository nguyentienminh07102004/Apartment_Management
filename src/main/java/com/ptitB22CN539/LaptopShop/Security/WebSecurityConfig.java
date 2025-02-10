package com.ptitB22CN539.LaptopShop.Security;

import com.ptitB22CN539.LaptopShop.Config.ConstantConfig;
import com.ptitB22CN539.LaptopShop.Config.JwtGenerator;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import com.ptitB22CN539.LaptopShop.Repository.JwtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final JwtRepository jwtRepository;
    private final JwtGenerator jwtGenerator;
    @Value(value = "${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(request -> request
                .requestMatchers(HttpMethod.POST, "/%s/users/login/{social}".formatted(apiPrefix)).permitAll()
                .requestMatchers(HttpMethod.POST, "/%s/users/register".formatted(apiPrefix)).permitAll()
                .anyRequest().authenticated());
        http.cors(cors -> corsFilter());
        http.oauth2ResourceServer(oauth2 ->
                oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())));
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
                if (!jwtRepository.existsByToken(token)) {
                    throw new DataInvalidException(ExceptionVariable.TOKEN_INVALID);
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
