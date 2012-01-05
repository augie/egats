<%@include file="inc/header.jsp" %>
<h2>Object</h2>
<%
if (request.getParameter("id") != null) {
    EGATSObject o = EGATSObjectCache.get(request.getParameter("id"));
    %>
    <table class="process">
        <tr>
            <td width="15%">ID</td>
            <td><%=WebUtil.getString(o.getID())%></td>
        </tr>
        <tr>
            <td>Class</td>
            <td><%=WebUtil.getString(o.getClassPath())%></td>
        </tr>
        <tr>
            <td>Created</td>
            <td><%=WebUtil.getDate(o.getCreateTime())%></td>
        </tr>
        <tr>
            <td>
                Object<br/>
                <a href="object-dl.jsp?id=<%=o.getID()%>">Download</a>
            </td>
            <td>
                <% if (o.getObject() != null) { %>
                    <%=o.getObject().replaceAll("\n", "<br/>")%>
                <% } %>
            </td>
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