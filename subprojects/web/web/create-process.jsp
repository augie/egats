<%@page import="org.apache.commons.fileupload.*, org.apache.commons.fileupload.servlet.*, org.apache.commons.fileupload.disk.*, org.apache.commons.fileupload.util.*" %>
<%@page import="java.io.IOException, org.apache.http.client.ClientProtocolException, org.apache.http.client.HttpClient, org.apache.http.client.ResponseHandler, org.apache.http.client.methods.HttpGet, org.apache.http.impl.client.BasicResponseHandler, org.apache.http.impl.client.DefaultHttpClient" %>
<%@include file="inc/header.jsp" %>
<h2>New Process</h2>
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
        
        String process = formFields.get("process");
        if (process == null) {
            throw new Exception("Process is null.");
        }
        process = process.trim();
        if (process.equals("")) {
            throw new Exception("Process is blank.");
        }
        boolean isPyProcess = process.endsWith(".py");
        
        String argCountString = formFields.get("argCount");
        if (argCountString == null) {
            throw new Exception("Arg count is null.");
        }
        int argCount = Integer.valueOf(argCountString.trim());
        
        // Read the args
        ArrayList<String> args = new ArrayList<String>();
        for (int argID = 0; argID < argCount; argID++) {
            if (isPyProcess) {
                if (formFields.containsKey("arg" + argID)) {
                    args.add(formFields.get("arg" + argID));
                } else {
                    for (FileItem fileItem : fileItems) {
                        if (fileItem.getFieldName().equals("arg" + argID)) {
                            // Upload the object
                            String argObjID = API.createObjectFile(fileItem.getName(), IOUtils.toString(fileItem.getInputStream()));
                            // Set the arg
                            args.add("egats-obj-file:" + argObjID);
                            break;
                        }
                    }
                }
            } else {
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
        }
        
        if(session.getAttribute("json_file").toString()!="no-response"){
         String argObjID = API.createObjectFile(session.getAttribute("json_name").toString(), session.getAttribute("json_file").toString());
         args.add("egats-obj-file:" + argObjID);
         }
        
        // Create a process to run
        EGATSProcess egatsProcess = new EGATSProcess();
        egatsProcess.setName(name);
        egatsProcess.setMethodPath(process);
        egatsProcess.setArgs(args.toArray(new String[0]));
        String id = API.createProcess(egatsProcess);

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
    session.setAttribute("json_file", "no-response");
    if(request.getQueryString()!=null){
       HttpClient httpclient = new DefaultHttpClient();
       try {
           String objectUrl=request.getQueryString().substring(request.getQueryString().indexOf("objecturl=")+10, request.getQueryString().length());
           String responseBody = httpclient.execute(new HttpGet(objectUrl), new BasicResponseHandler());
           session.setAttribute("json_file", responseBody);
           session.setAttribute("json_name", request.getQueryString().substring(request.getQueryString().indexOf("games/")+6, request.getQueryString().indexOf("?auth_token")));
                } catch (ClientProtocolException e) {
		e.printStackTrace();
                } catch (IOException e) {
		e.printStackTrace();
                } finally {
                 httpclient.getConnectionManager().shutdown();
                }
    }
    %>
    <script type="text/javascript">
        var argID = 0;
        
        function changedProcess() {
            if ($("#process").val() == '') {
                $("#addArg").attr('disabled', 'disabled');
                $("#addFile").attr('disabled', 'disabled');
                $("#args").html("<i>Select a process.</i>");
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
            var process = $("#process").val();
            if (process.length >= 3 && process.substring(process.length - 3) == '.py') {
                $("#args").append("<div id=\"arg" + id + "\"><textarea name=\"arg" + id + "\"></textarea></div><br/>");
            } else {
                $("#args").append("<div id=\"arg" + id + "\">Class path: <input name=\"arg" + id + "ClassPath\" type=\"text\"/><br/>Object: <textarea name=\"arg" + id + "\"></textarea></div><br/>");
            }
        }

        function addFileArgInput() {
            var id = getNextArgID();
            var process = $("#process").val();
            if (process.length >= 3 && process.substring(process.length - 3) == '.py') {
                $("#args").append("<div id=\"arg" + id + "\"><input name=\"arg" + id + "\" type=\"file\"></div><br/>");
            } else {
                $("#args").append("<div id=\"arg" + id + "\">Class path: <input name=\"arg" + id + "ClassPath\" type=\"text\"/><br/>Object: <input name=\"arg" + id + "\" type=\"file\"></div><br/>");
            }
        }

        function resetArgs() {
            argID = 0;
            $("#argCount").val(argID);
            $("#args").html('');
        }
    </script>
    <form action="create-process.jsp" enctype="multipart/form-data" method="post">
        <table>
            <tr>
                <td width="15%">Name</td>
                <td><input name="name" type="text"/></td>
            </tr>
            <tr>
                <td>Process</td>
                
                <td>
                    <select id="process" name="process" onchange="changedProcess()">
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
                    <button id="addArg" onclick="addArgInput()" type="button" disabled>+ Arg</button> <br/>
                    <button id="addFile" onclick="addFileArgInput()" type="button" disabled>+ File</button><br/>
                    <button onclick="resetArgs()" type="button">Reset</button>
                </td>
                <td>
                    <% if (session.getAttribute("json_file").toString()=="no-response") { %>
                    <% } else { %>
                    Downloaded Object From: <%=request.getQueryString().substring(request.getQueryString().indexOf("objecturl=")+10, request.getQueryString().indexOf("?auth_token"))%>
                    <% } %>
                    <input id="argCount" name="argCount" type="hidden" value="0" />
                    <div id="args">
                        <i>Select a process.</i>
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