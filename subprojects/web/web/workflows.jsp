<%@include file="inc/header.jsp" %>
<h2>Workflows</h2>
<div class="workflows">
<%    
List<EGATSWorkflow> list = EGATSWorkflowCache.get();
for (EGATSWorkflow w : list) {
    %>
    <div class="workflow">
        <div class="name">
            <a href="workflow.jsp?id=<%=w.getID()%>">
                <% if (w.getName() != null && !w.getName().equals("")) { %>
                    <%=w.getName()%>
                <% } else { %>
                    <%=w.getID()%>
                <% } %>
            </a>
        </div>
        <div class="status"><%=w.getStatus()%></div>
        <div class="create-time">(Created <%=WebUtil.getDate(w.getCreateTime())%>)</div>
    </div>
    <%
}
%>
</div>
<%@include file="inc/footer.jsp" %>