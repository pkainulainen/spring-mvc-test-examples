TodoApp.UserRouting = function() {
    var UserRouting = {};

    UserRouting.Router = Backbone.Marionette.AppRouter.extend({
        appRoutes: {
            "user/login": "login",
            "user/logout": "logout"
        }
    });

    TodoApp.addInitializer(function(){
        UserRouting.router = new UserRouting.Router({
            controller: TodoApp.Controllers.UserController
        });

        TodoApp.vent.trigger("routing:started");
    });

    return UserRouting;
}();