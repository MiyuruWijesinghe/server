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
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@NotNull(message="Code is required")
    @Size(min = 10, max = 10, message = "Character count should be 10")
    private String code;

    @NotNull(message = "name is required")
    @Size(min = 0, max = 255, message = "Maximum character count is 255")
    private String name;


    @Size(min = 0, max = 12, message = "Maximum character count is 12")
    private String nic;

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

    private LocalDate regdate;


    @Size(min = 12, max = 12, message = "Character count should be 10")
    private String passport;

    @Size(min = 10, max = 10, message = "Character count should be 10")
    private String fax;

    @Size(min = 0, max = 255, message = "Maximum character count is 255")
    private String email;

    @ManyToOne(optional = false)
    private Customertype customertype;

    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private List<Sale> saleList;


    @ManyToOne(optional = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User creator;


    public Customer(Integer id) {
        this.id = id;
    }
}
