<%@include file="inc/header.jsp" %>
<h2>Workflow</h2>
<%
if (request.getParameter("id") != null) {
    EGATSWorkflow o = null;
    try {
        o = EGATSWorkflowCache.get(request.getParameter("id"));
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
                <td><%=Util.getString(o.getClassPath())%></td>
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
                <td><%=Util.getString(o.getStatus())%></td>
            </tr>
            <tr>
                <td>Processes</td>
                <td>
                <%
                if (o.getProcessCount() > 0) {
                    List<String> processIDs = o.getProcesses();
                    List<EGATSProcess> processes = API.getProcesses(processIDs);
                    %><ol><%
                    for (EGATSProcess p : processes) {
                        %>
                        <li><a href="process.jsp?id=<%=p.getID()%>">
                            <% if (p.getName() != null && !p.getName().equals("")) { %>
                                <%=p.getName()%>
                            <% } else { %>
                                <%=p.getID()%>
                            <% } %>
                        </a></li>
                        <%
                    }
                    %></ol><%
                } else {
                    %>None<%
                }
                %>
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
        %><div>The workflow could not be retrieved.</div><%
    }
}
%>
<%@include file="inc/footer.jsp" %>