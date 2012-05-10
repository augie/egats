<%@include file="inc/header.jsp" %>
<h2>Workflow</h2>
<%
if (request.getParameter("id") != null) {
    EGATSWorkflow o = EGATSWorkflowCache.get(request.getParameter("id"));
    %>
    <table class="process">
        <tr>
            <td width="15%">ID</td>
            <td><%=o.getID()%></td>
        </tr>
        <tr>
            <td>Name</td>
            <td><%=WebUtil.getString(o.getName())%></td>
        </tr>
        <tr>
            <td>Script</td>
            <td><%=WebUtil.getString(o.getClassPath())%></td>
        </tr>
        <tr>
            <td>Args</td>
            <td>
                <ol>
                    <%
                    for (String arg : o.getArgs()) {
                        String id = arg;
                        out.println("<li>");
                        out.println("<a href=\"object.jsp?id=" + id + "\">");
                        out.println(arg);
                        out.println("</a>");
                        out.println("</li>");
                    }
                    %>
                </ol>
            </td>
        </tr>
        <tr>
            <td>Status</td>
            <td><%=WebUtil.getString(o.getStatus())%></td>
        </tr>
        <tr>
            <td>Created</td>
            <td><%=WebUtil.getDate(o.getCreateTime())%></td>
        </tr>
        <tr>
            <td>Started</td>
            <td><%=WebUtil.getDate(o.getStartTime())%></td>
        </tr>
        <tr>
            <td>Finished</td>
            <td><%=WebUtil.getDate(o.getFinishTime())%></td>
        </tr>
        <tr>
            <td>Exception</td>
            <td><%=WebUtil.getString(o.getExceptionMessage())%></td>
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