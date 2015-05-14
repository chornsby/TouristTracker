package com.chornsby.touristtracker.submit;

import android.content.Context;
import android.util.Log;

import com.chornsby.touristtracker.Secrets;
import com.chornsby.touristtracker.Utility;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class FileUploader {

    private static final String LOG_TAG = FileUploader.class.getSimpleName();

    private static final String APPLICATION_NAME = Secrets.APPLICATION_NAME;
    private static final String BUCKET_NAME = Secrets.BUCKET_NAME;
    private static final String SERVICE_ACCOUNT_EMAIL = Secrets.SERVICE_ACCOUNT_EMAIL;

    public static boolean tryUploadFile(Context context, File file, String userEmail) {
        try {
            uploadFile(context, file, userEmail);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Problem uploading file");
            return false;
        }
        return true;
    }

    public static void uploadFile(Context context, File file, String userEmail) throws GeneralSecurityException, IOException {
        Storage storage = getStorage(context);

        InputStream inputStream = new FileInputStream(file);
        InputStreamContent mediaContent = new InputStreamContent("application/octet-stream", inputStream);

        Storage.Objects.Insert insertObject = storage.objects()
                .insert(BUCKET_NAME, null, mediaContent)
                .setName(userEmail + "/" + file.getName());
        insertObject.getMediaHttpUploader().setDisableGZipContent(true);
        insertObject.execute();
    }

    private static Storage getStorage(Context context) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                .setServiceAccountPrivateKeyFromP12File(Utility.getKeyFile(context))
                .setServiceAccountScopes(Collections.singleton(StorageScopes.DEVSTORAGE_READ_WRITE))
                .build();

        return new Storage.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
