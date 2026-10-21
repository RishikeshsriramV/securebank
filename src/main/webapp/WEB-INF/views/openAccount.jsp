<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/views/header.jspf" %>
<div class="card narrow">
  <h2>Open New Account</h2>
  <form method="post" action="${pageContext.request.contextPath}/open-account">
    <label for="type">Account type</label>
    <select id="type" name="type" required>
      <option value="SAVINGS">Savings (4% interest, min opening Rs.1000, min balance Rs.1000)</option>
      <option value="CURRENT">Current (no interest, min opening Rs.500, overdraft Rs.5000)</option>
    </select>
    <label for="initialDeposit">Initial deposit (Rs.)</label>
    <input type="number" id="initialDeposit" name="initialDeposit" min="1" step="0.01" required>
    <button class="btn" type="submit">Open Account</button>
    <a class="btn light" href="${pageContext.request.contextPath}/dashboard">Cancel</a>
  </form>
</div>
<%@ include file="/WEB-INF/views/footer.jspf" %>
