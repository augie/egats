<%@include file="inc/header.jsp" %>
<h2>Create a Process</h2>
<%
String error = null;
if (request.getMethod().equals("POST")) {
    try {
        // Get the inputs
        Part namePart = request.getPart("name");
        if (namePart == null) {
            throw new Exception("Name cannot be blank.");
        }
        String name = namePart.toString();
        if (name == null || name.equals("")) {
            throw new Exception("Name cannot be blank.");
        }
        
        Part processPart = request.getPart("process");
        if (processPart == null) {
            throw new Exception("Process cannot be blank.");
        }
        String process = processPart.toString();
        if (process == null || process.equals("")) {
            throw new Exception("Process cannot be blank.");
        }
        
        String[] args = new String[0];
        if (args == null) {
            throw new Exception("Args is null.");
        }

        // Create an EGAT process to run
        String id = API.createProcess(name, process, args);

        // Redirect the user to the process page
        %>
        <p>You are being redirected to <a href="process.jsp?id=<%=id%>">your new process</a>.</p>
        <script type="text/javascript">
            $(location).attr('href', 'process.jsp?id=<%=id%>');
        </script>
        <%
    } catch(Exception e) {
        error = e.getMessage();
    }
}
if (request.getMethod().equals("GET") || error != null) {
    if (error != null) {
        %><p>The following error occurred:<br/><%=error%></p><%
    }
    %>
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
                <td>
                    Args<br/>
                    <button onclick="addArgInput()" type="button">+Arg</button><br/>
                    <button onclick="addFileArgInput()" type="button">+File</button>
                </td>
                <td>
                    <script type="text/javascript">
                        var argID = 0;
                        function getNextArgID() {
                            return argID++;
                        }

                        function addArgInput() {
                            var id = getNextArgID();
                            $("#args").append("<div id=\"arg" + id + "\"><input name=\"arg" + id + "\" type=\"text\"></div>");
                        }

                        function addFileArgInput() {
                            var id = getNextArgID();
                            $("#args").append("<div id=\"arg" + id + "\"><input name=\"arg" + id + "\" type=\"file\"></div>");
                        }
                    </script>
                    <div id="args"></div>
                </td>
            </tr>
            <tr><td colspan="2"><button type="submit">Submit</button></td></tr>
        </table>
    </form>
    <%
}                    
%>
<%@include file="inc/footer.jsp" %>