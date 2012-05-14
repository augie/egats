<%@include file="inc/header.jsp" %>
<h2>Process</h2>
<%
if (request.getParameter("id") != null) {
    EGATSProcess o = null;
    try {
        o = EGATSProcessCache.get(request.getParameter("id"));
    } catch (Exception e) {
    }
    if (o != null) {
        %>
        <table class="process">
            <tr>
                <td width="15%">ID</td>
                <td><%=o.getID()%></td>
            </tr>
            <tr>
                <td>Name</td>
                <td><%=Util.getString(o.getName())%></td>
            </tr>
            <tr>
                <td>Script</td>
                <td><%=Util.getString(o.getMethodPath())%></td>
            </tr>
            <tr>
                <td>Args</td>
                <td>
                    <ol>
                        <%
                        if (o.getMethodPath() != null) {
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
                        }
                        %>
                    </ol>
                </td>
            </tr>
            <tr>
                <td>Status</td>
                <td><%=Util.getString(o.getStatus())%></td>
            </tr>
            <tr>
                <td>Output</td>
                <td>
                    <% if (o.getOutputID() == null) { %>
                    null
                    <% } else { %>
                    <a href="object.jsp?id=<%=o.getOutputID()%>"><%=o.getOutputID()%></a> (<a href="object-dl.jsp?id=<%=o.getOutputID()%>">Download</a>)
                    <% } %>
                </td>
            </tr>
            <tr>
                <td>Created</td>
                <td><%=Util.getDate(o.getCreateTime())%></td>
            </tr>
            <tr>
                <td>Started</td>
                <td><%=Util.getDate(o.getStartTime())%></td>
            </tr>
            <tr>
                <td>Finished</td>
                <td><%=Util.getDate(o.getFinishTime())%></td>
            </tr>
            <tr>
                <td>Exception</td>
                <td><%=Util.getString(o.getExceptionMessage())%></td>
            </tr>
            <tr>
                <td>Raw JSON</td>
                <td><div style="font-size: 8pt;"><%=o%></div></td>
            </tr>
        </table>
        <%
    } else {
        %><div>The process could not be retrieved.</div><%
    }
}
%>
<%@include file="inc/footer.jsp" %>