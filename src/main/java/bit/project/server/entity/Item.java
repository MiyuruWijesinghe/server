package bit.project.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
public class Item {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Size(min=10, max=10, message="Character count should be 8")
    private String code;

    @Size(min=0, max=255, message="Maximum character count is 255")
    private String name;


    @Lob
    @Size(min=0, max=65535, message="Maximum character count is 65535")
    private String description;

    @NotNull(message = "last price should not be null")
    private BigDecimal lastprice;

    @Lob
    private byte[] photo;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime tocreation;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime todeletion;


    @ManyToOne(optional=false)
    private Itemstatus itemstatus;

    @ManyToOne(optional=false)
    private Itemtype itemtype;

    @ManyToOne(optional=false)
    private Itemcategory itemcategory;

    @ManyToOne(optional=false)
    private Unit unit;

    @JsonIgnore
    @OneToMany(mappedBy="item")
    private List<Inventory> inventoryList;

    @JsonIgnore
    @OneToMany(mappedBy="item")
    private List<Saleitem> saleitemList;

    @JsonIgnore
    @OneToMany(mappedBy="item")
    private List<Porderitem> porderitemList;

   /* @JsonIgnore
    @OneToMany(mappedBy="item")
    private List<Saleitem> saleitemList;*/

    @JsonIgnore
    @OneToMany(mappedBy="item")
    private List<Purchaseitem> purchaseitemList;


    @ManyToOne(optional=false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User creator;

    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER, orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Itembranch> itembranchList;




    @JsonIgnore
    @ManyToMany(mappedBy="itemList")
    private List<Supplier> supplierList;

    @JsonIgnore
    @OneToMany(mappedBy="item")
    private List<Complain> complainList;

    public Item(Integer id){
        this.id = id;
    }
    public Item(Integer id, String name){
        this.id = id;
        this.name = name;
    }


}
