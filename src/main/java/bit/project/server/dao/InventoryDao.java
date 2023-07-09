package bit.project.server.dao;

import bit.project.server.entity.Branch;
import bit.project.server.entity.Inventory;
import bit.project.server.entity.Item;
import bit.project.server.entity.Saleitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(exported=false)
public interface InventoryDao extends JpaRepository<Inventory,Integer> {

    Optional<Inventory>findByItemAndBranch(Item item,Branch branch);




}
