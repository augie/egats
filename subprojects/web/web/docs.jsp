<%@include file="inc/header.jsp" %>
<h2>EGATS ReST API</h2>

<div>All communication with the EGATS server is in JSON format. All objects are passed in their JSON representation.</div>

<br/>

<h3>Get Object</h3>
<table>
    <tr>
        <td width="25%">Method</td>
        <td>GET</td>
    </tr>
    <tr> 
        <td>URL</td>
        <td><%= API.getObjectURL("<i>$id</i>") %></td>
    </tr>
    <tr>
        <td>Parameters</td>
        <td><i>$id</i>: the ID of the object being requested</td>
    </tr>
    <tr>
        <td>Response</td>
        <td><a href="https://github.com/augie/egats/blob/master/src/egats/Response.java">egats.Response</a> object</td>
    </tr>
    <tr>
        <td>Response body</td>
        <td>
            <div>When response statusCode = 200, <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSObject.java">egats.EGATSObject</a> object</div>
            <div>When response statusCode = 300, an exception message</div>
            <div>When response statusCode = 404, <i>$id</i></div>
        </td>
    </tr>
</table>
<br/>

<h3>Get Process</h3>
<table>
    <tr>
        <td width="25%">Method</td>
        <td>GET</td>
    </tr>
    <tr> 
        <td>URL</td>
        <td><%= API.getProcessURL("<i>$id</i>") %></td>
    </tr>
    <tr>
        <td>Parameters</td>
        <td><i>$id</i>: the ID of the process being requested</td>
    </tr>
    <tr>
        <td>Response</td>
        <td><a href="https://github.com/augie/egats/blob/master/src/egats/Response.java">egats.Response</a> object</td>
    </tr>
    <tr>
        <td>Response body</td>
        <td>
            <div>When response statusCode = 200, <a href="https://github.com/augie/egats/blob/master/src/egats/EGATProcess.java">egats.EGATProcess</a> object</div>
            <div>When response statusCode = 300, an exception message</div>
            <div>When response statusCode = 404, <i>$id</i></div>
        </td>
    </tr>
</table>
<br/>

<h3>Get Process List</h3>
<table>
    <tr>
        <td width="25%">Method</td>
        <td>GET</td>
    </tr>
    <tr> 
        <td>URL</td>
        <td><%= API.HOST %><%= API.PROCESS_LIST_SUBFOLDER %><i>$timestamp</i></td>
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
            <div>When response statusCode = 200, array of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATProcess.java">egats.EGATProcess</a> objects</div>
            <div>When response statusCode = 300, <i>$timestamp</i></div>
        </td>
    </tr>
</table>
<br/>

<h3>Create Object</h3>
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
            <div><a href="https://github.com/augie/egats/blob/master/src/egats/EGATSObject.java">egats.EGATSObject</a> object</div>
            <br/>
            <div>
                Attributes to provide:
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
            <div>When response statusCode = 200, the <i>id</i> of the new object</div>
            <div>When response statusCode = 300, an exception message</div>
        </td>
    </tr>
</table>
<br/>

<h3>Create Process</h3>
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
            <div><a href="https://github.com/augie/egats/blob/master/src/egats/EGATProcess.java">egats.EGATProcess</a> object</div>
            <br/>
            <div>
                Attributes to provide:
                <ul>
                    <li><i>methodPath</i>: the Java resource path pointing to the method to be executed (e.g., <i>egats.example.serverside.ExampleServerSide.fakeEGATProcess</i>).</li>
                    <li><i>args</i>: a list of <a href="https://github.com/augie/egats/blob/master/src/egats/EGATSObject.java">egats.EGATSObject</a> ids.</li>
                    <li><i>name</i> (optional): some canonical name for the process.</li>
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
            <div>When response statusCode = 200, the <i>id</i> of the new process</div>
            <div>When response statusCode = 300, an exception message</div>
        </td>
    </tr>
</table>
<br/>

<br/><hr/><br/>

<h2>Testing</h2>

<script type="text/javascript">
    function makeRequest() {
        var method = $("#method option:selected").val();
        if (!(method == 'GET' || method == 'POST')) {
            return;
        }
        
        var request = $("#request option:selected").val();
        if (request == '') {
            return;
        }
        
        var param = $("#param").val();
        if (param == '') {
            return;
        }
        
        $("#constructed-request").html(method + " " + request + param);
        if (method == 'GET') {
            $.get(request + param, function(data) {
                $("#response").html(data);
            });
        }
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
                <select id="request" name="request">
                    <option value=""></option>
                    <option value="<%=API.getObjectURL("")%>">Object</option>
                    <option value="<%=API.getProcessURL("")%>">Process</option>
                    <option value="<%= API.HOST %><%= API.PROCESS_LIST_SUBFOLDER %>">Process List</option>
                </select>
            </td>
        </tr>
        <tr>
            <td>Parameter (if applicable)</td>
            <td>
                <input name="param" id="param" type="text"/>
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