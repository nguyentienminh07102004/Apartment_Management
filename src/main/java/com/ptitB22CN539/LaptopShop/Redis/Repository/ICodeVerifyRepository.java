package com.ptitB22CN539.LaptopShop.Redis.Repository;

import com.ptitB22CN539.LaptopShop.Redis.Entity.CodeVerifyChangePassword;

public interface ICodeVerifyRepository {
    void setCodeVerify(CodeVerifyChangePassword codeVerifyChangePassword);
    CodeVerifyChangePassword getCodeVerify(String email);
}
