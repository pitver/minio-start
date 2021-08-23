package ru.vershinin.basicminio.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface MinioAdapterService {

    List<String> getAllBuckets();

    void uploadFile(String name, MultipartFile files);

    byte[] getFile(String key);

    void checkBasket(String name,MultipartFile files) throws Exception;

    void  uploadObject(String name, File file) throws Exception;




}
