<%@include file="inc/header.jsp" %>
<h2>Object</h2>
<%
if (request.getParameter("id") != null) {
    EGATSObject o = EGATSObjectCache.get(request.getParameter("id"));
    %>
    <table class="process">
        <tr>
            <td width="15%">ID</td>
            <td><%=o.getID()%></td>
        </tr>
        <tr>
            <td>Class</td>
            <td><%=o.getClassPath()%></td>
        </tr>
        <tr>
            <td>Created</td>
            <td><%=WebUtil.getDate(o.getCreateTime())%></td>
        </tr>
        <tr>
            <td>Object</td>
            <td><%=o.getObject()%></td>
        </tr>
        <tr>
            <td>Raw JSON</td>
            <td><div style="font-size: 8pt;"><%=o%></div></td>
        </tr>
    </table>
    <%
}
%>
<%@include file="inc/footer.jsp" %>