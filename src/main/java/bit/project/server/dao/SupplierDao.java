package bit.project.server.dao;

import bit.project.server.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierDao extends JpaRepository<Supplier,Integer> {



    Supplier findByName(String name);
    Supplier findByContact1(String conatact1);



}
