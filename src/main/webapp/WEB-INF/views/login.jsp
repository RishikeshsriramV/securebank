<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/header.jspf" %>
<div class="card narrow">
  <h2>Login</h2>
  <form method="post" action="${pageContext.request.contextPath}/login">
    <label for="email">Email</label>
    <input type="email" id="email" name="email" required autofocus>
    <label for="password">Password</label>
    <input type="password" id="password" name="password" required>
    <button class="btn" type="submit">Login</button>
  </form>
  <p class="muted">New customer? <a href="${pageContext.request.contextPath}/register">Create an account</a></p>
</div>
<%@ include file="/WEB-INF/views/footer.jspf" %>
