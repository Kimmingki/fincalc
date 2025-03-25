package com.fincalc.dto.saving;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SavingResponseDTO {

    private BigDecimal totalInterest;           // 발생 이자
    private BigDecimal finalAmount;             // 만기 금액
}
