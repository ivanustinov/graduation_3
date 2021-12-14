<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script type="text/javascript">
    var i18n = [];
    let page;
    <c:if test="${not empty param.page}">
    page = '${param.page}';
    i18n["addTitle"] = '<spring:message code="${param.page}.add"/>';
    i18n["editTitle"] = '<spring:message code="${param.page}.edit"/>';
    i18n["saved"] = '<spring:message code="${param.page}.saved"/>';
    i18n["deleted"] = '<spring:message code="${param.page}.deleted"/>';
    </c:if>

    // user.add/user.edit, restaurant.add/restaurant.edit, menu.add/menu.edit, dish.add/dish.edit;
    <c:forEach var='key' items='<%=new String[]{"common.restaurant", "common.deleted", "common.saved", "commons.saved", "common.copied",
     "common.search", "common.confirm", "common.voted", "voting.result", "voting.time_enabled", "dish.rubles"}%>'>
    i18n['${key}'] = '<spring:message code="${key}"/>';
    </c:forEach>
</script>