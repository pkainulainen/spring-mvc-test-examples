TodoApp.Controllers.UserController = {
    login: function() {
        window.log("Rendering login page");
        var loginView = new TodoApp.Views.LoginView();
        TodoApp.mainRegion.show(loginView);
    },
    logout: function() {
        window.log("Logging user out")
        $.get("/api/logout", function(data) {
            window.log("Logout successful. Received data: ", data);
            TodoApp.vent.trigger("user:logoutSuccess");
        })
    }
};