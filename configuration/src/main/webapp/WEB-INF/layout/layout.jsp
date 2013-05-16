<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="spring.test.mvc.example.title"/></title>
    <link rel="stylesheet" type="text/css" href="/static/css/example.css"/>
    <link rel="stylesheet" type="text/css" href="/static/css/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/static/css/bootstrap-responsive.css"/>
    <script type="text/javascript" src="/static/js/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="/static/js/bootstrap.js"></script>
    <script type="text/javascript" src="/static/js/bootstrap-transition.js"></script>
    <script type="text/javascript" src="/static/js/bootstrap-collapse.js"></script>
    <sitemesh:write property="head"/>
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
                            <li><a href="/"><spring:message code="label.navigation.homepage.link"/></a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <div class="content">
            <sitemesh:write property="body"/>
        </div>
    </div>
</body>
</html>