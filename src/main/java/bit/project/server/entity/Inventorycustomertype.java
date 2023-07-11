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
public class Inventorycustomertype {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private BigDecimal price;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Inventory inventory;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Customertype customertype;

    public Inventorycustomertype(Integer id) {
        this.id = id;
    }
}
