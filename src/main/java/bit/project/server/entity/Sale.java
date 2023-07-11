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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 10, max = 10, message = "Character count should be 8")
    private String code;


    @Lob
    @Size(min = 0, max = 65535, message = "Maximum character count is 65535")
    private String description;

    @NotNull(message = "amount should not be null")
    private BigDecimal amount;

    private BigDecimal total;


    private BigDecimal discount;

    private LocalDate date;


    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime tocreation;


    @ManyToOne(optional = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User creator;


    @ManyToOne(optional = false)
    private Branch branch;


    @ManyToOne(optional = false)
    private Customer customer;


    @OneToMany(mappedBy = "sale", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Saleitem> saleitemList;

/*
    @OneToMany(mappedBy = "sale", fetch = FetchType.LAZY, orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Salepayment> salepaymentList;*/


    public Sale(Integer id) {
        this.id = id;
    }


}
