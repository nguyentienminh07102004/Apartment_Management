package com.ptitB22CN539.LaptopShop.Utils;

import com.ptitB22CN539.LaptopShop.Config.ConstantConfig;
import com.ptitB22CN539.LaptopShop.Domains.PermissionEntity;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity;

public class BuildScope {
    public static String buildScope(UserEntity user) {
        StringBuilder result = new StringBuilder();
        result.append(ConstantConfig.ROLE_PREFIX).append(user.getRole().getName()).append(" ");
        for (PermissionEntity permission : user.getPermissions()) {
            result.append(permission.getName()).append(" ");
        }
        return result.toString().strip();
    }
}
