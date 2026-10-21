<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/header.jspf" %>
<div class="card">
  <h2>Mini Statement (last 10 transactions)</h2>
  <form method="get" action="${pageContext.request.contextPath}/statement">
    <label for="accountNo">Select account</label>
    <select id="accountNo" name="accountNo" required>
      <c:forEach var="a" items="${accounts}">
        <option value="${a.accountNo}" ${a.accountNo == selected ? 'selected' : ''}>
          <c:out value="${a.accountType}"/> - <c:out value="${a.accountNo}"/>
        </option>
      </c:forEach>
    </select>
    <button class="btn" type="submit">View</button>
  </form>
</div>

<c:if test="${not empty selected}">
  <div class="card table-wrap">
    <h2>Account <c:out value="${selected}"/></h2>
    <c:choose>
      <c:when test="${empty transactions}"><p class="muted">No transactions yet.</p></c:when>
      <c:otherwise>
        <table>
          <tr><th>Date &amp; Time</th><th>Type</th><th>Amount (Rs.)</th><th>Balance (Rs.)</th><th>Remarks</th></tr>
          <c:forEach var="t" items="${transactions}">
            <tr>
              <td><fmt:formatDate value="${t.txnTime}" pattern="dd MMM yyyy, hh:mm a"/></td>
              <td><c:out value="${t.type}"/></td>
              <td class="${t.type == 'DEPOSIT' || t.type == 'TRANSFER_IN' ? 'credit' : 'debit'}">
                ${t.type == 'DEPOSIT' || t.type == 'TRANSFER_IN' ? '+' : '-'}<fmt:formatNumber value="${t.amount}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
              </td>
              <td><fmt:formatNumber value="${t.balanceAfter}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
              <td><c:out value="${t.remarks}"/></td>
            </tr>
          </c:forEach>
        </table>
      </c:otherwise>
    </c:choose>
  </div>
</c:if>
<%@ include file="/WEB-INF/views/footer.jspf" %>
