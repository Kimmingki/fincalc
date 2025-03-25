package com.fincalc.controller;

import com.fincalc.dto.saving.SavingRequestDTO;
import com.fincalc.dto.saving.SavingResponseDTO;
import com.fincalc.service.SavingCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saving")
public class SavingCalculationController {

    private final SavingCalculationService savingCalculationService;

    @PostMapping("/calculate")
    public SavingResponseDTO calculateSaving(@RequestBody SavingRequestDTO request) {
        return savingCalculationService.calculateSaving(request);
    }
}
