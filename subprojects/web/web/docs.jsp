<%@include file="inc/header.jsp" %>
<h2>EGATS ReST API</h2>

<div>All communication with the EGATS server is in JSON format. All objects are passed in their JSON representation.</div>

<br/>

<h3>Get Objects By ID</h3>
<table>
    <tr>
        <td width="25%">Method</td>
        <td>GET</td>
    </tr>
    <tr> 
        <td>URL</td>
        <td><%= API.getObjectURL("<i>$ids</i>") %></td>
    </tr>
    <tr>
        <td>Parameters</td>
        <td><i>$ids</i>: a comma-separated list of object IDs</td>
    </tr>
    <tr>
        <td>Response</td>
        <td><a href="https://github.com/augie/egats/blob/master/src/egats/Response.java">egats.Response</a> object</td>
    </tr>
    <tr>
        <td>Response body</td>
        <td>
            <div>When response statusCode = 200 (OK), a list of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSObject.java">egats.EGATSObject</a> objects</div>
            <div>When response statusCode = 300 (ERROR), an exception message</div>
            <div>When response statusCode = 404 (NOT FOUND), ID of the missing object</div>
        </td>
    </tr>
</table>
<br/>

<h3>Get Processes By ID</h3>
<table>
    <tr>
        <td width="25%">Method</td>
        <td>GET</td>
    </tr>
    <tr> 
        <td>URL</td>
        <td><%= API.getProcessURL("<i>$ids</i>") %></td>
    </tr>
    <tr>
        <td>Parameters</td>
        <td><i>$ids</i>: a comma-separated list of process IDs</td>
    </tr>
    <tr>
        <td>Response</td>
        <td><a href="https://github.com/augie/egats/blob/master/src/egats/Response.java">egats.Response</a> object</td>
    </tr>
    <tr>
        <td>Response body</td>
        <td>
            <div>When response statusCode = 200 (OK), a list of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSProcess.java">egats.EGATSProcess</a> objects</div>
            <div>When response statusCode = 300 (ERROR), an exception message</div>
            <div>When response statusCode = 404 (NOT FOUND), ID of the missing process</div>
        </td>
    </tr>
</table>
<br/>

<h3>Get Workflows By ID</h3>
<table>
    <tr>
        <td width="25%">Method</td>
        <td>GET</td>
    </tr>
    <tr> 
        <td>URL</td>
        <td><%= API.getWorkflowURL("<i>$ids</i>") %></td>
    </tr>
    <tr>
        <td>Parameters</td>
        <td><i>$ids</i>: a comma-separated list of workflow IDs</td>
    </tr>
    <tr>
        <td>Response</td>
        <td><a href="https://github.com/augie/egats/blob/master/src/egats/Response.java">egats.Response</a> object</td>
    </tr>
    <tr>
        <td>Response body</td>
        <td>
            <div>When response statusCode = 200 (OK), a list of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSWorkflow.java">egats.EGATSWorkflow</a> objects</div>
            <div>When response statusCode = 300 (ERROR), an exception message</div>
            <div>When response statusCode = 404 (NOT FOUND), ID of the missing workflow</div>
        </td>
    </tr>
</table>
<br/>

<h3>Get Processes By Timestamp</h3>
<table>
    <tr>
        <td width="25%">Method</td>
        <td>GET</td>
    </tr>
    <tr> 
        <td>URL</td>
        <td><%= API.HOST %><%= API.PROCESS_LIST_FOLDER %><i>$timestamp</i></td>
    </tr>
    <tr>
        <td>Parameters</td>
        <td><i>$timestamp</i>: a UNIX timestamp. All processes created on or after this time will be returned in chronological order.</td>
    </tr>
    <tr>
        <td>Response</td>
        <td><a href="https://github.com/augie/egats/blob/master/src/egats/Response.java">egats.Response</a> object</td>
    </tr>
    <tr>
        <td>Response body</td>
        <td>
            <div>When response statusCode = 200 (OK), a list of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSProcess.java">egats.EGATSProcess</a> objects</div>
            <div>When response statusCode = 300 (ERROR), an exception message</div>
        </td>
    </tr>
</table>
<br/>

<h3>Get Workflows By Timestamp</h3>
<table>
    <tr>
        <td width="25%">Method</td>
        <td>GET</td>
    </tr>
    <tr> 
        <td>URL</td>
        <td><%= API.HOST %><%= API.WORKFLOW_LIST_FOLDER %><i>$timestamp</i></td>
    </tr>
    <tr>
        <td>Parameters</td>
        <td><i>$timestamp</i>: a UNIX timestamp. All workflows created on or after this time will be returned in chronological order.</td>
    </tr>
    <tr>
        <td>Response</td>
        <td><a href="https://github.com/augie/egats/blob/master/src/egats/Response.java">egats.Response</a> object</td>
    </tr>
    <tr>
        <td>Response body</td>
        <td>
            <div>When response statusCode = 200 (OK), a list of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSWorkflow.java">egats.EGATSWorkflow</a> objects</div>
            <div>When response statusCode = 300 (ERROR), an exception message</div>
        </td>
    </tr>
</table>
<br/>

<h3>Create Objects</h3>
<table>
    <tr>
        <td width="25%">Method</td>
        <td>POST</td>
    </tr>
    <tr> 
        <td>URL</td>
        <td><%= API.CREATE_OBJECT_URL %></td>
    </tr>
    <tr>
        <td>Body</td>
        <td>
            <div>A list of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSObject.java">egats.EGATSObject</a> objects</div>
            <br/>
            <div>
                Attributes to provide for each object:
                <ul>
                    <li><i>classPath</i>: the Java resource path pointing to the class with which the object is instantiated (e.g., <i>java.lang.String</i>). To access this value from within Java, use the <i>[class object].getName()</i> method.</li>
                    <li><i>object</i>: the JSON representation of the object.</li>
                </ul>
            </div>
        </td>
    </tr>
    <tr>
        <td>Response</td>
        <td><a href="https://github.com/augie/egats/blob/master/src/egats/Response.java">egats.Response</a> object</td>
    </tr>
    <tr>
        <td>Response body</td>
        <td>
            <div>When response statusCode = 200 (OK), a list of IDs for the new objects</div>
            <div>When response statusCode = 300 (ERROR), an exception message</div>
        </td>
    </tr>
</table>
<br/>

<h3>Create Processes</h3>
<table>
    <tr>
        <td width="25%">Method</td>
        <td>POST</td>
    </tr>
    <tr> 
        <td>URL</td>
        <td><%= API.CREATE_PROCESS_URL %></td>
    </tr>
    <tr>
        <td>Body</td>
        <td>
            <div>A list of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSProcess.java">egats.EGATSProcess</a> objects</div>
            <br/>
            <div>
                Attributes to provide:
                <ul>
                    <li><i>methodPath</i>: the Java resource path pointing to the method to be executed (e.g., <i>egats.example.serverside.ExampleServerSide.fakeEGATProcess</i>).</li>
                    <li><i>args</i>: a list of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSObject.java">egats.EGATSObject</a> ids.</li>
                    <li><i>name</i> (optional): a name for the process.</li>
                </ul>
            </div>
        </td>
    </tr>
    <tr>
        <td>Response</td>
        <td><a href="https://github.com/augie/egats/blob/master/src/egats/Response.java">egats.Response</a> object</td>
    </tr>
    <tr>
        <td>Response body</td>
        <td>
            <div>When response statusCode = 200 (OK), a list of IDs for the new processes</div>
            <div>When response statusCode = 300 (ERROR), an exception message</div>
        </td>
    </tr>
</table>
<br/>

<h3>Create Workflows</h3>
<table>
    <tr>
        <td width="25%">Method</td>
        <td>POST</td>
    </tr>
    <tr> 
        <td>URL</td>
        <td><%= API.CREATE_WORKFLOW_URL %></td>
    </tr>
    <tr>
        <td>Body</td>
        <td>
            <div>A list of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSWorkflow.java">egats.EGATSWorkflow</a> objects</div>
            <br/>
            <div>
                Attributes to provide:
                <ul>
                    <li><i>classPath</i>: the Java resource path pointing to the method to be executed (e.g., <i>egats.example.serverside.ExampleServerSide.fakeEGATProcess</i>).</li>
                    <li><i>args</i>: a list of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSObject.java">egats.EGATSObject</a> ids.</li>
                    <li><i>name</i> (optional): a name for the workflow.</li>
                </ul>
            </div>
        </td>
    </tr>
    <tr>
        <td>Response</td>
        <td><a href="https://github.com/augie/egats/blob/master/src/egats/Response.java">egats.Response</a> object</td>
    </tr>
    <tr>
        <td>Response body</td>
        <td>
            <div>When response statusCode = 200 (OK), a list of IDs for the new workflows</div>
            <div>When response statusCode = 300 (ERROR), an exception message</div>
        </td>
    </tr>
</table>
<br/>

<br/><hr/><br/>

<h2>Testing</h2>

<script type="text/javascript">
    function makeRequest() {
        // Clear the previous response
        $("#constructed-request").html('');
        $("#response").html('');
        
        var method = $("#method option:selected").val();
        if (!(method == 'GET' || method == 'POST')) {
            return;
        }
        
        var folder = $("#subfolder option:selected").val();
        if (folder == '') {
            return;
        }
        
        var param = $("#param").val();
        if (method == 'GET' && param == '') {
            return;
        }
        
        var body = $("#body").val();
        if (method == 'POST' && body == '') {
            return;
        }
        
        $("#constructed-request").html(method + " <%= API.HOST %>" + folder + param);
        $.get('scripts/api.jsp?method=' + method + '&folder=' + folder + '&param=' + param + '&body=' + body, function(data) {
            $("#response").html(data);
        });
    }
</script>
<form action="" method="get" onsubmit="makeRequest(); return false;">
    <table>
        <tr>
            <td width="25%">Method</td>
            <td>
                <select id="method" name="method">
                    <option>GET</option>
                    <option>POST</option>
                </select>
            </td>
        </tr>
        <tr>
            <td>Request</td>
            <td>
                <select id="subfolder" name="subfolder">
                    <option value=""></option>
                    <option value="<%= API.OBJECT_FOLDER %>">Objects</option>
                    <option value="<%= API.PROCESS_FOLDER %>">Processes</option>
                    <option value="<%= API.WORKFLOW_FOLDER %>">Workflows</option>
                    <option value="<%= API.PROCESS_LIST_FOLDER %>">Processes By Timestamp</option>
                    <option value="<%= API.WORKFLOW_LIST_FOLDER %>">Workflows By Timestamp</option>
                </select>
            </td>
        </tr>
        <tr>
            <td>Parameter (if applicable)</td>
            <td>
                <input name="param" id="param" type="text" style="width: 100%;"/>
            </td>
        </tr>
        <tr>
            <td>Body (if applicable)</td>
            <td>
                <textarea name="body" id="body" type="text" style="width: 100%;"></textarea>
            </td>
        </tr>
        <tr>
            <td colspan="2"><button type="submit">Make Request</button></td>
        </tr>
        <tr>
            <td>Request</td>
            <td><div id="constructed-request" style="font-family: monospace;"></div></td>
        </tr>
        <tr>
            <td>Response</td>
            <td><div id="response" style="font-family: monospace;"></div></td>
        </tr>
    </table>
</form>

<%@include file="inc/footer.jsp" %>