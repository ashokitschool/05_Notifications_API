package in.ashokit.ecomm.util;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;

@Service
public class S3Util {

	@Value("${aws.bucketName}")
	private String bucketName;
	
	@Value("${aws.region}")
	private String bucketRegion;

	private final AmazonS3 s3;

	public S3Util(AmazonS3 s3) {
		this.s3 = s3;
	}

	public String saveFileInBucket(File file) {
		try {
			PutObjectResult putObjectResult = s3.putObject(bucketName, file.getName(), file);
			System.out.println("Invoice "+ file.getName()+" is saved into s3 bucket");
			String invoiceUrl = String.format("https://s3.%s.amazonaws.com/%s/%s", bucketRegion, bucketName, file.getName());
			System.out.println("invoiceUrl "+invoiceUrl);
			System.out.println("getContentMd5 "+putObjectResult.getContentMd5());
			return invoiceUrl;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
