$(document).ready(function() {
    // 대출 금액 입력 시 실시간으로 포맷된 값을 표시
    $("#loanAmount").on("input", function() {
        let rawValue = $(this).val().replace(/[^0-9]/g, ""); // 숫자 이외의 문자 제거
        let formattedValue = formatNumberWithCommas(rawValue);
        $("#formattedAmount").text(formattedValue + "원"); // 변환된 값 표시
    });

    // 빠른 입력 버튼 클릭 시 기존 값에 추가
    $(".quick-btn").click(function() {
        let valueToAdd = parseFloat($(this).data("value")); // 버튼 값 (숫자로 변환)
        let inputField = $(this).closest(".mb-3").find("input"); // 해당 버튼이 속한 입력 필드 찾기
        let currentValue = parseFloat(inputField.val()) || 0; // 현재 입력값 (비어있으면 0)

        let newValue = currentValue + valueToAdd
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
            beforeSend: function() {
                $("#resultContainer").fadeOut(200); // 결과 숨기기
            },
            success: function(response) {
                setTimeout(() => {
                    displayResult(response);
                    $("#resultContainer").removeClass("d-none").fadeIn(400); // 부드러운 효과
                }, 300);
            },
            error: function(xhr) {
                console.error("Error:", xhr.responseText);
            }
        });
    });
});

// 계산 결과 출력 함수
function displayResult(data) {
    $("#totalInterest").text(data.totalInterest.toLocaleString() + " 원");
    $("#totalPayment").text(data.totalPayment.toLocaleString() + " 원");

    let monthlyList = $("#monthlyPayments");
    monthlyList.empty();
    $.each(data.monthlyPayments, function(index, payment) {
        monthlyList.append(`<li class="list-group-item">${index + 1}개월차: ${payment.toLocaleString()} 원</li>`);
    });
}

// 숫자를 금액 형식(콤마 포함)으로 변환하는 함수
function formatNumberWithCommas(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}