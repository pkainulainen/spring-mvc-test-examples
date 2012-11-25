TodoApp.vent.on("routing:started", function(){
    if( ! Backbone.History.started) Backbone.history.start();
})

TodoApp.vent.on("todo:added", function(model) {
    window.log("Processing todo added event for model: ", model.toJSON());
    Backbone.history.navigate("#/todo/" + model.get("id"));

    var translatedMessage = i18n.t("todoAdded", { title: model.get("title") });
    var feedbackMessage = new TodoApp.Models.FeedbackMessage({message: translatedMessage});
    var messageView = new TodoApp.Views.FeedbackMessageView({model: feedbackMessage});
    TodoApp.messageRegion.show(messageView);
});

TodoApp.vent.on("todo:deleted", function(model) {
    window.log("Processing todo deleted event for model: ", model.toJSON());

    Backbone.history.navigate("#/");

    var translatedMessage = i18n.t("todoDeleted", {title: model.get("title")});
    var feedBackMessage = new TodoApp.Models.FeedbackMessage({message: translatedMessage});
    var messageView = new TodoApp.Views.FeedbackMessageView({model: feedBackMessage});
    TodoApp.messageRegion.show(messageView);
});

TodoApp.vent.on("todo:updated", function(model) {
    window.log("Processing todo updated event for model: ", model.toJSON());

    Backbone.history.navigate("#/todo/" + model.get("id"));

    var translatedMessage = i18n.t("todoUpdated", {title: model.get("title")});
    var feedbackMessage = new TodoApp.Models.FeedbackMessage({message: translatedMessage});
    var messageView = new TodoApp.Views.FeedbackMessageView({model: feedbackMessage});
    TodoApp.messageRegion.show(messageView);
});
