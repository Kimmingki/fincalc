package com.fincalc.dto.deposit;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DepositResponseDTO {

    private BigDecimal interestEarned;          // 발생 이자
    private BigDecimal finalAmount;             // 만기 금액 (원금 + 이자)
}
