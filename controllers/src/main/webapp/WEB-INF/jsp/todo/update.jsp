<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="/static/js/todo.form.js"></script>
</head>
<body>
    <h1><spring:message code="label.todo.update.page.title"/></h1>
    <div class="well page-content">
        <form:form action="/todo/update" commandName="todo" method="POST" enctype="utf8">
            <form:hidden path="id"/>
            <div id="control-group-title" class="control-group">
                <label for="todo-title"><spring:message code="label.todo.title"/>:</label>

                <div class="controls">
                    <form:input id="todo-title" path="title"/>
                    <form:errors id="error-title" path="title" cssClass="help-inline"/>
                </div>
            </div>
            <div id="control-group-description" class="control-group">
                <label for="todo-description"><spring:message code="label.todo.description"/>:</label>

                <div class="controls">
                    <form:textarea id="todo-description" path="description"/>
                    <form:errors id="error-description" path="description" cssClass="help-inline"/>
                </div>
            </div>
            <div class="action-buttons">
                <a href="/todo/${todo.id}" class="btn"><spring:message code="label.cancel"/></a>
                <button id="update-todo-button" type="submit" class="btn btn-primary"><spring:message
                        code="label.update.todo.button"/></button>
            </div>
        </form:form>
    </div>
</body>
</html>