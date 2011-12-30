<%@include file="inc/header.jsp" %>
<h2>Object</h2>
<%
if (request.getParameter("id") != null) {
    EGATProcess o = EGATProcessCache.get(request.getParameter("id"));
    %><%=o.getJSON()%><%
}
%>
<%@include file="inc/footer.jsp" %>