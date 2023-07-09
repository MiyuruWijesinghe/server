package bit.project.server.entity;

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

@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Complain {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    //@NotNull(message="Code is required")
    @Size(min=10, max=10 , message="Character count should be 8")
    private String code;

    private String name;

    private LocalDate date;

    @NotNull(message="NIC is required")
    @Size(min=0, max=12, message="Maximum character count is 12")
    private String nic;

    @Lob
    @Size(min=0, max=65535, message="Maximum character count is 65535")
    private String address;

    @NotNull(message="contact1 is required")
    @Size(min=10, max=10, message="Character count should be 10")
    private String contact;

    @Lob
    @Size(min=0, max=65535, message="Maximum character count is 65535")
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime tocreation;

    @ManyToOne(optional=false)
    private Item item;


    @ManyToOne(optional=false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User creator;


    public Complain(Integer id){
        this.id = id;
    }
}

