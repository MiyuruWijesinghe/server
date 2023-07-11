/*
 * Generated By Spring Data JPA Entity Generator
 * @author Niroshan Mendis
 */

package bit.project.server.entity;

import lombok.Data;

import java.util.List;
import javax.persistence.Id;
import javax.persistence.Entity;

import lombok.NoArgsConstructor;

import javax.persistence.OneToMany;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Civilstatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Name is required")
    @Size(min = 0, max = 20, message = "Maximum character count is 20")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "civilstatus")
    private List<Employee> employeeList;


    public Civilstatus(Integer id) {
        this.id = id;
    }


}
