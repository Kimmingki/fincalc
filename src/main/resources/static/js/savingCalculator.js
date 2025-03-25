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
            interestType: $("select[name='interestType']").val()
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
                displaySavingResult(response);
                $("#savingResultContainer").show();
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

function displaySavingResult(data) {
    $("#savingInterestEarned").text(formatNumberWithCommas(data.totalInterest) + " 원");
    $("#savingFinalAmount").text(formatNumberWithCommas(data.finalAmount) + " 원");
}