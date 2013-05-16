window.debug = true;

// usage: log('inside coolFunc', this, arguments);
// paulirish.com/2009/log-a-lightweight-wrapper-for-consolelog/
window.log = function(){
    if(window.debug){
        log.history = log.history || []; // store logs to an array for reference
        log.history.push(arguments);
        if(this.console) {
            arguments.callee = arguments.callee.caller;
            var newarr = [].slice.call(arguments);
            (typeof console.log === 'object' ? log.apply.call(console.log, console, newarr) : console.log.apply(console, newarr));
        }
    }
};

Backbone.Marionette.TemplateCache.prototype.compileTemplate = function(rawTemplate){
    return rawTemplate;
};

//Configures Backbone.Marionette to render the Handlebar template with the given data
Backbone.Marionette.Renderer.render = function(template, data){
    var handleBarsTemplate = Handlebars.compile($(template).html());
    return handleBarsTemplate(data);
};

var TodoApp = new Backbone.Marionette.Application();

TodoApp.addRegions({
    mainRegion: "#view-content",
    messageRegion: "#message-holder",
    modalRegion: "#dialog-holder"
});

TodoApp.Collections = {};
TodoApp.Controllers = {};
TodoApp.Models = {};
TodoApp.Translations = {};
TodoApp.Vents = {};
TodoApp.Views = {};

TodoApp.spinner = new Spinner({
    lines: 13, // The number of lines to draw
    length: 30, // The length of each line
    width: 7, // The line thickness
    radius: 19, // The radius of the inner circle
    corners: 1, // Corner roundness (0..1)
    rotate: 0, // The rotation offset
    color: '#000', // #rgb or #rrggbb
    speed: 1, // Rounds per second
    trail: 60, // Afterglow percentage
    shadow: false, // Whether to render a shadow
    hwaccel: false, // Whether to use hardware acceleration
    className: 'spinner', // The CSS class to assign to the spinner
    zIndex: 2e9, // The z-index (defaults to 2000000000)
    top: '100', // Top position relative to parent in px
    left: 'auto' // Left position relative to parent in px
});

$(document).bind('ajaxStart', function() {
    window.log("ajaxStart");
    TodoApp.spinner.spin(document.getElementById('activity-indicator'));
}).bind('ajaxError', function(event, request ,settings) {
    window.log('ajaxError with status code: ', request.status);

    TodoApp.spinner.stop();

    if (request.status != 400) {
        if (request.status == 404) {
            TodoApp.vent.trigger("error:404");
        }
        else {
            TodoApp.vent.trigger("error:error");
        }
    }
}).bind('ajaxStop', function() {
    window.log("ajaxStop");
    TodoApp.spinner.stop();
});

$(document).ready(function(){
    i18n.init({
        debug: true,
        resStore: TodoApp.Translations.resources
    });

    TodoApp.start();
});