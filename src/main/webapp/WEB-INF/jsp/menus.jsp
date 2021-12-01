<%@ page contentType="text/html;charset=UTF-8" import="java.time.LocalDate" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<jsp:include page="fragments/headTag.jsp"/>
<body>
<script type="text/javascript" src="resources/js/voting.menus.js" defer></script>
<script> let dDate = ${date} </script>
<jsp:include page="fragments/bodyHeader.jsp"/>
<%
    request.setAttribute("dateNow", LocalDate.now());
%>
<div class="jumbotron pt-4">
    <div class="container">
        <div class="row" style="justify-content: flex-end">
            <button class="btn btn-secondary" style="margin-right: 10px" onclick="window.history.back()">
                <span class="fa fa-arrow-left"></span>
                <spring:message code="common.back"/>
            </button>
            <c:if test="${date.isAfter(dateNow) || date.equals(dateNow)}">
                <div style="display:flex; justify-content: flex-end">
                    <button class="btn btn-primary" onclick="add('${date}')">
                        <span class="fa fa-plus"></span>
                        <spring:message code="common.add"/>
                    </button>
                </div>
            </c:if>
        </div>
        <h3 style="margin-top: 30px; margin-bottom: 20px" class="text-center" id="date"><spring:message code="menu.on"/> ${date}</h3>
        <div class="row justify-content-md-center text-center">
            <%--https://getbootstrap.com/docs/4.0/components/card/--%>
            <c:forEach items="${restaurants}" var="restaurant">
                <div class="card mycard" style="width: 290px" id="${restaurant.id}">
                    <jsp:useBean id="restaurant" type="ru.ustinov.voting.model.Restaurant"/>
                    <h3 class="my-0 font-weight-normal" id="restaurant_name">${restaurant.name}</h3>
                    <ul class="list-unstyled mt-3 mb-4"><spring:message code="menu.title"/>
                        <c:forEach items="${restaurant.dishes}" var="dish">
                            <jsp:useBean id="dish" type="ru.ustinov.voting.model.Dish"/>
                            <li style="font-size: 10px">${dish.name} ${dish.price.stripTrailingZeros().toPlainString()} </li>
                        </c:forEach>
                    </ul>
                    <c:if test="${date.isAfter(dateNow) || date.equals(dateNow)}">
                        <div class="row"
                             style="margin-bottom: 10px; margin-top: auto; width: 100%; justify-content: space-evenly">
                            <button class="btn btn-primary" onclick="remove('${restaurant.id}', '${date}')">
                                <span class="fa fa-remove"></span>
                                <spring:message code="common.delete"/>
                            </button>
                            <a class="btn btn-primary" href="dishes/${restaurant.id}/${date}">
                                <span class="fa fa-pencil"></span>
                                <spring:message code="common.edit"/>
                            </a>
                        </div>
                    </c:if>
                </div>
            </c:forEach>
        </div>
        <br/>
    </div>
</div>

<div class="modal fade" tabindex="-1" id="select_restaurant">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="modalTitle"></h4>
                <button type="button" class="close" data-dismiss="modal" onclick="closeNoty()">&times;</button>
            </div>
            <div class="modal-body">
                <form:form id="detailsForm" method="get">
                    <div class="form-group">
                        <label for="name" class="col-form-label"><spring:message code="menu.restaurant"/></label>
                        <select type="text" class="form-control" id="name"/></select>
                    </div>
                </form:form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="closeNoty()">
                    <span class="fa fa-close"></span>
                    <spring:message code="common.cancel"/>
                </button>
                <button type="submit" form="detailsForm" class="btn btn-primary">
                    <span class="fa fa-check"></span>
                    <spring:message code="common.create"/>
                </button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="fragments/footer.jsp"/>
</body>
<jsp:include page="fragments/i18n.jsp">
    <jsp:param name="page" value="menu"/>
</jsp:include>
</html>