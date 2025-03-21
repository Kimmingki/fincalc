package com.fincalc.service;

import com.fincalc.dto.deposit.DepositRequestDTO;
import com.fincalc.dto.deposit.DepositResponseDTO;
import com.fincalc.model.InterestType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class DepositCalculationService {

    public DepositResponseDTO calculateDeposit(DepositRequestDTO depositRequestDTO) {
        BigDecimal depositAmount = depositRequestDTO.getDepositAmount();
        BigDecimal annualInterestRate = depositRequestDTO.getAnnualInterestRate().divide(BigDecimal.valueOf(100), 10, BigDecimal.ROUND_HALF_UP);
        int depositPeriod = depositRequestDTO.getDepositPeriod();
        InterestType interestType = depositRequestDTO.getInterestType();

        BigDecimal interestEarned;
        BigDecimal finalAmount;

        if (interestType == InterestType.SIMPLE) {
            // 단리: 원금 * (연이율) * 기간
            interestEarned = depositAmount.multiply(annualInterestRate).multiply(BigDecimal.valueOf(depositPeriod));
            finalAmount = depositAmount.add(interestEarned);
        } else {
            // 복리: 원금 * (1 + 연이율)^기간
            BigDecimal factor = BigDecimal.ONE.add(annualInterestRate).pow(depositPeriod, new MathContext(10));
            finalAmount = depositAmount.multiply(factor).setScale(2, RoundingMode.HALF_UP);
            interestEarned = finalAmount.subtract(depositAmount);
        }

        // 결과 값을 정수로 표기
        interestEarned = interestEarned.setScale(0, RoundingMode.HALF_UP);
        finalAmount = finalAmount.setScale(0, RoundingMode.HALF_UP);

        return new DepositResponseDTO(interestEarned, finalAmount);
    }
}
