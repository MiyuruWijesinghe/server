/*
 * Generated By Spring Data JPA Entity Generator
 * @author Niroshan Mendis
 */

package bit.project.server.dao;

import bit.project.server.entity.Role;
import bit.project.server.util.jpasupplement.UpdateSupplement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface RoleDao extends JpaRepository<Role, Integer>, UpdateSupplement<Role> {
    @Query("select new Role(r.id, r.name) from Role r")
    Page<Role> findAllBasic(PageRequest pageRequest);

    Role findByName(String name);
}
