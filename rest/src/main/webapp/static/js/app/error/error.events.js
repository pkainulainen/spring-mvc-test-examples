TodoApp.vent.on("error:404", function() {
    window.log("Processing 404 event.")
    Backbone.history.navigate("#/error/404");
});

TodoApp.vent.on("error:error", function(){
    window.log("Processing error event")
    Backbone.history.navigate("#/error/error");
});