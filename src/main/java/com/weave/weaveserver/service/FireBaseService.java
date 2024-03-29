package com.weave.weaveserver.service;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.StorageClient;
import com.weave.weaveserver.config.exception.NotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
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

    //archive : file name + random code
    //team : team + teamIdx
    public String uploadFiles(String boardType, String fileName, MultipartFile file) throws IOException, FirebaseAuthException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        InputStream content = new ByteArrayInputStream(file.getBytes());

        String img_url;
        if(boardType.equals("archive")){
            String randomCode = randomCode();
            Blob blob = bucket.create(fileName.toString()+"/"+randomCode, content, file.getContentType());
            //https://firebasestorage.googleapis.com/v0/b/wave-weave.appspot.com/o/test1%2FBfIzrG?alt=media
            img_url = "https://firebasestorage.googleapis.com/v0/b/wave-weave.appspot.com/o/"
                    +fileName.toString()+"%2F"+randomCode+"?alt=media";
        }else{
            Blob blob = bucket.create(fileName.toString(), content, file.getContentType());
            //https://firebasestorage.googleapis.com/v0/b/wave-weave.appspot.com/o/test1?alt=media
            img_url = "https://firebasestorage.googleapis.com/v0/b/wave-weave.appspot.com/o/"
                    +fileName+"?alt=media";
        }

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

    public String randomCode(){
        return RandomStringUtils.random(6,33,125,true,false);
    }
}
