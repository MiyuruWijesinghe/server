package bit.project.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Salepayment {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;


    private BigDecimal pamount;

    private LocalDate date;

    @JsonIgnore
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Sale sale;

    public Salepayment(Integer id){
        this.id = id;
    }

}
