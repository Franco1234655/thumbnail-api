package mg.hei.thumbnail.model;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Submission {
  private final String id;
  private final String email;
  @Setter private String thumbnailKey;
  private final Instant createdAt;

  public Submission(String email) {
    this.id = UUID.randomUUID().toString();
    this.email = email;
    this.thumbnailKey = null;
    this.createdAt = Instant.now();
  }
}
