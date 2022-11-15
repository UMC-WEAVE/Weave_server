package com.weave.weaveserver.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.StorageClient;
import com.weave.weaveserver.config.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FireBaseService {
    @Value("${app.firebase-bucket}")
    private String firebaseBucket;

    public String uploadFiles(String fileName, MultipartFile file) throws IOException, FirebaseAuthException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        InputStream content = new ByteArrayInputStream(file.getBytes());
        Blob blob = bucket.create(fileName.toString(), content, file.getContentType());
        //https://firebasestorage.googleapis.com/v0/b/wave-weave.appspot.com/o/test1?alt=media
        String img_url = "https://firebasestorage.googleapis.com/v0/b/wave-weave.appspot.com/o/"
                +fileName+"?alt=media";
        return img_url;
    }
    public void deleteFiles(String fileName){
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        System.out.println(bucket.getGeneratedId());
        try{
            boolean blob = bucket.getStorage().delete(firebaseBucket,fileName);
        }catch (Exception e){
            throw new NotFoundException("file 삭제 실패");
        }
    }
}
