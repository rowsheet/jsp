<html>
<script src="/test.js"></script>
<link rel="stylesheet" type="text/css" href="/style.css">
<body>
    <h1>
        Hello World!
        <br/>
        <%
            out.println("Your IP address is " + request.getRemoteAddr());
        %>
        <br/>
        <%
            for (int i = 0; i < 10; i++) {
                out.println("<p style='font-size: 10px'>testing<p>");
            }
        %>
        <jsp:include page="foo/othertest.jsp"/>
    </h1>
</body>
</html>