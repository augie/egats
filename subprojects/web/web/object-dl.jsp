<%@page contentType="text/plain" import="egats.*, egats.web.*" pageEncoding="utf8"%><%
String id = request.getParameter("id");
EGATSObject egatsObject = API.getObject(id);
out.print(egatsObject.getObject());
%>