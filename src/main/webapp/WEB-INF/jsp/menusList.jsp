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
<script type="text/javascript">
    var dateFormat = "${dateFormat}";
</script>
<jsp:include page="fragments/bodyHeader.jsp"/>

<div class="jumbotron pt-4">
    <div class="container">
        <div class="card-body pb-0">
            <div class="row justify-content-end">
                <div class="col-4">
                    <label class="myLabel" for="timeZone"><spring:message
                            code="voting.time_zone"/></label>
                    <div class="form-inline my-2 align-items-stretch" style="justify-content: flex-end">
                        <select style="margin-right: 5px; width: 80%" class="form-control" name="timeZone"
                                id="timeZone"></select>
                        <button class="btn btn-info mr-1" onclick="setTimeZone()">
                            <span class="fa fa-check"></span>
                        </button>
                    </div>
                </div>
                <div class="col-3">
                    <div style="margin-block-end: 1em">
                        <label class="myLabel" for="time"><spring:message
                                code="voting.time"/></label>
                        <div class="form-inline my-2 align-items-stretch" style="justify-content: flex-end">
                            <input style="margin-right: 5px;" class="form-control" type="time" name="time" id="time"/>
                            <button class="btn btn-info mr-1" onclick="setTime()">
                                <span class="fa fa-check"></span>
                                <%--                            <spring:message code="common.establish"/>--%>
                            </button>
                        </div>
                    </div>
                </div>
                <div class="col-3">
                    <form id="dateform">
                        <label class="myLabel" for="date"><spring:message
                                code="menu.date"/></label>
                        <div class="form-inline my-2 align-items-stretch" style="justify-content: flex-end">
                            <input style="margin-right: 5px; width: 50%" class="form-control" name="date" id="date"/>
                            <button class="btn btn-info mr-1" onclick="createMenu()">
                                <span class="fa fa-plus"></span>
                                <spring:message code="common.add"/>
                            </button>
                        </div>
                    </form>
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

<div class="modal fade" tabindex="-1" id="editRow">
    <div class="modal-dialog">
        <div style="width: 600px" class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="modalTitle"></h4>
                <button type="button" class="close" data-dismiss="modal" onclick="closeNoty()">&times;</button>
            </div>
            <div class="modal-body">
                <form id="detailsForm">
                </form>
            </div>
        </div>
    </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
</body>
<jsp:include page="fragments/i18n.jsp"/>
</html>