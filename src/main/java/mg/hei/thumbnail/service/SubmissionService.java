package mg.hei.thumbnail.service;

import jakarta.mail.internet.InternetAddress;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import mg.hei.thumbnail.file.bucket.BucketComponent;
import mg.hei.thumbnail.mail.Email;
import mg.hei.thumbnail.mail.Mailer;
import mg.hei.thumbnail.model.Submission;
import mg.hei.thumbnail.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SubmissionService {
  static {
    System.setProperty("java.awt.headless", "true");
  }

  private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

  private final SubmissionRepository repository;
  private final BucketComponent bucketComponent;
  private final Mailer mailer;

  public Submission create(MultipartFile file, String email) {
    Submission submission = new Submission(email);
    repository.save(submission);
    byte[] bytes;
    try {
      bytes = file.getBytes();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    EXECUTOR.submit(() -> process(submission, bytes));
    return submission;
  }

  public List<Submission> findAll() {
    return repository.findAll();
  }

  private void process(Submission submission, byte[] originalBytes) {
    try {
      File resizedFile = resizeToTempFile(originalBytes);
      String key = "thumbnails/" + submission.getId() + ".png";
      bucketComponent.upload(resizedFile, key);
      resizedFile.delete();

      submission.setThumbnailKey(key);
      repository.save(submission);

      String url = bucketComponent.presign(key, Duration.ofDays(7)).toString();
      mailer.accept(
              new Email(
                      new InternetAddress(submission.getEmail()),
                      List.of(),
                      List.of(),
                      "Votre vignette est prete",
                      "<p>Votre vignette est disponible ici : <a href=\"" + url + "\">" + url + "</a></p>",
                      List.of()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private File resizeToTempFile(byte[] originalBytes) throws IOException {
    BufferedImage original = ImageIO.read(new ByteArrayInputStream(originalBytes));
    BufferedImage resized = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = resized.createGraphics();
    g.drawImage(original, 0, 0, 256, 256, null);
    g.dispose();
    File tempFile = File.createTempFile("thumbnail-", ".png");
    ImageIO.write(resized, "png", tempFile);
    return tempFile;
  }
}