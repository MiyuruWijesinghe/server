/*
 * Generated By Spring Data JPA Entity Generator
 * @author Niroshan Mendis
 */

package bit.project.server.entity;

import lombok.Data;

import java.util.List;
import javax.persistence.*;

import lombok.NoArgsConstructor;

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
public class Gender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Name is required")
    @Size(min = 0, max = 10, message = "Maximum character count is 10")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "gender")
    private List<Employee> employeeList;


    public Gender(Integer id) {
        this.id = id;
    }


}
