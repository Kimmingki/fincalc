package com.fincalc.service;

import com.fincalc.dto.deposit.DepositRequestDTO;
import com.fincalc.dto.deposit.DepositResponseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DepositCalculationService {

    public DepositResponseDTO calculateDeposit(DepositRequestDTO depositRequestDTO) {
        BigDecimal depositAmount = depositRequestDTO.getDepositAmount();
        BigDecimal annualInterestRate = depositRequestDTO.getAnnualInterestRate().divide(BigDecimal.valueOf(100), 10, BigDecimal.ROUND_HALF_UP);

    }
}
