package com.ptitB22CN539.LaptopShop.DTO.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSocialLogin {
    private String name;
    private String code;
    private String AccessTokenUrl;
    private String clientId;
    private String clientSecret;
    private HttpMethod userInfoMethod;
    private HttpMethod accessTokenMethod;
    private String redirectUri;
    private String UserInfoUrl;
}
