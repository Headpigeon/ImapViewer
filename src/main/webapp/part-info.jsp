<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="part" value="${part}" scope="request" />
<jsp:include page="/part-info-content.jsp" />
