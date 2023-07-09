package bit.project.server.dao;

import bit.project.server.entity.Porder;
import bit.project.server.entity.Porderstatus;
import bit.project.server.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;

@RepositoryRestResource(exported=false)
public interface PurchaseDao extends JpaRepository<Purchase,Integer> {

    @Query("select sum(p.amount) from Purchase p where p.date>=:startdate and p.date<=:enddate")
    Long getPurchaseByRange(@Param("startdate") LocalDate startdate, @Param("enddate") LocalDate enddate);

}
