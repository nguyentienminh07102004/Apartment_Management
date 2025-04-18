package com.ptitB22CN539.LaptopShop.Service.Upload;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Collections;

@Service
public class UploadService implements IUploadService {
    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final String SERVICE_ACCOUNT_KEY_PATH = getServiceAccountKey();

    public String getServiceAccountKey() {
        String currentDirectory = System.getProperty("user.dir");
        return Paths.get(currentDirectory, "Cred.json").toString();
    }

    @Override
    @Transactional
    public String upload(java.io.File file) {
        try {
            String folderId = "1DA6aPT2CuHZ-yrmr6O1UY4cjujmd5llE";
            Drive drive = createDriveServiceAccount();
            File fileToUpload = new File();
            fileToUpload.setName(file.getName());
            fileToUpload.setParents(Collections.singletonList(folderId));
            FileContent fileContent = new FileContent("image/**", file);
            File uploadedFile = drive
                    .files()
                    .create(fileToUpload, fileContent)
                    .setFields("id")
                    .execute();
            return uploadedFile.getId();
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    @Transactional
    public void delete(String fileId) {
        try {
            Drive drive = createDriveServiceAccount();
            drive.files().delete(fileId).setFields("id").execute();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private Drive createDriveServiceAccount() throws IOException {
        return new Drive.Builder(
                new NetHttpTransport(),
                JSON_FACTORY,
                getServiceAccountCredentials())
                .build();
    }

    private Credential getServiceAccountCredentials() throws IOException {
        InputStream inputStream = new FileInputStream(SERVICE_ACCOUNT_KEY_PATH);
        GoogleClientSecrets googleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));
        GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), JSON_FACTORY, googleClientSecrets, DriveScopes.all())
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        LocalServerReceiver localServerReceiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(googleAuthorizationCodeFlow, localServerReceiver).authorize("user");
    }
}
