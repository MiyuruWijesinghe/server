package bit.project.server.dao;


import bit.project.server.entity.Branch;
import bit.project.server.entity.Porder;
import bit.project.server.entity.Porderstatus;
import bit.project.server.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface PorderDao extends JpaRepository<Porder, Integer> {

    Porder findByPorderstatus(Porderstatus porderstatus);

}
