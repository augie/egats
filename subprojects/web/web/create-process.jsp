<%@include file="inc/header.jsp" %>
<h2>Create a Process</h2>
<form action="create-process.jsp" enctype="multipart/form-data" method="post">
    <table>
        <tr>
            <td width="15%">Name</td>
            <td><input name="name" type="text"/></td>
        </tr>
        <tr>
            <td>Process</td>
            <td>
                <select name="process">
                    <option value=""></option>
                    <% for (String p : API.getToolkit()) { %>
                    <option value="<%=p%>"><%=p%></option>
                    <% } %>
                </select>
            </td>
        </tr>
        <tr>
            <td>Inputs</td>
            <td>
                <input name="input-0" type="file"/>
            </td>
        </tr>
    </table>
</form>
<%@include file="inc/footer.jsp" %>