package com.ptitB22CN539.LaptopShop.Mapper.User;

import com.ptitB22CN539.LaptopShop.DTO.User.UserRegister;
import com.ptitB22CN539.LaptopShop.Domains.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings(value = {
            @Mapping(target = "permissions", ignore = true),
            @Mapping(target = "role", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "jwts", ignore = true),
            @Mapping(target = "status", ignore = true)
    })
    UserEntity registerToEntity(UserRegister userRegister);
}
