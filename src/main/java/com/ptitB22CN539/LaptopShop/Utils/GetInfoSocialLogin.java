package com.ptitB22CN539.LaptopShop.Utils;

import com.ptitB22CN539.LaptopShop.DTO.User.UserSocialLogin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GetInfoSocialLogin {
    @Value(value = "${google.clientId}")
    private String googleClientId;
    @Value(value = "${google.clientSecret}")
    private String googleClientSecret;
    @Value(value = "${discord.clientId}")
    private String discordClientId;
    @Value(value = "${discord.clientSecret}")
    private String discordClientSecret;
    public Map<String, UserSocialLogin> getInfoSocialLogin(String code) {
        Map<String, UserSocialLogin> properties = new HashMap<>();
        UserSocialLogin googleSocial = UserSocialLogin.builder()
                .code(code)
                .name("google")
                .redirectUri("http://localhost:3000/login?social=google")
                .clientId(this.googleClientId)
                .clientSecret(this.googleClientSecret)
                .AccessTokenUrl("https://www.googleapis.com/oauth2/v4/token")
                .UserInfoUrl("https://www.googleapis.com/oauth2/v3/userinfo")
                .userInfoMethod(HttpMethod.POST)
                .accessTokenMethod(HttpMethod.POST)
                .build();
        properties.put("google", googleSocial);
        UserSocialLogin discordSocialLogin = UserSocialLogin.builder()
                .code(code)
                .name("discord")
                .redirectUri("http://localhost:3000/login?social=discord")
                .clientId(this.discordClientId)
                .clientSecret(this.discordClientSecret)
                .AccessTokenUrl("https://discord.com/api/v10/oauth2/token")
                .UserInfoUrl("https://discord.com/api/v10/users/@me")
                .accessTokenMethod(HttpMethod.POST)
                .userInfoMethod(HttpMethod.GET)
                .build();
        properties.put("discord", discordSocialLogin);
        return properties;
    }
}
