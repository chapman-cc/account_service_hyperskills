package account.repositories;

import account.models.SecurityEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

public interface SecurityEventRepository extends ListCrudRepository<SecurityEvent, Long> {
}
