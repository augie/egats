<%@page import="org.apache.commons.fileupload.*, org.apache.commons.fileupload.servlet.*, org.apache.commons.fileupload.disk.*, org.apache.commons.fileupload.util.*" %>
<%@include file="inc/header.jsp" %>
<h2>New Workflow</h2>
<%
String error = null;
if (request.getMethod().equals("POST")) {
    try {
        // Handle multi-part form data request
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = upload.parseRequest(request);
        
        // Place form fields into a map
        Map<String, String> formFields = new HashMap<String, String>();
        // Work with the file items later
        Set<FileItem> fileItems = new HashSet<FileItem>();
        
        // Process the uploaded form field items
        Iterator<FileItem> iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = iter.next();
            if (item.isFormField()) {
                formFields.put(item.getFieldName(), item.getString());
            } else {
                fileItems.add(item);
            }
        }
        
        // Get the inputs
        String name = formFields.get("name");
        
        String workflow = formFields.get("workflow");
        if (workflow == null) {
            throw new Exception("Workflow is null.");
        }
        workflow = workflow.trim();
        if (workflow.equals("")) {
            throw new Exception("Workflow is blank.");
        }
        
        String argCountString = formFields.get("argCount");
        if (argCountString == null) {
            throw new Exception("Arg count is null.");
        }
        int argCount = Integer.valueOf(argCountString.trim());
        
        // Read the args
        ArrayList<String> args = new ArrayList<String>();
        for (int argID = 0; argID < argCount; argID++) {
            String classPath = formFields.get("arg" + argID + "ClassPath");
            String object = null;
            if (formFields.containsKey("arg" + argID)) {
                object = formFields.get("arg" + argID);
            } else {
                for (FileItem fileItem : fileItems) {
                    if (fileItem.getFieldName().equals("arg" + argID)) {
                        object = IOUtils.toString(fileItem.getInputStream());
                        break;
                    }
                }
            }

            // Create the object
            EGATSObject egatsObject = new EGATSObject();
            egatsObject.setClassPath(classPath);
            egatsObject.setObject(object);
            String id = API.createObject(egatsObject);

            // Set the argument
            args.add(id);
        }
        
        // Create a workflow to run
        EGATSWorkflow egatsWorkflow = new EGATSWorkflow();
        egatsWorkflow.setName(name);
        egatsWorkflow.setClassPath(workflow);
        egatsWorkflow.setArgs(args.toArray(new String[0]));
        String id = API.createWorkflow(egatsWorkflow);

        // Redirect the user to the process page
        %>
        <p>You are being redirected to <a href="workflow.jsp?id=<%=id%>">your new workflow</a>.</p>
        <script type="text/javascript">
            $(location).attr('href', 'workflow.jsp?id=<%=id%>');
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
    <script type="text/javascript">
        var argID = 0;
        
        function changedWorkflow() {
            if ($("#workflow").val() == '') {
                $("#addArg").attr('disabled', 'disabled');
                $("#addFile").attr('disabled', 'disabled');
                $("#args").html("<i>Select a workflow.</i>");
            } else {
                $("#addArg").removeAttr('disabled');
                $("#addFile").removeAttr('disabled');
                $("#args").html('');
            }
        }

        function getNextArgID() {
            var id = argID++;
            $("#argCount").val(argID);
            return id;
        }

        function addArgInput() {
            var id = getNextArgID();
            $("#args").append("<div id=\"arg" + id + "\">Class path: <input name=\"arg" + id + "ClassPath\" type=\"text\"/><br/>Object: <textarea name=\"arg" + id + "\"></textarea></div><br/>");
        }

        function addFileArgInput() {
            var id = getNextArgID();
            $("#args").append("<div id=\"arg" + id + "\">Class path: <input name=\"arg" + id + "ClassPath\" type=\"text\"/><br/>Object: <input name=\"arg" + id + "\" type=\"file\"></div><br/>");
        }

        function resetArgs() {
            argID = 0;
            $("#argCount").val(argID);
            $("#args").html('');
        }
    </script>
    <form action="create-workflow.jsp" enctype="multipart/form-data" method="post">
        <table>
            <tr>
                <td width="15%">Name</td>
                <td><input name="name" type="text"/></td>
            </tr>
            <tr>
                <td>Workflow</td>
                <td>
                    <select id="workflow" name="workflow" onchange="changedWorkflow()">
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
                    <button id="addArg" onclick="addArgInput()" type="button" disabled>+ Arg</button><br/>
                    <button id="addFile" onclick="addFileArgInput()" type="button" disabled>+ File</button><br/>
                    <button onclick="resetArgs()" type="button">Reset</button>
                </td>
                <td>
                    <input id="argCount" name="argCount" type="hidden" value="0" />
                    <div id="args">
                        <i>Select a workflow.</i>
                    </div>
                </td>
            </tr>
            <tr><td colspan="2"><button type="submit">Submit</button></td></tr>
        </table>
    </form>
    <%
}                    
%>
<%@include file="inc/footer.jsp" %>