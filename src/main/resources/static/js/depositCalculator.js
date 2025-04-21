$(document).ready(function() {
    // 예금 금액 input 이벤트: label 업데이트
    $("#depositAmount").on("input", function() {
        let rawValue = $(this).val().replace(/[^0-9]/g, "");
        let formattedValue = formatNumberWithCommas(rawValue);
        $("#depositAmountLabel").text("예금 금액 (원): " + formattedValue + "원");
    });

    // 연이율 input 이벤트: label 업데이트
    $("#annualInterestRate").on("input", function() {
        let rawValue = $(this).val().replace(/[^0-9\.]/g, "");
        let num = parseFloat(rawValue);
        if (isNaN(num)) num = 0;
        $("#annualInterestRateLabel").text("연이율 (%): " + num.toFixed(2) + "%");
    });

    // 예금 기간 input 이벤트: label 업데이트
    $("#depositPeriod").on("input", function() {
        let rawValue = $(this).val().replace(/[^0-9]/g, "");
        let num = parseInt(rawValue) || 0;
        $("#depositPeriodLabel").text("예금 기간 (년): " + num + "년");
    });

    // 퀵 버튼 클릭: 해당 input 필드에 값 추가 및 label 업데이트
    $(".quick-btn").click(function() {
        // 해당 버튼이 속한 .mb-3의 첫 번째 input 요소를 찾음
        let inputField = $(this).closest(".mb-3").find("input").first();
        // 연이율의 경우 소수점 포함, 그 외는 정수로 처리
        let currentValue = parseFloat(inputField.val().replace(/[^0-9\.]/g, "")) || 0;
        let valueToAdd = parseFloat($(this).data("value"));
        let newValue = currentValue + valueToAdd;
        if (inputField.attr("id") !== "annualInterestRate") {
            newValue = Math.floor(newValue);
        }
        inputField.val(newValue).trigger("input");
    });

    // 폼 제출 이벤트 처리: AJAX 요청으로 계산 결과 받아오기
    $("#depositForm").submit(function(event) {
        event.preventDefault();

        let formData = {
            depositAmount: $("input[name='depositAmount']").val(),
            annualInterestRate: $("input[name='annualInterestRate']").val(),
            depositPeriod: $("input[name='depositPeriod']").val(),
            interestType: $("select[name='interestType']").val()
        };

        $.ajax({
            type: "POST",
            url: "/api/deposit/calculate",
            contentType: "application/json",
            data: JSON.stringify(formData),
            beforeSend: function() {
                console.log("Deposit 요청 데이터:", formData);
            },
            success: function(response) {
                console.log("Deposit 서버 응답 데이터:", response);
                displayDepositResult(response);
                $("#depositResultContainer").removeClass("d-none");
            },
            error: function(xhr) {
                console.error("Deposit 계산 에러:", xhr.responseText);
            }
        });
    });
});

function formatNumberWithCommas(number) {
    return Math.floor(number).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function displayDepositResult(data) {
    $("#interestEarned").text(formatNumberWithCommas(data.interestEarned) + " 원");
    $("#finalAmount").text(formatNumberWithCommas(data.finalAmount) + " 원");
}