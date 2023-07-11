package bit.project.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@NotNull(message="Code is required")
    @Size(min = 10, max = 10, message = "Character count should be 10")
    private String code;

    @Lob
    @Size(min = 0, max = 65535, message = "Maximum character count is 65535")
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime tocreation;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime todeletion;


    private BigDecimal amount;

    private BigDecimal taxamount;

    private BigDecimal totalamount;

    private LocalDate date;


    @ManyToOne(optional = false)
    private Branch branch;

    @ManyToOne(optional = false)
    private Porder porder;


    @OneToMany(mappedBy = "purchase", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Purchaseitem> purchaseitemList;


    @ManyToOne(optional = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User creator;

    @JsonIgnore
    @OneToMany(mappedBy = "purchase")
    private List<Inventory> inventoryList;


    public Purchase(Integer id) {
        this.id = id;
    }

}
