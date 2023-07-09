package bit.project.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Inventory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Size(min=10, max=10 , message="Character count should be 10")
    private String code;

    private LocalDate doexpired;

    private LocalDate domanufactured;

    @Positive
    private Integer initqty;
    @Positive
    private Integer qty;

    private String batchno;

    @ManyToOne(optional=false)
     Branch branch ;

    @ManyToOne(optional=false)
    private Item item ;

    @ManyToOne
    private Purchase purchase;


    @OneToMany(mappedBy = "inventory", fetch = FetchType.LAZY, orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Inventorycustomertype> inventorycustomertypeList;


    @JsonIgnore
    @OneToMany(mappedBy="inventory")
    private List<Saleitem> saleitemList;



    @ManyToOne(optional=false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User creator;




    public Inventory (Integer id){
        this.id = id;
    }

}
