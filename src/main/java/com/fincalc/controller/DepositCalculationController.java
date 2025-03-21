package com.fincalc.controller;

import com.fincalc.dto.deposit.DepositRequestDTO;
import com.fincalc.dto.deposit.DepositResponseDTO;
import com.fincalc.service.DepositCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deposit")
public class DepositCalculationController {

    private final DepositCalculationService depositCalculationService;

    @PostMapping("/calculate")
    public DepositResponseDTO calculateDeposit(@RequestBody DepositRequestDTO depositRequestDTO) {
        return depositCalculationService.calculateDeposit(depositRequestDTO);
    }
}
