<%@include file="inc/header.jsp" %>
<h2>Object</h2>
<%
if (request.getParameter("id") != null) {
    EGATSObject o = null;
    try {
        o = EGATSObjectCache.get(request.getParameter("id"));
    } catch (Exception e) {
    }
    if (o != null) {
        %>
        <table class="process">
            <tr>
                <td width="15%">ID</td>
                <td><%=Util.getString(o.getID())%></td>
            </tr>
            <tr>
                <td>Class</td>
                <td><%=Util.getString(o.getClassPath())%></td>
            </tr>
            <tr>
                <td>Created</td>
                <td><%=Util.getDate(o.getCreateTime())%></td>
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
    } else {
        %><div>The object could not be retrieved.</div><%
    }
}
%>
<%@include file="inc/footer.jsp" %>