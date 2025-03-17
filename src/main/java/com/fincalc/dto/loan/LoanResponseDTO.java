package com.fincalc.dto.loan;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class LoanResponseDTO {

    private List<BigDecimal> monthlyPayments;           // 매월 상환 금액 목록
    private BigDecimal totalInterest;                   // 총 이자 금액
    private BigDecimal totalPayment;                    // 총 상환 금액 (원금 + 이자)
}
