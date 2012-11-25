//Models
TodoApp.Models.FeedbackMessage = Backbone.Model.extend();

TodoApp.Models.Todo = Backbone.Model.extend({
    urlRoot: "/todo"
});

//Collections
TodoApp.Collections.Todos = Backbone.Collection.extend({
    model: TodoApp.Models.Todo,
    url: function() {
        return "/todo";
    }
})