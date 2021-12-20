<%--
  Created by IntelliJ IDEA.
  User: vinciraia99
  Date: 20/11/2021
  Time: 17:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="c"
          uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <script>
        <c:if test="${rules !=null && connector != null && stencil != null}">
        var connector =  decodeURIComponent("${connector}");
        var stencil = decodeURIComponent("${stencil}");
        var rules = decodeURIComponent("${rules}");
        localStorage.setItem("RULES",rules.replaceAll("+"," "));
        localStorage.setItem("CONNECTOR",connector.replaceAll("+"," "));
        localStorage.setItem("STENCIL",stencil.replaceAll("+"," "));
        window.location.href = (window.location.href).replaceAll("/uploadexternal","");
        </c:if>

        <c:if test="${rules !=null && connector != null && stencil != null && semantic != null}">
        var connector =  decodeURIComponent("${connector}");
        var stencil = decodeURIComponent("${stencil}");
        var rules = decodeURIComponent("${rules}");
        var semantic = decodeURIComponent("${semantic}");
        localStorage.setItem("RULES",rules.replaceAll("+"," "));
        localStorage.setItem("CONNECTOR",connector.replaceAll("+"," "));
        localStorage.setItem("STENCIL",stencil.replaceAll("+"," "));
        localStorage.setItem("SEMANTIC_RULES",stencil.replaceAll("+"," "));
        window.location.href = (window.location.href).replaceAll("/uploadexternal","");
        </c:if>

    </script>
    <title>Title</title>
</head>
<body>

</body>
</html>
