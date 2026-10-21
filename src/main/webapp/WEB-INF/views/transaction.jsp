<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/header.jspf" %>
<div class="card narrow">
  <h2>
    <c:choose>
      <c:when test="${mode == 'DEPOSIT'}">Deposit Money</c:when>
      <c:when test="${mode == 'WITHDRAW'}">Withdraw Money</c:when>
      <c:otherwise>Fund Transfer</c:otherwise>
    </c:choose>
  </h2>

  <c:if test="${empty accounts}">
    <p class="muted">You need an account first. <a href="${pageContext.request.contextPath}/open-account">Open one</a>.</p>
  </c:if>

  <c:if test="${not empty accounts}">
    <form method="post" action="${pageContext.request.contextPath}/${action}">
      <label for="accountNo"><c:out value="${mode == 'TRANSFER' ? 'From account' : 'Account'}"/></label>
      <select id="accountNo" name="accountNo" required>
        <c:forEach var="a" items="${accounts}">
          <option value="${a.accountNo}" ${param.accountNo == a.accountNo ? 'selected' : ''}>
            <c:out value="${a.accountType}"/> - <c:out value="${a.accountNo}"/>
            (Rs. <fmt:formatNumber value="${a.balance}" type="number" minFractionDigits="2" maxFractionDigits="2"/>)
          </option>
        </c:forEach>
      </select>

      <c:if test="${mode == 'TRANSFER'}">
        <label for="toAccount">To account number</label>
        <input type="number" id="toAccount" name="toAccount" value="<c:out value='${param.toAccount}'/>" required>
      </c:if>

      <label for="amount">Amount (Rs.)</label>
      <input type="number" id="amount" name="amount" min="0.01" step="0.01" value="<c:out value='${param.amount}'/>" required>

      <label for="remarks">Remarks (optional)</label>
      <input type="text" id="remarks" name="remarks" maxlength="100" value="<c:out value='${param.remarks}'/>">

      <button class="btn" type="submit">
        <c:choose>
          <c:when test="${mode == 'DEPOSIT'}">Deposit</c:when>
          <c:when test="${mode == 'WITHDRAW'}">Withdraw</c:when>
          <c:otherwise>Transfer</c:otherwise>
        </c:choose>
      </button>
      <a class="btn light" href="${pageContext.request.contextPath}/dashboard">Cancel</a>
    </form>
  </c:if>
</div>
<%@ include file="/WEB-INF/views/footer.jspf" %>
