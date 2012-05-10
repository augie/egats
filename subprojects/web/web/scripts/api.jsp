<%@page import="java.lang.*, java.util.*, egats.*, egats.web.*" contentType="text/plain" %><%

try {
    String method = request.getParameter("method");
    String folder = request.getParameter("folder");
    String param = request.getParameter("param");
    String body = request.getParameter("body");

    if (method.equals("GET")) {
        out.print(Util.send(API.HOST + folder + param));
    } else if (method.equals("POST")) {
        out.print(Util.send(API.HOST + folder, body));
    }
} catch (Exception e) {
    out.print(e.getMessage());
    e.printStackTrace();
}

%>