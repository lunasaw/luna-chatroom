$(".toggle").on("click", function () {
    $(".container").stop().addClass("active");
});

$(".close").on("click", function () {
    $(".container").stop().removeClass("active");
});

$("#btn").click(function () {
    $.post("login?", $("#loginForm").serialize(), function (res) {
        if (res.success) {
            console.log(res);
            location.href = "main.html";
        } else {
            console.log(res);
            $("#err_msg").html(res.message);
        }
    }, "json");
});