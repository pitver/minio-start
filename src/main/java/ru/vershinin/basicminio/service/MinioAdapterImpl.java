package ru.vershinin.basicminio.service;

import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MinioAdapterImpl implements MinioAdapterService {

    final MinioClient minioClient;

    @Value("${minio.buckek.name}")
    String defaultBucketName;

    @Value("${minio.default.folder}")
    String defaultBaseFolder;

    public MinioAdapterImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public List<String> getAllBuckets() {
        try {

            return minioClient.listBuckets()
                    .stream().map(bucket -> bucket.creationDate() + ", " + bucket.name())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    public void uploadFile(String name, MultipartFile files)  {


        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(files.getBytes());

            minioClient.putObject(PutObjectArgs.builder().bucket(defaultBucketName).object(name).stream(
                    bais, bais.available(), -1)
                    .contentType(files.getContentType())
                    .build());


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public void  uploadObject(String name, File file) throws Exception {
         minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket("test")
                    .object(name)
                    .filename(file.getAbsolutePath())
                    .build());
    }

    public byte[] getFile(String key) {
        try {
            InputStream stream =
                    minioClient.getObject(
                            GetObjectArgs.builder().bucket(defaultBucketName).object(key).build());

            byte[] content = IOUtils.toByteArray(stream);
            stream.close();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostConstruct
    public void init() {
    }

    public void checkBasket(String name,MultipartFile files) throws Exception {
        // Make 'test' bucket if not exist.
        System.out.println(files.getContentType());
        boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
        if (!found) {
            // Make a new bucket called 'test'.
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
        } else {
            System.out.println("Bucket "+ name+" already exists.");
        }
    }
}
