package com.fincalc.service;

import com.fincalc.dto.LoanRequestDTO;
import com.fincalc.dto.LoanResponseDTO;
import com.fincalc.model.RepaymentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LoanCalculationService {

    public LoanResponseDTO calculateLoan(LoanRequestDTO loanRequestDTO) {
        log.info("대출 계산 요청: {}", loanRequestDTO);

        BigDecimal loanAmount = loanRequestDTO.getLoanAmount();
        BigDecimal annualInterestRate = loanRequestDTO.getAnnualInterestRate().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        int loanTermMonths = loanRequestDTO.getLoanTermMonths();
        RepaymentType repaymentType = loanRequestDTO.getRepaymentType();

        List<BigDecimal> monthlyPayments = new ArrayList<>();
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPayment = BigDecimal.ZERO;

        switch (repaymentType) {
            case EQUAL_INSTALLMENT -> {
                BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
                BigDecimal numerator = monthlyRate.multiply(loanAmount);
                BigDecimal denominator = BigDecimal.ONE.subtract(BigDecimal.ONE.add(monthlyRate).pow(-loanTermMonths));
                BigDecimal monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);

                for (int i = 0; i < loanTermMonths; i++) {
                    monthlyPayments.add(monthlyPayment);
                }

                totalPayment = monthlyPayment.multiply(BigDecimal.valueOf(loanTermMonths));
                totalInterest = totalPayment.subtract(loanAmount);
            }
            case EQUAL_PRINCIPAL -> {
                BigDecimal monthlyPrincipal = loanAmount.divide(BigDecimal.valueOf(loanTermMonths), 2, RoundingMode.HALF_UP);
                BigDecimal remainingLoan = loanAmount;

                for (int i = 0; i < loanTermMonths; i++) {
                    BigDecimal monthlyInterest = remainingLoan.multiply(annualInterestRate).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
                    BigDecimal monthlyPayment = monthlyPrincipal.add(monthlyInterest);
                    monthlyPayments.add(monthlyPayment);
                    remainingLoan = remainingLoan.subtract(monthlyPrincipal);
                    totalInterest = totalInterest.add(monthlyInterest);
                }

                totalPayment = loanAmount.add(totalInterest);
            }
            case BULLET_PAYMENT -> {
                BigDecimal monthlyInterest = loanAmount.multiply(annualInterestRate).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
                for (int i = 0; i < loanTermMonths; i++) {
                    monthlyPayments.add(monthlyInterest);
                    totalInterest = totalInterest.add(monthlyInterest);
                }
                totalPayment = loanAmount.add(totalInterest);
            }
        }

        return new LoanResponseDTO(monthlyPayments, totalInterest, totalPayment);
    }
}
