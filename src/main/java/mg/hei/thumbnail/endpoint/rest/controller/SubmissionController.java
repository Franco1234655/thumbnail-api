package mg.hei.thumbnail.endpoint.rest.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mg.hei.thumbnail.model.Submission;
import mg.hei.thumbnail.service.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {
  private final SubmissionService service;

  @PostMapping(consumes = "multipart/form-data")
  public ResponseEntity<Submission> create(
      @RequestParam("file") MultipartFile file, @RequestParam("email") String email) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.create(file, email));
  }

  @GetMapping
  public List<Submission> list() {
    return service.findAll();
  }
}
