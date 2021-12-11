<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<jsp:include page="fragments/headTag.jsp"/>
<body>
<script src="resources/js/voting.common.js" defer></script>
<script src="resources/js/voting.restaurants.js" defer></script>
<jsp:include page="fragments/bodyHeader.jsp"/>
<div class="jumbotron pt-4">
    <div class="container">
        <div style="display: flex; justify-content: flex-end">
            <button class="btn btn-secondary" style="margin-right: 10px" onclick="window.history.back()">
                <span class="fa fa-arrow-left"></span>
                <spring:message code="common.back"/>
            </button>
            <button class="btn btn-primary" onclick="add()">
                <span class="fa fa-plus"></span>
                <spring:message code="common.add"/>
            </button>
        </div>
        <h3 class="text-center"><spring:message code="restaurants.title"/></h3>
        <table class="table table-striped" id="datatable">
            <thead>
            <tr>
                <th><spring:message code="restaurants.title"/></th>
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
                    <div class="form-group">
                        <label for="name"  class="col-form-label"><spring:message code="restaurant.name"/></label>
                        <input style="width: 400px" type="text" id="name" name="name"/>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal" onclick="closeNoty()">
                            <span class="fa fa-close"></span>
                            <spring:message code="common.cancel"/>
                        </button>
                        <button type="button" class="btn btn-primary" onclick="save()">
                            <span class="fa fa-check"></span>
                            <spring:message code="common.save"/>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="fragments/footer.jsp"/>
</body>
<jsp:include page="fragments/i18n.jsp">
    <jsp:param name="page" value="restaurant"/>
</jsp:include>
</html>