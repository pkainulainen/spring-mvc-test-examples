<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title></title>
</head>
<body>
    <h1><spring:message code="label.todo.list.page.title"/></h1>
    <div>
        <a href="/todo/add" class="btn btn-primary"><spring:message code="label.add.todo.link"/></a>
    </div>
    <div id="todo-list" class="page-content">
        <c:choose>
            <c:when test="${empty todos}">
                <p><spring:message code="label.todo.list.empty"/></p>
            </c:when>
            <c:otherwise>
                <c:forEach items="${todos}" var="todo">
                    <div class="well well-small">
                        <a href="/todo/${todo.id}"><c:out value="${todo.title}"/></a>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>