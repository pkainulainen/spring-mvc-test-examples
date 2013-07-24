TodoApp.Controllers.TodoController = {
    add: function() {
        window.log("Rendering add todo view.");
        var addTodoView = new TodoApp.Views.AddTodoView();
        TodoApp.mainRegion.show(addTodoView);
    },
    view: function(id) {
        window.log("Rendering view page for todo entry with id: ", id);
        var viewTodoView = new TodoApp.Views.ViewTodoView({id: id});
        TodoApp.mainRegion.show(viewTodoView);
    },
    list: function() {
        window.log("Rendering todo list view.");
        var todoPageView = new TodoApp.Views.TodoListView();
        TodoApp.mainRegion.show(todoPageView);
    },
    update: function(id) {
        window.log("Rendering update todo view.")

        var updateTodoView = new TodoApp.Views.UpdateTodoView({id: id});
        TodoApp.mainRegion.show(updateTodoView);
    }
};
