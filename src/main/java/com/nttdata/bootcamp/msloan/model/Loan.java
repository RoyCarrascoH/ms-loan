package com.nttdata.bootcamp.msloan.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Document(collection = "Loan")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Loan {

    @Id
    private String idLoan;

    private String idClient;

    @NotNull(message = "no debe estar nulo")
    private Integer loanNumber;

    @NotEmpty(message = "no debe estar vacío")
    private String loanType;

    @NotNull(message = "no debe estar nulo")
    private Double loanAmount;

    @NotEmpty(message = "no debe estar vacío")
    private String currency;

    @NotNull(message = "no debe estar nulo")
    private Integer numberQuotas;

    private String status;

}
