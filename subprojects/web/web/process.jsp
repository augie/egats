<%@include file="inc/header.jsp" %>
<h2>Process</h2>
<%
if (request.getParameter("id") != null) {
    EGATProcess o = EGATProcessCache.get(request.getParameter("id"));
    %>
    <div class="item">
        <div class="name">ID</div>
        <div class="value"><%=o.getID()%></div>
    </div>
    <div class="item">
        <div class="name">Args</div>
        <div class="value">
            <ol>
                <% for (String arg : o.getArgs()) { %>
                <li><a href="object.jsp?id=<%=arg%>"><%=arg%></a></li>
                <% } %>
            </ol>
        </div>
    </div>
    <%
}
%>
<%@include file="inc/footer.jsp" %>