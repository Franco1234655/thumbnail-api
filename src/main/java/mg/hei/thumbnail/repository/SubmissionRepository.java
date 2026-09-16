package mg.hei.thumbnail.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import mg.hei.thumbnail.model.Submission;
import org.springframework.stereotype.Repository;

@Repository
public class SubmissionRepository {
  private final Map<String, Submission> store = new ConcurrentHashMap<>();

  public Submission save(Submission s) {
    store.put(s.getId(), s);
    return s;
  }

  public List<Submission> findAll() {
    return List.copyOf(store.values());
  }
}
