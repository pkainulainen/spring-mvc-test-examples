<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title></title>
    <script type="text/javascript" src="/static/js/todo.view.js"></script>
</head>
<body>
    <div id="todo-id" class="hidden">${todo.id}</div>
    <h1><spring:message code="label.todo.view.page.title"/></h1>
    <div class="well page-content">
        <h2><c:out value="${todo.title}"/></h2>
        <div>
            <p><c:out value="${todo.description}"/></p>
        </div>
        <div class="action-buttons">
            <a href="/todo/update/${todo.id}" class="btn btn-primary"><spring:message code="label.update.todo.link"/></a>
            <a id="delete-todo-link" class="btn btn-primary"><spring:message code="label.delete.todo.link"/></a>
        </div>
    </div>
    <script id="template-delete-todo-confirmation-dialog" type="text/x-handlebars-template">
        <div id="delete-todo-confirmation-dialog" class="modal">
            <div class="modal-header">
                <button class="close" data-dismiss="modal">×</button>
                <h3><spring:message code="label.todo.delete.dialog.title"/></h3>
            </div>
            <div class="modal-body">
                <p><spring:message code="label.todo.delete.dialog.message"/></p>
            </div>
            <div class="modal-footer">
                <a id="cancel-todo-button" href="#" class="btn"><spring:message code="label.cancel"/></a>
                <a id="delete-todo-button" href="#" class="btn btn-primary"><spring:message code="label.delete.todo.button"/></a>
            </div>
        </div>
    </script>
</body>
</html>