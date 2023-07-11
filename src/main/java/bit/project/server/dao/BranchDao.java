package bit.project.server.dao;

import bit.project.server.entity.Branch;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface BranchDao extends JpaRepository<Branch, Integer> {

    Branch findByName(String name);

    Branch findByContact1(String conatact1);
}
