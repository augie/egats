<%@include file="inc/header.jsp" %>
<h2>Process</h2>
<%
if (request.getParameter("id") != null) {
    EGATProcess o = EGATProcessCache.get(request.getParameter("id"));
    %>
    <table class="process">
        <tr>
            <td width="15%">ID</td>
            <td><%=o.getID()%></td>
        </tr>
        <tr>
            <td>Name</td>
            <td><%=o.getName()%></td>
        </tr>
        <tr>
            <td>Script</td>
            <td><%=o.getMethodPath()%></td>
        </tr>
        <tr>
            <td>Args</td>
            <td>
                <ol>
                    <%
                    boolean python = o.getMethodPath().toLowerCase().endsWith(".py");
                    for (String arg : o.getArgs()) {
                        boolean link = !python;
                        String id = arg;
                        if (python && id.startsWith("egats-obj-file:")) {
                            id = id.replaceFirst("egats-obj-file:", "");
                            link = true;
                        }
                        out.println("<li>");
                        if (link) {
                            out.println("<a href=\"object.jsp?id=" + id + "\">");
                        }
                        out.println(arg);
                        if (link) {
                            out.println("</a>");
                        }
                        out.println("</li>");
                    }
                    %>
                </ol>
            </td>
        </tr>
        <tr>
            <td>Status</td>
            <td><%=o.getStatus()%></td>
        </tr>
        <tr>
            <td>Output</td>
            <td>
                <% if (o.getOutputID() == null) { %>
                null
                <% } else { %>
                <a href="object.jsp?id=<%=o.getOutputID()%>"><%=o.getOutputID()%></a>
                <% } %>
            </td>
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
            <td><%=o.getExceptionMessage()%></td>
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