TodoApp.Controllers.TodoController = {
    add: function() {
        window.log("Rendering add todo view.");
        if (this.isAnonymousUser()) {
            TodoApp.vent.trigger("user:login")
        }
        else {
            var addTodoView = new TodoApp.Views.AddTodoView();
            TodoApp.mainRegion.show(addTodoView);
        }
    },
    view: function(id) {
        window.log("Rendering view page for todo entry with id: ", id);
        if (this.isAnonymousUser()) {
            TodoApp.vent.trigger("user:login")
        }
        else {
            var viewTodoView = new TodoApp.Views.ViewTodoView({id: id});
            TodoApp.mainRegion.show(viewTodoView);
        }
    },
    list: function() {
        window.log("Rendering todo list view.");
        if (this.isAnonymousUser()) {
            TodoApp.vent.trigger("user:login")
        }
        else {
            var todoPageView = new TodoApp.Views.TodoListView();
            TodoApp.mainRegion.show(todoPageView);
        }
    },
    isAnonymousUser: function() {
        if (TodoApp.user === 'anonymous') {
            window.log("User is anonymous");
            return true;
        }
        return false;
    },
    update: function(id) {
        window.log("Rendering update todo view.")
        if (this.isAnonymousUser()) {
            TodoApp.vent.trigger("user:login")
        }
        else {
            var updateTodoView = new TodoApp.Views.UpdateTodoView({id: id});
            TodoApp.mainRegion.show(updateTodoView);
        }
    }
};
