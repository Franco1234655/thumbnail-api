package mg.hei.thumbnail.bucket;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class BucketConf {
  @Getter private final String bucket;
  private final Region region;

  public BucketConf(
      @Value("${aws.s3.bucket}") String bucket,
      @Value("${aws.region:eu-west-3}") Region region) {
    this.bucket = bucket;
    this.region = region;
  }

  @Bean
  public S3Client s3Client() {
    return S3Client.builder().region(region).build();
  }
}
