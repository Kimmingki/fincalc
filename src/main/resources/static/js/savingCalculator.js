$(document).ready(function() {
    // 매월 납입액 input 이벤트: label 업데이트
    $("#monthlyDeposit").on("input", function() {
        let rawValue = $(this).val().replace(/[^0-9]/g, "");
        let formattedValue = formatNumberWithCommas(rawValue);
        $("#monthlyDepositLabel").text("매월 납입액 (원): " + formattedValue + "원");
    });

    // 연이율 input 이벤트: label 업데이트
    $("#annualInterestRate").on("input", function() {
        let rawValue = $(this).val().replace(/[^0-9\.]/g, "");
        let num = parseFloat(rawValue);
        if (isNaN(num)) num = 0;
        $("#annualInterestRateLabel").text("연이율 (%): " + num.toFixed(2) + "%");
    });

    // 적금 기간 input 이벤트: label 업데이트
    $("#savingPeriodMonths").on("input", function() {
        let rawValue = $(this).val().replace(/[^0-9]/g, "");
        let num = parseInt(rawValue) || 0;
        $("#savingPeriodLabel").text("적금 기간 (개월): " + num + "개월");
    });

    // 퀵 버튼 클릭: 해당 input 필드에 값 추가 및 label 업데이트
    $(".quick-btn").click(function() {
        // 해당 버튼이 속한 .mb-3의 첫 번째 input 요소를 찾음
        let inputField = $(this).closest(".mb-3").find("input").first();
        let currentValue = parseFloat(inputField.val().replace(/[^0-9\.]/g, "")) || 0;
        let valueToAdd = parseFloat($(this).data("value"));
        let newValue = currentValue + valueToAdd;
        if (inputField.attr("id") !== "annualInterestRate") {
            newValue = Math.floor(newValue);
        }
        inputField.val(newValue).trigger("input");
    });

    // 폼 제출 이벤트 처리: AJAX 요청으로 적금 계산 결과 받아오기
    $("#savingForm").submit(function(event) {
        event.preventDefault();

        let formData = {
            monthlyDeposit: $("input[name='monthlyDeposit']").val(),
            annualInterestRate: $("input[name='annualInterestRate']").val(),
            savingPeriodMonths: $("input[name='savingPeriodMonths']").val(),
            interestType: $("select[name='interestType']").val(),
            taxType: $("input[name='taxType']:checked").val()
        };

        $.ajax({
            type: "POST",
            url: "/api/saving/calculate",
            contentType: "application/json",
            data: JSON.stringify(formData),
            beforeSend: function() {
                console.log("Saving 요청 데이터:", formData);
            },
            success: function(response) {
                console.log("Saving 서버 응답 데이터:", response);
                displaySavingResult(response, formData.taxType);
                $(".saving-container").addClass("expanded");
                $("#savingResultContainer").removeClass("d-none");
            },
            error: function(xhr) {
                console.error("Saving 계산 에러:", xhr.responseText);
            }
        });
    });
});

function formatNumberWithCommas(number) {
    return Math.floor(number).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function displaySavingResult(data, taxType) {
    const P = parseFloat($("input[name='monthlyDeposit']").val());
    const n = parseInt($("input[name='savingPeriodMonths']").val(), 10);
    const grossInterest = parseFloat(data.totalInterest);  // 세전 이자
    const grossFinal = parseFloat(data.finalAmount);       // 세전 만기 금액

    // 과세 계산
    let taxAmount   = 0;
    let netInterest = grossInterest;
    if (taxType === "taxable") {
        taxAmount   = grossInterest * 0.154;              // 15.4% 세율
        netInterest = grossInterest - taxAmount;
    }

    // 세후 만기 금액 = 원금 총합 + 세후 이자
    const principalTotal = P * n;
    const netFinal = principalTotal + netInterest;

    // 화면에 출력
    $("#savingInterestEarned").text(formatNumberWithCommas(grossInterest) + " 원");
    $("#savingFinalAmount").text(formatNumberWithCommas(grossFinal) + " 원");
    $("#savingNetInterest").text(formatNumberWithCommas(Math.floor(netInterest)) + " 원");
    $("#savingNetFinalAmount").text(formatNumberWithCommas(Math.floor(netFinal)) + " 원");
}