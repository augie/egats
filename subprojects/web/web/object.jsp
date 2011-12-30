<%@include file="inc/header.jsp" %>
<h2>Object</h2>
<%
if (request.getParameter("id") != null) {
    EGATSObject o = EGATSObjectCache.get(request.getParameter("id"));
    %><%=o.getJSON()%><%
}
%>
<%@include file="inc/footer.jsp" %>