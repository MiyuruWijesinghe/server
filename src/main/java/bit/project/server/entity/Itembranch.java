package bit.project.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.stream.FactoryConfigurationError;
import java.math.BigDecimal;


@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Itembranch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer rop;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Item item;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Branch branch;

    public Itembranch(Integer id) {
        this.id = id;
    }

}
