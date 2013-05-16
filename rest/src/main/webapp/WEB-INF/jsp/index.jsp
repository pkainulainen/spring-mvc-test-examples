<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="spring.test.mvc.example.title"/></title>
    <link rel="stylesheet" type="text/css" href="/static/css/example.css"/>
    <link rel="stylesheet" type="text/css" href="/static/css/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/static/css/bootstrap-responsive.css"/>
    <script type="text/javascript" src="/static/js/vendor/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="/static/js/vendor/spin.js"></script>
    <script type="text/javascript" src="/static/js/vendor/bootstrap.js"></script>
    <script type="text/javascript" src="/static/js/vendor/bootstrap-transition.js"></script>
    <script type="text/javascript" src="/static/js/vendor/bootstrap-collapse.js"></script>
    <script type="text/javascript" src="/static/js/vendor/i18next-1.5.8.js"></script>
    <script type="text/javascript" src="/static/js/vendor/underscore.js"></script>
    <script type="text/javascript" src="/static/js/vendor/backbone.js"></script>
    <script type="text/javascript" src="/static/js/vendor/Backbone.ModelBinder.js"></script>
    <script type="text/javascript" src="/static/js/vendor/handlebars-1.0.rc.1.js"></script>
    <script type="text/javascript" src="/static/js/vendor/backbone.marionette.js"></script>
    <script type="text/javascript" src="/static/js/app/app.js"></script>
    <script type="text/javascript" src="/static/js/app/todo/todo.events.js"></script>
    <script type="text/javascript" src="/static/js/app/todo/todo.controller.js"></script>
    <script type="text/javascript" src="/static/js/app/todo/todo.i18n.js"></script>
    <script type="text/javascript" src="/static/js/app/todo/todo.models.js"></script>
    <script type="text/javascript" src="/static/js/app/todo/todo.routing.js"></script>
    <script type="text/javascript" src="/static/js/app/todo/todo.views.js"></script>
    <script type="text/javascript" src="/static/js/app/error/error.events.js"></script>
    <script type="text/javascript" src="/static/js/app/error/error.controller.js"></script>
    <script type="text/javascript" src="/static/js/app/error/error.routing.js"></script>
    <script type="text/javascript" src="/static/js/app/error/error.views.js"></script>
</head>
<body>
<div class="page">
    <div class="navbar">
        <div class="navbar-inner">
            <div class="container">
                <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </a>

                <span class="brand"><spring:message code="label.navigation.brand"/></span>

                <div class="nav-collapse">
                    <ul class="nav">
                        <li><a href="#/"><spring:message code="label.navigation.homepage.link"/></a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <div class="content">
        <div id="activity-indicator"></div>
        <div id="dialog-holder"></div>
        <div id="message-holder"></div>
        <div id="view-content"></div>
    </div>
</div>
<!-- Handlebars Templates -->

<!-- Feedback Message View -->
<script id="template-feedback-message" type="text/x-handlebars-template">
    <a class="close" data-dismiss="alert">&times;</a>
    {{message}}
</script>

<!-- Add Todo View -->
<script id="template-add-todo-view" type="text/x-handlebars-template">
    <h1><spring:message code="label.add.todo.page.title"/></h1>
    <div class="well page-content">
        <form id="add-todo-form" action="#" method="POST">
            <div id="control-group-title" class="control-group">
                <label for="todo-title"><spring:message code="label.todo.title"/>:</label>

                <div class="controls">
                    <input id="todo-title" type="text" name="title"/>
                    <span id="error-title" class="help-inline"></span>
                </div>
            </div>
            <div id="control-group-description" class="control-group">
                <label for="todo-description"><spring:message code="label.todo.description"/>:</label>

                <div class="controls">
                    <textarea id="todo-description" name="description"/>
                    <span id="error-description" class="help-inline"></span>
                </div>
            </div>
            <div class="action-buttons">
                <a href="#/" class="btn"><spring:message code="label.cancel"/></a>
                <a id="add-todo-button" href="#" type="submit" class="btn btn-primary"><spring:message
                        code="label.add.todo.button"/></a>
            </div>
        </form>
    </div>
</script>
<!-- Todo List Item View -->
<script id="template-todo-list-view" type="text/x-handlebars-template">
    <a href="#/todo/{{id}}">{{title}}</a>
</script>

<!-- Todo List View -->
<script id="template-todo-list-page" type="text/x-handlebars-template">
    <h1><spring:message code="label.todo.list.title"/></h1>
    <div id="todo-list-actions">
        <a href="#/todo/add" class="btn btn-primary"><spring:message code="label.add.todo.link"/></a>
    </div>
    <div id="todo-list-items" class="page-content">

    </div>
</script>

<!-- Update Todo View -->
<script id="template-update-todo-view" type="text/x-handlebars-template">
    <h1><spring:message code="label.todo.update.page.title"/></h1>
    <div class="well page-content">
        <form id="update-todo-form" action="#" method="POST">
            <input type="hidden" name="id" value="{{id}}"/>
            <div id="control-group-title" class="control-group">
                <label for="todo-title"><spring:message code="label.todo.title"/>:</label>

                <div class="controls">
                    <input id="todo-title" type="text" name="title"/>
                    <span id="error-title" class="help-inline"></span>
                </div>
            </div>
            <div id="control-group-description" class="control-group">
                <label for="todo-description"><spring:message code="label.todo.description"/>:</label>

                <div class="controls">
                    <textarea id="todo-description" name="description"/>
                    <span id="error-description" class="help-inline"></span>
                </div>
            </div>
            <div class="action-buttons">
                <a href="#/todo/{{id}}" class="btn"><spring:message code="label.cancel"/></a>
                <a id="update-todo-button" href="#" class="btn btn-primary"><spring:message
                        code="label.update.todo.button"/></a>
            </div>
        </form>
    </div>
</script>

<!-- View Todo View -->
<script id="template-view-todo-view" type="text/x-handlebars-template">
    <h1><spring:message code="label.todo.view.page.title"/></h1>
    <div class="well page-content">
        <h2>{{title}}</h2>
        <div>
            <p>{{description}}</p>
        </div>
        <div class="action-buttons">
            <a href="#/todo/update/{{id}}" class="btn btn-primary"><spring:message code="label.update.todo.link"/></a>
            <a id="delete-todo-link" href="#" class="btn btn-primary"><spring:message code="label.delete.todo.link"/></a>
        </div>
    </div>
</script>

<!-- Delete Todo Confirmation Dialog -->
<script id="template-delete-todo-confirmation-dialog" type="text/x-handlebars-template">
    <div class="modal-header">
        <button class="close" data-dismiss="modal">×</button>
        <h3><spring:message code="label.todo.delete.dialog.title"/></h3>
    </div>
    <div class="modal-body">
        <p><spring:message code="label.todo.delete.dialog.message"/></p>
    </div>
    <div class="modal-footer">
        <a id="cancel-delete-todo-button" href="#" class="btn"><spring:message code="label.cancel"/></a>
        <a id="delete-todo-button" href="#" class="btn btn-primary"><spring:message code="label.delete.todo.button"/></a>
    </div>
</script>

<!-- 404 View -->
<script id="template-not-found-view" type="text/x-handlebars-template">
    <h1><spring:message code="label.404.page.title"/></h1>
    <div class="page-content">
        <p><spring:message code="label.404.page.message"/></p>
    </div>
</script>

<!-- Error View -->
<script id="template-error-view" type="text/x-handlebars-template">
    <h1><spring:message code="label.internalservererror.page.title"/></h1>
    <div class="page-content">
        <p><spring:message code="label.internalservererror.page.message"/></p>
    </div>
</script>
</body>
</html>