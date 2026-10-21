<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/header.jspf" %>
<div class="card">
  <h2>My Accounts</h2>
  <a class="btn" href="${pageContext.request.contextPath}/open-account">+ Open New Account</a>
</div>

<c:if test="${empty accounts}">
  <div class="card center muted">You do not have any account yet. Open your first account to get started.</div>
</c:if>

<div class="grid">
  <c:forEach var="acc" items="${accounts}">
    <div class="card acct ${acc.accountType == 'CURRENT' ? 'current' : ''}">
      <h3><c:out value="${acc.accountType}"/> Account</h3>
      <div class="muted">A/c No: <c:out value="${acc.accountNo}"/>
        &nbsp;<span class="badge ${acc.status}"><c:out value="${acc.status}"/></span></div>
      <div class="balance">Rs. <fmt:formatNumber value="${acc.balance}" type="number" minFractionDigits="2" maxFractionDigits="2"/></div>
      <div class="muted">Yearly interest: Rs. <fmt:formatNumber value="${acc.annualInterest}" type="number" minFractionDigits="2" maxFractionDigits="2"/></div>
      <div class="actions">
        <a class="btn small" href="${pageContext.request.contextPath}/deposit">Deposit</a>
        <a class="btn small" href="${pageContext.request.contextPath}/withdraw">Withdraw</a>
        <a class="btn small" href="${pageContext.request.contextPath}/transfer">Transfer</a>
        <a class="btn small light" href="${pageContext.request.contextPath}/statement?accountNo=${acc.accountNo}">Statement</a>
      </div>
    </div>
  </c:forEach>
</div>
<%@ include file="/WEB-INF/views/footer.jspf" %>
