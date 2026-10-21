<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/header.jspf" %>
<div class="card narrow center">
  <h2>Oops!</h2>
  <p>
    <c:choose>
      <c:when test="${pageContext.errorData.statusCode == 403}">You are not allowed to open this page.</c:when>
      <c:when test="${pageContext.errorData.statusCode == 404}">The page you are looking for does not exist.</c:when>
      <c:otherwise>Something went wrong on our side. Please try again.</c:otherwise>
    </c:choose>
  </p>
  <a class="btn" href="${pageContext.request.contextPath}/">Go to Home</a>
</div>
<%@ include file="/WEB-INF/views/footer.jspf" %>
