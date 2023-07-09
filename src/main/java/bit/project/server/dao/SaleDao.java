package bit.project.server.dao;

import bit.project.server.entity.Branch;
import bit.project.server.entity.Inventory;
import bit.project.server.entity.Item;
import bit.project.server.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.util.Optional;

@RepositoryRestResource(exported=false)

public interface SaleDao extends JpaRepository<Sale,Integer> {


    @Query("select count(c) from Sale c where c.date>=:startdate and c.date<=:enddate")
    Long getSaleCountByRange(@Param("startdate") LocalDate startdate, @Param("enddate") LocalDate enddate);

    @Query("select sum(s.amount) from Sale s where s.date>=:startdate and s.date<=:enddate")
    Long getSaleByRange(@Param("startdate") LocalDate startdate, @Param("enddate") LocalDate enddate);

    @Query(value = "SELECT sum(p.amount) FROM Sale p where year(p.date) = ? and month(p.date) = ?" , nativeQuery = true)
    Integer getMonthlySaleAmount(String year, String month);

    @Query(value = "SELECT sum(p.amount) FROM Sale p where month(p.date) = ? and day(p.date) = ?" , nativeQuery = true)
    Integer getDailySaleAmount(String year, String month);

    @Query(value = "SELECT sum(p.amount) FROM Sale p  where year(p.date) = ? and month(p.date) = ?" , nativeQuery = true)
    Integer getMonthlySaleitemCategoryAmount(String year, String month);

}
