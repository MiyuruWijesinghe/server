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
public class Porder {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    //@NotNull(message="Code is required")
    @Size(min=10, max=10 , message="Character count should be 10")
    private String code;

    @Lob
    @Size(min=0, max=65535, message="Maximum character count is 65535")
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime tocreation;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime todeletion;

    private LocalDateTime doordered;

    private LocalDate dorequired;

    private LocalDate dorecieved;

    @ManyToOne(optional=false)
    private Porderstatus porderstatus ;

    @ManyToOne(optional=false)
    private Branch branch ;

    @ManyToOne(optional=false)
    private Supplier supplier ;

    @JsonIgnore
    @OneToMany(mappedBy="porder")
    private List<Purchase> purchaseList;

    @OneToMany(mappedBy = "porder", fetch = FetchType.EAGER, orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Porderitem> porderitemList;


    @ManyToOne(optional=false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User creator;


    public Porder(Integer id){
        this.id = id;
    }
}
