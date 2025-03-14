$(document).ready(function() {
    // 대출 금액 입력 시 실시간으로 label 업데이트
    $("#loanAmount").on("input", function() {
        let rawValue = $(this).val().replace(/[^0-9]/g, "");
        let formattedValue = formatNumberWithCommas(rawValue);
        // label 업데이트
        $("#loanAmountLabel").text("대출 금액 (원): " + formattedValue + "원");
    });

    // 연이율 입력 시 label 업데이트 (소수점 포함)
    $("#annualInterestRate").on("input", function() {
        let rawValue = $(this).val().replace(/[^0-9\.]/g, "");
        let num = parseFloat(rawValue);
        if (isNaN(num)) num = 0;
        // 소수점 두자리로 고정
        let formattedValue = num.toFixed(2);
        $("#annualInterestRateLabel").text("연이율 (%): " + formattedValue + "%");
    });

    // 대출 기간 입력 시 label 업데이트 (정수)
    $("#loanTermMonths").on("input", function() {
        let rawValue = $(this).val().replace(/[^0-9]/g, "");
        let formattedValue = parseInt(rawValue) || 0;
        $("#loanTermMonthsLabel").text("대출 기간 (개월): " + formattedValue + "개월");
    });

    // 퀵 버튼 클릭 시 해당 input 필드에 값 추가 및 label 업데이트
    $(".quick-btn").click(function() {
        let valueToAdd = parseInt($(this).data("value"), 10);
        // 버튼이 속한 부모 .mb-3 내의 첫 번째 input 요소를 찾음
        let inputField = $(this).closest(".mb-3").find("input").first();
        let currentValue = parseInt(inputField.val().replace(/[^0-9]/g, ""), 10) || 0;
        let newValue = Math.floor(currentValue + valueToAdd);
        inputField.val(newValue).trigger("input");
    });

    // 폼 제출 이벤트 처리
    $("#loanForm").submit(function(event) {
        event.preventDefault();

        let formData = {
            loanAmount: $("input[name='loanAmount']").val(),
            annualInterestRate: $("input[name='annualInterestRate']").val(),
            loanTermMonths: $("input[name='loanTermMonths']").val(),
            repaymentType: $("select[name='repaymentType']").val()
        };

        $.ajax({
            type: "POST",
            url: "/api/loan/calculate",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function(response) {
                displayResult(response);
                $("#resultContainer").removeClass("d-none");
            },
            error: function(xhr) {
                console.error("에러 발생:", xhr.responseText);
            }
        });
    });
});

// 계산 결과를 화면에 표시하는 함수
function displayResult(data) {
    $("#totalInterest").text(formatNumberWithCommas(data.totalInterest) + "원");
    $("#totalPayment").text(formatNumberWithCommas(data.totalPayment) + "원");

    let monthlyList = $("#monthlyPayments");
    monthlyList.empty();
    $.each(data.monthlyPayments, function(index, payment) {
        monthlyList.append(`<li class="list-group-item">${index + 1}개월차: ${formatNumberWithCommas(payment)}원</li>`);
    });
}

// 숫자를 금액 형식(콤마 포함)으로 변환하는 함수 (정수 처리)
function formatNumberWithCommas(number) {
    return Math.floor(number).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}
