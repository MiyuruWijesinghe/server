package bit.project.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Purchaseitem {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private Integer qty;

    private BigDecimal unitprice;

    private String batchno;

    private LocalDate doexpired;

    private LocalDate domanufactured;

    @JsonIgnore
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Purchase purchase;

    @ManyToOne(optional = false)
    private Item item;

    public Purchaseitem(Integer id){
        this.id = id;
    }
}
