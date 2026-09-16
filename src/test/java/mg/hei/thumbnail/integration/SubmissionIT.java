package mg.hei.thumbnail.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import mg.hei.thumbnail.conf.FacadeIT;
import mg.hei.thumbnail.model.Submission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class SubmissionIT extends FacadeIT {

  @Autowired private TestRestTemplate rest;

  @Test
  void create_then_appears_in_list() throws Exception {
    try (InputStream is = getClass().getResourceAsStream("/test-image.png")) {
      byte[] bytes = is.readAllBytes();
      ByteArrayResource fileResource =
          new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
              return "test-image.png";
            }
          };

      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("file", fileResource);
      body.add("email", "test@example.com");

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);
      HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

      ResponseEntity<Submission> postResponse =
          rest.postForEntity("/submissions", request, Submission.class);

      assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(postResponse.getBody()).isNotNull();
      assertThat(postResponse.getBody().getEmail()).isEqualTo("test@example.com");

      ResponseEntity<Submission[]> listResponse =
          rest.getForEntity("/submissions", Submission[].class);

      assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(listResponse.getBody()).isNotEmpty();
    }
  }
}
