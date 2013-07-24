TodoApp.Controllers.ErrorController = {
    404: function() {
        window.log("Rendering 404 view.");
        var notFoundView = new TodoApp.Views.NotFoundView();
        TodoApp.mainRegion.show(notFoundView);
    },
    error: function() {
        window.log("Rendering error view.");
        var errorView = new TodoApp.Views.ErrorView();
        TodoApp.mainRegion.show(errorView);
    }

}