<%@ page import="java.util.Random" %><%
    Random random = new Random(System.currentTimeMillis());
    String number = String.valueOf(random.nextInt());
%>

<html>
<head>Example</head>
<body>
<div>
    Random number: <%= number %>.
</div>
</body>
</html>