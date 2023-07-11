package bit.project.server.dao;


import bit.project.server.entity.Item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface ItemDao extends JpaRepository<Item, Integer> {

    @Query("select new Item(i.id, i.name) from Item i")
    Page<Item> findAllBasic(PageRequest pageRequest);

    @Query("select i  from Item i where i.itemstatus.id = 1")
    List<Item> findAllActiveitems();


    Item findByName(String name);


}
