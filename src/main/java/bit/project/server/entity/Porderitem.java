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
public class Porderitem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private BigDecimal qty;

    @JsonIgnore
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Porder porder;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Item item;

    public Porderitem(Integer id){
        this.id = id;
    }
}
