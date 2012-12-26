TodoApp.vent.on("user:login", function(){
    Backbone.history.navigate("#/user/login");
});

TodoApp.vent.on("user:loginFailed", function() {
    var translatedErrorMessage = i18n.t("loginFailed");
    var errorMessage = new TodoApp.Models.FeedbackMessage({message: translatedErrorMessage});
    var errorMessageView = new TodoApp.Views.ErrorMessageView({model: errorMessage});
    TodoApp.messageRegion.show(errorMessageView);
});

TodoApp.vent.on("user:loginSuccess", function() {
    var showTodoList = function() {
        Backbone.history.navigate("#/");
        TodoApp.showLogoutLink();
    }

    TodoApp.getLoggedInUser(showTodoList);
});

TodoApp.vent.on("user:logoutSuccess", function() {
   TodoApp.setUserAsAnonymous();
    Backbone.history.navigate("#/");
});
