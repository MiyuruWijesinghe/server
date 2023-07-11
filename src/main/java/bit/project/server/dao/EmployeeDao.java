/*
 * Generated By Spring Data JPA Entity Generator
 * @author Niroshan Mendis
 */

package bit.project.server.dao;

import bit.project.server.entity.Branch;
import bit.project.server.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface EmployeeDao extends JpaRepository<Employee, Integer> {
    @Query("select new Employee (e.id, e.code, e.callingname, e.nametitle) from Employee e")
    Page<Employee> findAllBasic(PageRequest pageRequest);

    Employee findByNic(String nic);

    Employee findByMobile(String mobile);

    List<Employee> findAllByBranch(Branch branch);
}
