package com.fincalc.service;

import com.fincalc.dto.loan.LoanRequestDTO;
import com.fincalc.dto.loan.LoanResponseDTO;
import com.fincalc.model.RepaymentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
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

        // 입력값 검증
        if (loanAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("대출 금액은 0보다 커야 합니다.");
        }
        if (annualInterestRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("연이율은 0보다 커야 합니다.");
        }
        if (loanTermMonths <= 0) {
            throw new IllegalArgumentException("대출 기간(개월)은 1 이상이어야 합니다.");
        }

        List<BigDecimal> monthlyPayments = new ArrayList<>();
        BigDecimal totalInterest = BigDecimal.ZERO;
        BigDecimal totalPayment = BigDecimal.ZERO;

        switch (repaymentType) {
            case EQUAL_INSTALLMENT -> {
                BigDecimal monthlyPayment = getBigDecimal(annualInterestRate, loanTermMonths, loanAmount);

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
                    monthlyPayment = monthlyPayment.setScale(0, RoundingMode.HALF_UP);

                    monthlyPayments.add(monthlyPayment);
                    remainingLoan = remainingLoan.subtract(monthlyPrincipal);
                    totalInterest = totalInterest.add(monthlyInterest);
                }

                totalPayment = loanAmount.add(totalInterest);
            }
            case BULLET_PAYMENT -> {
                BigDecimal monthlyInterest = loanAmount.multiply(annualInterestRate).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
                monthlyInterest = monthlyInterest.setScale(0, RoundingMode.HALF_UP);

                for (int i = 0; i < loanTermMonths; i++) {
                    monthlyPayments.add(monthlyInterest);
                    totalInterest = totalInterest.add(monthlyInterest);
                }
                totalPayment = loanAmount.add(totalInterest);
            }
        }

        // 총 이자, 총 상환 금액 정수 변환
        totalInterest = totalInterest.setScale(0, RoundingMode.HALF_UP);
        totalPayment = totalPayment.setScale(0, RoundingMode.HALF_UP);

        return new LoanResponseDTO(monthlyPayments, totalInterest, totalPayment);
    }

    private static BigDecimal getBigDecimal(BigDecimal annualInterestRate, int loanTermMonths, BigDecimal loanAmount) {
        BigDecimal monthlyRate = annualInterestRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal powerFactor = BigDecimal.ONE.add(monthlyRate).pow(loanTermMonths, new MathContext(10));
        BigDecimal denominator = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(powerFactor, 10, RoundingMode.HALF_UP));

        BigDecimal monthlyPayment = loanAmount.multiply(monthlyRate).divide(denominator, 2, RoundingMode.HALF_UP);
        monthlyPayment = monthlyPayment.setScale(0, RoundingMode.HALF_UP);
        return monthlyPayment;
    }
}
