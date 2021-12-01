<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<jsp:include page="fragments/headTag.jsp"/>
<body>
<script src="resources/js/voting.common.js" defer></script>
<script src="resources/js/voting.menusList.js" defer></script>
<jsp:include page="fragments/bodyHeader.jsp"/>

<div class="jumbotron pt-4">
    <div class="container">
        <div class="card-body pb-0">
            <div class="row justify-content-end">
                <div class="col-4 form-inline my-2">
                    <form id="dateform">
                        <label style="justify-content: left" for="date"><spring:message code="menu.date"/></label>
                        <input style="margin-right: 5px;" class="form-control" type="date" name="date" id="date"/>
                        <button class="btn btn-info mr-1" onclick="createMenu()">
                            <span class="fa fa-plus"></span>
                            <spring:message code="common.add"/>
                        </button>
                    </form>
                </div>
                <div class="col-4 form-inline my-2">
                    <div style="margin-block-end: 1em">
                        <label style="justify-content: left" for="time"><spring:message code="voting.time"/></label>
                        <input style="margin-right: 5px;" class="form-control" type="time" name="time" id="time"/>
                        <button class="btn btn-info mr-1" onclick="setTime()">
                            <span class="fa fa-plus"></span>
                            <spring:message code="common.establish"/>
                        </button
                    </div>
                </div>
            </div>
        </div>
        <h3 class="text-center"><spring:message code="menu_list.name"/></h3>
        <table class="table table-striped" id="datatable">
            <thead>
            <tr>
                <th><spring:message code="restaurants.title"/></th>
                <th><spring:message code="common.date"/></th>
                <th><spring:message code="common.edit"/></th>
                <th><spring:message code="common.delete"/></th>
                <th><spring:message code="common.copy"/></th>
            </tr>
            </thead>
        </table>
    </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
</body>
<jsp:include page="fragments/i18n.jsp"/>
</html>