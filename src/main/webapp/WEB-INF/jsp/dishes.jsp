<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://voting.ustinov.ru/functions" %>
<%@ page contentType="text/html;charset=UTF-8" import="java.time.LocalDate" %>

<html>
<jsp:include page="fragments/headTag.jsp"/>
<body>
<script>
    let dDate = "${fn:formatDate(date)}";
    let restaurant_id = '${restaurant.id}'
</script>
<script src="resources/js/voting.common.js"></script>
<script src="resources/js/voting.dishes.js" defer></script>
<jsp:include page="fragments/bodyHeader.jsp"/>

<div class="jumbotron pt-4">
    <div class="container">
        <div class="row" style="justify-content: flex-end">
            <button class="btn btn-secondary" style="margin-right: 10px" onclick="window.history.back()">
                <span class="fa fa-arrow-left"></span>
                <spring:message code="common.back"/>
            </button>
            <button style="margin-right: 5px" class="btn btn-primary" onclick="add()">
                <span class="fa fa-plus"></span>
                <spring:message code="common.add"/>
            </button>
            <c:if test="${!date.isBefore(LocalDate.now())}">
                <a href="delete_all_dishes?date=${fn:formatDate(date)}&restaurant_id=${restaurant.id}"
                   style="margin-right: 5px" class="btn btn-primary">
                    <span class="fa fa-remove"></span>
                    <spring:message code="common.delete_all"/>
                </a>
            </c:if>
            <button class="btn btn-primary" onclick="getLastMenu()">
                <span class="fa fa-plus"></span>
                <spring:message code="dish.last"/>
            </button>
        </div>
        <c:if test="${date.isBefore(LocalDate.now())}">
            <h4 style="margin-top: 30px; margin-bottom: 20px" class="text-center"><spring:message
                    code="dish.create_in_the_past"/></h4>
        </c:if>
        <h3 style="margin-top: 30px" class="text-center">${restaurant.name}
            <spring:message code="common.on"/> ${fn:formatDate(date)}</h3>
        <table class="table table-striped" id="datatable">
            <thead>
            <tr>
                <th><spring:message code="dish.name"/></th>
                <th><spring:message code="dish.price"/></th>
                <th><spring:message code="common.edit"/></th>
                <th><spring:message code="common.delete"/></th>
            </tr>
            </thead>
        </table>
    </div>
</div>

<div class="modal fade" tabindex="-1" id="editRow">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="modalTitle"></h4>
                <button type="button" class="close" data-dismiss="modal" onclick="closeNoty()">&times;</button>
            </div>
            <div class="modal-body">
                <form id="detailsForm">
                    <input type="hidden" id="id" name="id">
                    <input type="hidden" id="date" name="date">
                    <div class="form-group">
                        <label for="name" class="col-form-label"><spring:message code="dish.name"/></label>
                        <input style="width: 400px" type="text" id="name" name="name"/>
                    </div>
                    <div class="form-group">
                        <label for="price" class="col-form-label"><spring:message code="dish.price"/></label>
                        <input type="number" min="0.00" step="0.05" value="1.00" id="price" name="price"
                               placeholder="00.00">
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="closeNoty()">
                            <span class="fa fa-close"></span>
                            <spring:message code="common.cancel"/>
                        </button>
                        <button type="button" class="btn btn-primary" onclick="fillDate()">
                            <span class="fa fa-check"></span>
                            <spring:message code="common.save"/>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" tabindex="-1" id="lastMenu">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="lastMenuModalTitle"><spring:message code="menu.last"/></h4>
                <button type="button" class="close" data-dismiss="modal" onclick="closeNoty()">&times;</button>
            </div>
            <div class="modal-body">
                <h4 class="text-center"><spring:message code="dish.dishes"/></h4>
                <table class="table table-striped" style="width: 100%" id="lastMenuTable">
                    <thead>
                    <tr>
                        <th><spring:message code="dish.name"/></th>
                        <th><spring:message code="dish.price"/></th>
                        <th><spring:message code="common.select"/></th>
                    </tr>
                    </thead>
                </table>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="closeNoty()">
                        <span class="fa fa-close"></span>
                        <spring:message code="common.cancel"/>
                    </button>
                    <button type="button" class="btn btn-primary" onclick="sentDishes()">
                        <span class="fa fa-check"></span>
                        <spring:message code="common.save"/>
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="fragments/footer.jsp"/>
</body>
<jsp:include page="fragments/i18n.jsp">
    <jsp:param name="page" value="dish"/>
</jsp:include>
</html>