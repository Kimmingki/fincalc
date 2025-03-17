package com.fincalc.dto.deposit;

import com.fincalc.model.InterestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequestDTO {

    private BigDecimal depositAmount;           // 예금 금액
    private BigDecimal annualInterestRate;      // 연이율
    private int depositPeriod;                  // 예금 기간
    private InterestType interestType;          // 단리 또는 복리
}
