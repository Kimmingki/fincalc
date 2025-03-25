package com.fincalc.service;

import com.fincalc.dto.saving.SavingRequestDTO;
import com.fincalc.dto.saving.SavingResponseDTO;
import com.fincalc.model.InterestType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class SavingCalculationService {

    public SavingResponseDTO calculateSaving(SavingRequestDTO request) {
        BigDecimal monthlyDeposit = request.getMonthlyDeposit();
        BigDecimal monthlyRate = request.getAnnualInterestRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        int n = request.getSavingPeriodMonths();
        InterestType interestType = request.getInterestType();

        BigDecimal finalAmount;
        BigDecimal totalInterest;

        if (interestType == InterestType.COMPOUND) {
            // 복리: 만기금액 = P * ((1 + r)^n-1) / r
            // 단, r이 0인 경우 처리 해야함
            if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
                finalAmount = monthlyDeposit.multiply(BigDecimal.valueOf(n));
            } else {
                BigDecimal factor = BigDecimal.ONE.add(monthlyRate)
                        .pow(n, new MathContext(10));
                finalAmount = monthlyDeposit.multiply(factor.subtract(BigDecimal.ONE))
                        .divide(monthlyRate, 0, RoundingMode.HALF_UP);
            }
            totalInterest = finalAmount.subtract(monthlyDeposit.multiply(BigDecimal.valueOf(n)));
        } else {
            // 단리: 만기금액 = P * r * (n(n+1)/2)
            BigDecimal nBig = BigDecimal.valueOf(n);
            BigDecimal sumOfPeriods = nBig.multiply(nBig.add(BigDecimal.ONE)).divide(BigDecimal.valueOf(2), 0, RoundingMode.HALF_UP);
            totalInterest = monthlyDeposit.multiply(monthlyRate).multiply(sumOfPeriods)
                    .setScale(0, RoundingMode.HALF_UP);
            finalAmount = monthlyDeposit.multiply(nBig).add(totalInterest);
        }

        totalInterest = totalInterest.setScale(0, RoundingMode.HALF_UP);
        finalAmount = finalAmount.setScale(0, RoundingMode.HALF_UP);

        return new SavingResponseDTO(totalInterest, finalAmount);
    }
}
