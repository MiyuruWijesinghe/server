package bit.project.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;


@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Saleitem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer qty;


    private BigDecimal unitprice;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Sale sale;


    @ManyToOne(optional = false)
    private Inventory inventory;

    @ManyToOne(optional = false)
    private Item item;


    public Saleitem(Integer id) {
        this.id = id;
    }


}
