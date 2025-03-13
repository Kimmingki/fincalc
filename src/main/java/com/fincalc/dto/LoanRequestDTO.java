package com.fincalc.dto;

import com.fincalc.model.RepaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {

    private BigDecimal loanAmount;
    private BigDecimal annualInterestRate;
    private int loanTermMonths;
    private RepaymentType repaymentType;
}
