package com.ptitB22CN539.LaptopShop.Service.Upload;

import java.io.File;

public interface IUploadService {
    String upload(File file);
    void delete(String fileId);
}
