package com.xiepuxin.toutiao.service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.UploadFileRequest;
import com.xiepuxin.toutiao.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class AliyunService {
    private static final Logger logger = LoggerFactory.getLogger(AliyunService.class);

    // endpoint以杭州为例，其它region请按实际情况填写
    private String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，
    // 创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建
    private String accessKeyId = "LTAIUediJDQhH6J6";
    private String accessKeySecret = "BsTG4G7JO723Xjis7iitUitqdoaMf7";

    private static String IMAGE_DIR = "images/";

    //  bucket名字
    private String bucketName = "xiepuxin";

    public String saveImage(MultipartFile file) throws IOException {
        int doPos = file.getOriginalFilename().lastIndexOf(".");
        if (doPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(doPos + 1).toLowerCase();
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {
            return null;
        }

        String fileName = IMAGE_DIR + UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;

        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        //上传文件流
        ossClient.putObject(bucketName, fileName, file.getInputStream());

        // 关闭client
        ossClient.shutdown();

        return ToutiaoUtil.ALIYUN_DOMAIN_PREFIX + fileName;
    }

    public String saveImageWithBreakPoint(MultipartFile file) throws IOException {
        int doPos = file.getOriginalFilename().lastIndexOf(".");
        if (doPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(doPos + 1).toLowerCase();
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;

        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        // 设置断点续传请求
        UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, fileName);
        // 指定上传的本地文件
        uploadFileRequest.setUploadFile(file.getOriginalFilename());
        // 指定上传并发线程数
        uploadFileRequest.setTaskNum(5);
        // 指定上传的分片大小
        uploadFileRequest.setPartSize(1 * 1024 * 1024);
        // 开启断点续传
        uploadFileRequest.setEnableCheckpoint(true);
        // 断点续传上传
        try {
            ossClient.uploadFile(uploadFileRequest);
        }catch(Throwable e){
            logger.error("断点上传发生异常："+e.getMessage());
        }

        // 关闭client
        ossClient.shutdown();

        logger.info("断点上传成功，上传文件路径为：" + ToutiaoUtil.ALIYUN_DOMAIN_PREFIX + "images/" + fileName);

        return ToutiaoUtil.ALIYUN_DOMAIN_PREFIX + "images/" + fileName;
    }

    public String saveImageWithFile(File file) throws IOException {
        int doPos = file.getName().lastIndexOf(".");
        if (doPos < 0) {
            return null;
        }
        String fileExt = file.getName().substring(doPos + 1).toLowerCase();
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {
            return null;
        }

        String fileName = IMAGE_DIR + file.getName();

        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        //上传文件流
        InputStream inputStream = new FileInputStream(file);
        ossClient.putObject(bucketName, fileName, new FileInputStream(file));

        // 关闭client
        ossClient.shutdown();

        inputStream.close();

        return ToutiaoUtil.ALIYUN_DOMAIN_PREFIX + fileName;
    }
}
