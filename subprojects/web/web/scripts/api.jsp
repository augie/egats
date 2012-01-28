<%@page import="java.lang.*, java.util.*, egats.*, egats.web.*" contentType="text/plain" %><%

try {
    String method = request.getParameter("method");
    String subfolder = request.getParameter("subfolder");
    String param = request.getParameter("param");
    String body = request.getParameter("body");

    if (method.equals("GET")) {
        out.print(Util.sendRequest(API.HOST + subfolder + param));
    } else if (method.equals("POST")) {
        out.print(Util.sendPostRequest(API.HOST + subfolder, body));
    }
} catch (Exception e) {
    out.print(e.getMessage());
    e.printStackTrace();
}

%>