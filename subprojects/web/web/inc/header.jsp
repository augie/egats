<%@page import="java.lang.*, java.util.*, egats.*, egats.web.*, org.apache.commons.fileupload.*, org.apache.commons.fileupload.servlet.*, org.apache.commons.fileupload.disk.*, org.apache.commons.fileupload.util.*" %><%
// Sets the address of the EGATS server
API.setHost(Util.HOST);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
    <head>
        <title>Empirical Game Analysis Toolkit</title>
        <link href="css/egats.css" rel="stylesheet" type="text/css" />
        <script src="js/jquery.js" type="text/javascript"></script>
    </head>
    <body>
        <h1>Empirical Game Analysis Toolkit</h1>
        <div id="menu">
            <div class="item"><a href="workflows.jsp">Workflows</a></div>
            <div class="item"><a href="create-workflow.jsp">Run Workflow</a></div>
            <div class="item"><a href="processes.jsp">Processes</a></div>
            <div class="item"><a href="create-process.jsp">Run Process</a></div>
            <div class="item"><a href="docs.jsp">Docs</a></div>
        </div>