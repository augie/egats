<%@include file="inc/header.jsp" %>
<h2>Processes</h2>
<div class="processes">
<%    
List<EGATSProcess> list = EGATSProcessCache.get();
Collections.reverse(list);
for (EGATSProcess p : list) {
    %>
    <div class="process">
        <div class="name">
            <a href="process.jsp?id=<%=p.getID()%>">
                <% if (p.getName() != null && !p.getName().equals("")) { %>
                    <%=p.getName()%>
                <% } else { %>
                    <%=p.getID()%>
                <% } %>
            </a>
        </div>
        <div class="status"><%=p.getStatus()%></div>
        <div class="create-time">(Created <%=Util.getDate(p.getCreateTime())%>)</div>
    </div>
    <%            
}
%>
</div>
<%@include file="inc/footer.jsp" %>