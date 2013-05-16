TodoApp.ErrorRouting = function(){
    var ErrorRouting = {};

    ErrorRouting.Router = Backbone.Marionette.AppRouter.extend({
        appRoutes: {
            "error/404": "404",
            "error/error": "error"
        }
    });

    TodoApp.addInitializer(function(){
        ErrorRouting.router = new ErrorRouting.Router({
            controller: TodoApp.Controllers.ErrorController
        });
    });

    return ErrorRouting;
}();

