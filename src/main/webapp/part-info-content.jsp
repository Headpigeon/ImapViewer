<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<table class="table table-sm table-bordered table-no-outer-border part">
    <c:if test="${not empty part.from}">
        <tr>
            <td class="key">From:</td>
            <td class="value"><c:out value="${part.from}" /></td>
        </tr>
    </c:if>
    <c:if test="${not empty part.replyTo}">
        <tr>
            <td class="key">Reply To</td>
            <td class="value"><c:out value="${part.replyTo}" /></td>
        </tr>
    </c:if>
    <c:if test="${not empty part.recipients}">
        <tr>
            <td class="key">Recipients</td>
            <td class="value"><c:out value="${part.recipients}" /></td>
        </tr>
    </c:if>
    <c:if test="${not empty part.subject}">
        <tr>
            <td class="key">Subject</td>
            <td class="value"><c:out value="${part.subject}" /></td>
        </tr>
    </c:if>
    <c:if test="${part.sentDate != null}">
        <tr>
            <td class="key">Sent Date:</td>
            <td class="value"><fmt:formatDate pattern="dd.MM.yyyy HH:mm:ss" value="${part.sentDate}" /></td>
        </tr>
    </c:if>
    <c:if test="${part.receivedDate != null}">
        <tr>
            <td class="key">Received Date:</td>
            <td class="value"><fmt:formatDate pattern="dd.MM.yyyy HH:mm:ss" value="${part.receivedDate}" /></td>
        </tr>
    </c:if>
    <c:if test="${not empty part.contentType}">
        <tr>
            <td class="key">Content-Type:</td>
            <td class="value"><c:out value="${part.contentType}" /></td>
        </tr>
    </c:if>
    <c:if test="${not empty part.description}">
        <tr>
            <td class="key">Description:</td>
            <td class="value"><c:out value="${part.description}" /></td>
        </tr>
    </c:if>
    <c:if test="${not empty part.disposition}">
        <tr>
            <td class="key">Disposition:</td>
            <td class="value"><c:out value="${part.disposition}" /></td>
        </tr>
    </c:if>
    <c:if test="${not empty part.filename}">
        <tr>
            <td class="key">Filename:</td>
            <td class="value"><c:out value="${part.filename}" /></td>
        </tr>
    </c:if>
    <c:if test="${part.size != null}">
        <tr>
            <td class="key">Size (bytes):</td>
            <td class="value"><c:out value="${part.size}" /></td>
        </tr>
    </c:if>
    <c:if test="${not empty part.headers}">
        <tr>
            <td class="key">Headers:</td>
            <td>
                <table class="table table-sm table-bordered table-no-outer-border">
                <c:forEach var="h" items="${part.headers}">
                    <tr>
                        <td class="key"><c:out value="${h.name}" />:</td>
                        <td class="value"><c:out value="${h.value}" /></td>
                    </tr>
                </c:forEach>
                </table>
            </td>
        </tr>
    </c:if>
    <c:if test="${not empty part.subParts}">
        <tr>
            <td class="key">Sub-Parts:</td>
            <td>
                <c:forEach var="subPart" items="${part.subParts}">
                    <c:set var="part" value="${subPart}" scope="request" />
                    <jsp:include page="/part-info-content.jsp" />
                </c:forEach>
            </td>
        </tr>
    </c:if>
</table>
