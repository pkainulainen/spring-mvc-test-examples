TodoApp.Views.FeedbackMessageView = Marionette.ItemView.extend({
   id: "feedback-message",
    className: "alert alert-success fade in",
    template: "#template-feedback-message",
    onRender: function() {
        var self = this;
        $(this.el).alert().delay(5000).fadeOut("fast", function() { self.close(); })
    }
});

TodoApp.Views.FormView = Marionette.ItemView.extend({
    addFormValidationErrors: function(formSelector, errorMessage, fieldErrors) {
        var form = $(formSelector);

        form.find(".control-group").removeClass("error");
        form.find(".help-inline").text("");

        var formErrorHolder = form.find("#form-error-messages");
        formErrorHolder.text(errorMessage);

        $.each(fieldErrors, function(index, fieldError) {
            var pathSuffix = constructPathSuffix(fieldError.path);
            var controlGroupSelector = "#control-group-" + pathSuffix;
            $(controlGroupSelector).addClass("error");
            var fieldErrorSelector = "#error-" + pathSuffix;
            var fieldErrorHolder = form.find(fieldErrorSelector);
            if (fieldErrorHolder) {
                fieldErrorHolder.text(fieldError.message);
            }
        });

        /**
         * Constructs a path suffix so that the we can parse the indexed name
         * attributes to an id suffix which contains only legal characters.
         *
         * E.g. list[0].attribute is parsed to list_0_attribute.
         * @param path The field path returned by the back end service
         */
        function constructPathSuffix(path) {
            return path.replace(/\./g, '').replace(/\[/g, '_').replace(/\]/g, '_');
        }
    },
    initialize:function () {
        window.log("Binding the model to the form fields.")
        this._modelBinder = new Backbone.ModelBinder();
        if (this.onInitialize) {
            this.onInitialize();
        }
    },
    onClose: function() {
        window.log("Unbinding the model from the form fields.")
        this._modelBinder.unbind();
    }
});

TodoApp.Views.AddTodoView = TodoApp.Views.FormView.extend({
    events: {
        "click #add-todo-button": "saveTodo"
    },
    onRender: function() {
        this._modelBinder.bind(this.model, this.$('#add-todo-form'));
    },
    onInitialize: function() {
        this.model = new TodoApp.Models.Todo();
    },
    template: "#template-add-todo-view",
    saveTodo: function(e) {
        e.preventDefault();
        window.log("Saving todo:", this.model.toJSON());

        var self = this;

        this.model.save({},{
            success: function(model, result) {
                window.log("A new todo entry was added: ", model.toJSON());
                TodoApp.vent.trigger("todo:added", model);

            },
            error: function(model, response) {
                window.log("Error occurred with response: ", response);
                if (response.status == 400) {
                    var validationError = JSON.parse(response.responseText);
                    self.addFormValidationErrors("#add-todo-form", "", validationError.fieldErrors);
                }
            }
        })
    }
});

TodoApp.Views.DeleteTodoView = Marionette.ItemView.extend({
    id: "delete-todo-confirmation-dialog",
    cancelDelete: function(e) {
        window.log("Cancel delete button was clicked.")

        e.preventDefault();
        this.close();
    },
    className: "modal",
    delete: function(e) {
        window.log("Deleting todo entry: ", this.model.toJSON());

        var self = this;

        this.model.destroy({
            success: function(model, response) {
                window.log("Deleted to entry: ", model.toJSON());
                self.close();
                TodoApp.vent.trigger("todo:deleted", model);
            }
        })
    },
    events: {
        "click #cancel-delete-todo-button": "cancelDelete",
        "click #delete-todo-button": "delete"
    },
    model: TodoApp.Models.Todo,
    template: "#template-delete-todo-confirmation-dialog"
})

TodoApp.Views.UpdateTodoView = TodoApp.Views.FormView.extend({
    events: {
        "click #update-todo-button": "saveTodo"
    },
    onInitialize: function() {
        this.model = new TodoApp.Models.Todo({id: this.options.id});
        this.model.fetch();
    },
    onRender: function() {
        this._modelBinder.bind(this.model, this.$('#update-todo-form'));
    },
    template: "#template-update-todo-view",
    saveTodo: function(e) {
        e.preventDefault();
        window.log("Saving todo: ", this.model.toJSON());

        var self = this;

        this.model.save({},{
            success: function(model, result) {
                window.log("Todo entry was updated: ", model.toJSON());
                TodoApp.vent.trigger("todo:updated", model);

            },
            error: function(model, response) {
                window.log("Error occurred with response: ", response);
                if (response.status == 400) {
                    var validationError = JSON.parse(response.responseText);
                    self.addFormValidationErrors("#update-todo-form", "", validationError.fieldErrors);
                }
            }
        })
    }
});

TodoApp.Views.TodoListView = Marionette.ItemView.extend({
    className: "well",
    template: "#template-todo-list-view"
});

TodoApp.Views.TodoListView = Marionette.CompositeView.extend({
    id: "todo-list",
    initialize: function() {
        this.collection =  new TodoApp.Collections.Todos();
        this.collection.fetch();
    },
    itemView: TodoApp.Views.TodoListView,
    itemViewContainer: "#todo-list-items",
    template: "#template-todo-list-page"
});

TodoApp.Views.ViewTodoView = Marionette.ItemView.extend({
    events: {
        "click #delete-todo-link": "showDeleteDialog"
    },
    initialize: function() {
        this.model = new TodoApp.Models.Todo({id: this.options.id});
        this.model.bind("change", this.render);
        this.model.fetch();
    },
    showDeleteDialog: function(e) {
        window.log("Showing delete confirmation dialog for todo: ", this.model.toJSON());

        e.preventDefault();

        var deleteConfirmationDialog = new TodoApp.Views.DeleteTodoView({model: this.model});
        TodoApp.modalRegion.show(deleteConfirmationDialog);
    },
    template: "#template-view-todo-view"
})