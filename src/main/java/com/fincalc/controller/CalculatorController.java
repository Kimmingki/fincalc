package com.fincalc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalculatorController {

    @GetMapping
    public String home() {
        return "redirect:/loan";
    }

    @GetMapping("/loan")
    public String showLoanForm() {
        return "loan-form";
    }

    @GetMapping("/deposit")
    public String showDepositForm() {
        return "deposit-form";
    }

    @GetMapping("/saving")
    public String showSavingForm() {
        return "saving-form";
    }
}
