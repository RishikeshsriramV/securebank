<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/header.jspf" %>
<div class="card narrow">
  <h2>Customer Registration</h2>
  <form method="post" action="${pageContext.request.contextPath}/register">
    <label for="name">Full name</label>
    <input type="text" id="name" name="name" value="<c:out value='${name}'/>" required maxlength="100">
    <label for="email">Email</label>
    <input type="email" id="email" name="email" value="<c:out value='${email}'/>" required maxlength="120">
    <label for="password">Password (min 6 characters)</label>
    <input type="password" id="password" name="password" required minlength="6">
    <label for="confirm">Confirm password</label>
    <input type="password" id="confirm" name="confirm" required minlength="6">
    <button class="btn" type="submit">Register</button>
  </form>
  <p class="muted">Already registered? <a href="${pageContext.request.contextPath}/login">Login</a></p>
</div>
<%@ include file="/WEB-INF/views/footer.jspf" %>
