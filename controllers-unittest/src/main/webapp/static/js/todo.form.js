$(function() {
    addValidationErrorClassesToForm();

    function addValidationErrorClassesToForm() {
        $("form").find(".control-group").each(function() {
            var errorMessage = $(this).find(".help-inline").text();

            if (errorMessage) {
                $(this).addClass("error");
            }
        })
    }
})