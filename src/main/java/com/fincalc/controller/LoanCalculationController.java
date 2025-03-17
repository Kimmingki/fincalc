package com.fincalc.controller;

import com.fincalc.dto.loan.LoanRequestDTO;
import com.fincalc.dto.loan.LoanResponseDTO;
import com.fincalc.service.LoanCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loan")
public class LoanCalculationController {

    private final LoanCalculationService loanCalculationService;

    @PostMapping("/calculate")
    public LoanResponseDTO calculateLoan(@RequestBody LoanRequestDTO loanRequestDTO) {
        return loanCalculationService.calculateLoan(loanRequestDTO);
    }
}
