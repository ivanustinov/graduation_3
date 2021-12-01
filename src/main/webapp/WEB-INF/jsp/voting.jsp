<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" import="java.time.LocalDate" %>
<html>
<jsp:include page="fragments/headTag.jsp"/>
<body>
<script type="text/javascript" src="resources/js/voting.voting.js" defer></script>
<jsp:include page="fragments/bodyHeader.jsp"/>

<div class="jumbotron pt-4">
    <div class="container">
        <div class="row justify-content-end" style="line-height: 1">
            <div class="row" style="align-items:center;font-size: 20px; margin-right: 10px; border-radius: 0.25rem;"
                 id="background">
                <label for="votingTime" style="margin-right: 5px; margin-bottom: 0; margin-left: 5px;"><spring:message
                        code="voting.time_left"/></label>
                <p style="margin-bottom: 0; margin-right: 5px;" id="votingTime"></p>
            </div>
            <button class="btn btn-primary" onclick="getMyVotes()">
                <spring:message code="voting.history"/>
            </button>
        </div>
        <h3 style="margin-top: 30px; margin-bottom: 20px" class="text-center"><spring:message code="menu.on"/> <%=LocalDate.now()%></h3>
        <div class="row justify-content-md-center text-center">
            <%--https://getbootstrap.com/docs/4.0/components/card/--%>
            <c:forEach items="${restaurants}" var="restaurant">
                <jsp:useBean id="restaurant" type="ru.ustinov.voting.model.Restaurant"/>
                <div class="card mycard col-3" id="${restaurant.id}">
                    <h3 class="my-0 font-weight-normal">${restaurant.name}</h3>
                    <ul class="list-unstyled mt-3 mb-4"><spring:message code="menu.title"/>
                        <c:forEach items="${restaurant.dishes}" var="dish">
                            <jsp:useBean id="dish" type="ru.ustinov.voting.model.Dish"/>
                            <li style="font-size: 10px">${dish.name} ${dish.price.stripTrailingZeros().toPlainString()} </li>
                        </c:forEach>
                    </ul>
                    <button class="btn btn-primary mybutton" onclick="vote(${restaurant.id})">
                        <span class="fa fa-check"></span>
                        <spring:message code="voting.vote"/>
                    </button>
                </div>
            </c:forEach>
        </div>
        <div class="row justify-content-end" id="votebutton">
        </div>
        <br/>
    </div>
</div>

<div class="modal fade" tabindex="-1" id="result">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="modalTitle"></h4>
                <button type="button" class="close" data-dismiss="modal" onclick="closeNoty()">&times;</button>
            </div>
            <div class="modal-body">
                <div class="justify-content-md-center text-center">
                    <h3 class="my-0 font-weight-normal" id="restaurant_name"></h3>
                    <ul class="list-unstyled mt-3 mb-4" id="dishes"><spring:message code="menu.title"/></ul>
                    <label><spring:message code="voting.count"/></label>
                    <h5 style="display: inline-block" id="count"></h5>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" tabindex="-1" id="votes">
    <div class="modal-dialog" style="max-width: 600px">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="my_votes_title"></h4>
                <button type="button" class="close" data-dismiss="modal" onclick="closeNoty()">&times;</button>
            </div>
            <div class="modal-body">
                <h4 class="text-center"><spring:message code="voting.history"/></h4>
                <table class="table table-striped" style="width: 100%" id="my_votes">
                    <thead>
                    <tr>
                        <th><spring:message code="restaurant.one"/></th>
                        <th><spring:message code="common.date"/></th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
</body>
<jsp:include page="fragments/i18n.jsp"/>
</html>