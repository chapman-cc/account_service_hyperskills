package account.repositories;

import account.models.LoginInformation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;

public interface LoginInformationRepository extends ListCrudRepository<LoginInformation, Long> {
}
