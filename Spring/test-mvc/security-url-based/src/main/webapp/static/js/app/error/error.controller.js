TodoApp.Controllers.ErrorController = {
    404: function() {
        window.log("Rendering 404 view.");
        var notFoundView = new TodoApp.Views.NotFoundView();
        TodoApp.mainRegion.show(notFoundView);
    },
    notAuthorized: function() {
        window.log("Rendering not authorized view");
        var notAuthorizedView = new TodoApp.Views.NotAuthorizedView();
        TodoApp.mainRegion.show(notAuthorizedView);
    },
    error: function() {
        window.log("Rendering error view.");
        var errorView = new TodoApp.Views.ErrorView();
        TodoApp.mainRegion.show(errorView);
    }

}