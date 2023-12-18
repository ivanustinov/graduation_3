<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-expand-md navbar-dark bg-dark py-0">
    <div class="container">
        <div href="meals" class="navbar-brand"><img src="resources/images/icon-meal.png"> <spring:message
                code="app.title"/></div>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item">
                    <sec:authorize access="isAuthenticated()">
                        <form:form class="form-inline my-2" action="logout" method="post">
                            <a class="btn btn-info mr-1" href="voting"><spring:message code="voting.name"/></a>
                            <sec:authorize access="hasRole('ADMIN')">
                                <a class="btn btn-info mr-1" href="users"><spring:message code="user.title"/></a>
                                <a class="btn btn-info mr-1" href="restaurants"><spring:message
                                        code="restaurants.title"/></a>
                                <a class="btn btn-info mr-1" href="menusList"><spring:message code="menu.title"/></a>
                            </sec:authorize>
                            <a class="btn btn-info mr-1" href="profile"><sec:authentication
                                    property="principal.user.name"/> <spring:message code="app.profile"/></a>
                            <button class="btn btn-primary my-1" type="submit">
                                <span class="fa fa-sign-out"></span>
                            </button>
                        </form:form>
                    </sec:authorize>
                    <sec:authorize access="isAnonymous()">
                        <form:form class="form-inline my-2" id="login_form" action="spring_security_check"
                                   method="post">
                            <input class="form-control mr-1" type="text" placeholder="Email" name="username">
                            <input class="form-control mr-1" type="password" placeholder="Password" name="password">
                            <button class="btn btn-success" type="submit">
                                <span class="fa fa-sign-in"></span>
                            </button>
                        </form:form>
                    </sec:authorize>
                </li>
                <li class="nav-item dropdown">
                    <a class="dropdown-toggle nav-link my-1 ml-2"
                       data-toggle="dropdown">${pageContext.response.locale}</a>
                    <div class="dropdown-menu">
                        <c:set var="params" value="${requestScope['javax.servlet.forward.query_string']}"/>
                        <c:set var="requestPath"
                               value="${requestScope['javax.servlet.forward.request_uri'].concat('?')}"/>
                        <c:set var="dsf"
                               value="${params.contains('lang') ? params.split('lang')[0] : params.concat('&')}"/>
                        <a class="dropdown-item" href="${requestPath.concat(dsf)}lang=en">English</a>
                        <a class="dropdown-item" href="${requestPath.concat(dsf)}lang=ru">Русский</a>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</nav>
<script type="text/javascript">
    var localeCode = "${pageContext.response.locale}";
</script>
