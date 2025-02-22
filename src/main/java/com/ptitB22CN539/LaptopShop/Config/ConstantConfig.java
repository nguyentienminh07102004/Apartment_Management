package com.ptitB22CN539.LaptopShop.Config;

import java.util.Arrays;
import java.util.List;

public class ConstantConfig {
    public static final String NAME = "PTITB22CN539";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String AUTHORIZATION_PREFIX = "Bearer ";
    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String DEFAULT_PASSWORD = "123456789";
    public static final String PERMISSION_READ = "READ";
    public static final String PERMISSION_BUY = "BUY";
    public static final List<String> PERMISSION_DEFAULT_USER = Arrays.asList(PERMISSION_READ, PERMISSION_BUY);
}
