package bit.project.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customertype {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message="Name is required")
    @Size(min=0, max=20, message="Maximum character count is 20")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy="customertype")
    private List<Customer> customerList;

    @JsonIgnore
    @OneToMany(mappedBy="customertype")
    private List<Inventorycustomertype> inventorycustomertypeList;


    public Customertype(Integer id){
        this.id = id;
    }
}
