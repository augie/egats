<%@include file="inc/header.jsp" %>
<h2>Processes</h2>
<div class="processes">
<%    
List<EGATProcess> list = EGATProcessCache.get();
for (EGATProcess p : list) {
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
    </div>
    <%            
}
%>
</div>
<%@include file="inc/footer.jsp" %>