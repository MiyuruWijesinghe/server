package bit.project.server.dao;

import bit.project.server.entity.Complain;
import bit.project.server.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ComplainDao extends JpaRepository<Complain, Integer> {

    Complain findByNic(String nic);

    Complain findByContact(String contact);
}
