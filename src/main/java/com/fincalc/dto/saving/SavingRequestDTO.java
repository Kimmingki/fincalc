package com.fincalc.dto.saving;

import com.fincalc.model.InterestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingRequestDTO {

    private BigDecimal monthlyDeposit;          // 매월 납입액
    private BigDecimal annualInterestRate;      // 연이율
    private int savingPeriodMonths;             // 적금 기간
    private InterestType interestType;          // 단리 또는 복리
}
