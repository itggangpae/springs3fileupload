package com.adamsoft.fileupload;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3ServiceImpl implements AwsS3Service {
    private AmazonS3 amazonS3Client;

    //properties에서 값을 가지고 와서 설정
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    //생성자가 호출된 후에 수행할 메서드
    @PostConstruct
    public void setS3Client(){
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        amazonS3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
    }

    //업로드 할 파일 존재 여부를 리턴해주는 메서드
    private boolean validateFileExists(MultipartFile multipartFile) {
        boolean result = true;
        if(multipartFile.isEmpty()){
            result = false;
        }
        return result;
    }

    @Override
    public String uploadFile(String category, MultipartFile multipartFile) {
        boolean result = validateFileExists(multipartFile);
        if(result == false){
            return null;
        }
        //파일 경로 생성
        String fileName = CommonUtils.buildFileName(category, multipartFile.getOriginalFilename());
        //파일 업로드 준비
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        try(InputStream inputStream = multipartFile.getInputStream()){
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        }catch(IOException e){
            return null;
        }


        return "";
    }
}
