<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div class="row">
        <div class="col-12">
            <div class="card">
                <h3 class="card-header">
                    IMAP Viewer

                    <div class="float-right">
                        <button id="refresh" class="btn btn-sm btn-primary">
                            <i class="fas fa-sync-alt"></i> Refresh
                        </button>
                    </div>
                </h3>

                <div class="card-body" id="mailList">
                    <div class="row header">
                        <div class="col-12 col-md-4">Sender</div>
                        <div class="col-12 col-md-2">Sent Date</div>
                        <div class="col-12 col-md-6">Subject</div>
                    </div>
                    <div class="row">
                        <div id="mailListContent" class="col-12">
                            <c:forEach var="msg" items="${msgs.messages}">
                                <div class="row" data-message-number="${msg.number}">
                                    <div class="col-12 col-md-4"><c:out value="${msg.sender}" /></div>
                                    <div class="col-12 col-md-2"><fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${msg.date}" /></div>
                                    <div class="col-12 col-md-6"><c:out value="${msg.subject}" /></div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="mailContentWrapper" class="card mt-4 d-none">
        <div class="card-header">
            <ul class="nav nav-tabs card-header-tabs">
                <li class="nav-item">
                    <a class="nav-link disabled" id="mailTextTab" data-toggle="tab" href="#mailText">
                        Text
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link disabled" id="mailHtmlTab" data-toggle="tab" href="#mailHtml">
                        HTML
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link disabled" id="mailDetailsTab" data-toggle="tab" href="#mailDetails">
                        Details
                    </a>
                </li>
            </ul>
        </div>

        <div class="card-header d-none" id="attachments">
            <ul class="nav nav-pills" id="attachmentList">
                <li class="nav-item">
                    <a class="nav-link disabled" href="#">Attachments</a>
                </li>
            </ul>
        </div>

        <div class="card-body">
            <div class="tab-content" id="myTabContent">
                <div class="tab-pane" id="mailText"></div>
                <div class="tab-pane" id="mailHtml"><iframe id="mailHtmlContent" src="javascript:void(0);"></iframe></div>
                <div class="tab-pane" id="mailDetails"></div>
            </div>
        </div>
    </div>
</div>

<%@include file="/WEB-INF/jspf/footer.jspf" %>
