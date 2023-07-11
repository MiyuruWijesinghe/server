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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@NotNull(message="Code is required")
    @Size(min = 10, max = 10, message = "Character count should be 10")
    private String code;

    @NotNull(message = "name is required")
    @Size(min = 0, max = 255, message = "Maximum character count is 255")
    private String name;

    @Lob
    @Size(min = 0, max = 65535, message = "Maximum character count is 65535")
    private String address;


    @NotNull(message = "contact1 is required")
    @Size(min = 10, max = 10, message = "Character count should be 10")
    private String contact1;

    @Size(min = 10, max = 10, message = "Character count should be 10")
    private String contact2;


    @Lob
    @Size(min = 0, max = 65535, message = "Maximum character count is 65535")
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime tocreation;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime todeletion;

    private LocalDate dorecruite;

    @Size(min = 10, max = 10, message = "Character count should be 10")
    private String fax;

    @NotNull(message = "Email should not be null")
    @Size(min = 0, max = 255, message = "Maximum character count is 255")
    private String email;


    @ManyToOne(optional = false)
    private Branchstatus branchstatus;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    private List<Employee> employeeList;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    private List<Porder> porderList;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    private List<Inventory> inventoryList;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    private List<Purchase> purchaseList;

    @JsonIgnore
    @OneToMany(mappedBy = "branch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Itembranch> itembranchList;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    private List<Sale> saleList;

    @ManyToOne(optional = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User creator;


    public Branch(Integer id) {
        this.id = id;
    }


}
