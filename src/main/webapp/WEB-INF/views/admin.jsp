<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/header.jspf" %>
<div class="card table-wrap">
  <h2>Customers (<c:out value="${customers.size()}"/>)</h2>
  <table>
    <tr><th>ID</th><th>Name</th><th>Email</th></tr>
    <c:forEach var="u" items="${customers}">
      <tr><td><c:out value="${u.userId}"/></td><td><c:out value="${u.name}"/></td><td><c:out value="${u.email}"/></td></tr>
    </c:forEach>
  </table>
</div>

<div class="card table-wrap">
  <h2>All Accounts</h2>
  <table>
    <tr><th>A/c No</th><th>Holder</th><th>Type</th><th>Balance (Rs.)</th><th>Status</th><th>Action</th></tr>
    <c:forEach var="a" items="${accounts}">
      <tr>
        <td><c:out value="${a.accountNo}"/></td>
        <td><c:out value="${a.holderName}"/></td>
        <td><c:out value="${a.accountType}"/></td>
        <td><fmt:formatNumber value="${a.balance}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
        <td><span class="badge ${a.status}"><c:out value="${a.status}"/></span></td>
        <td>
          <form method="post" action="${pageContext.request.contextPath}/admin" style="margin:0">
            <input type="hidden" name="accountNo" value="${a.accountNo}">
            <c:choose>
              <c:when test="${a.status == 'ACTIVE'}">
                <input type="hidden" name="action" value="freeze">
                <button class="btn small danger" type="submit">Freeze</button>
              </c:when>
              <c:otherwise>
                <input type="hidden" name="action" value="unfreeze">
                <button class="btn small success" type="submit">Unfreeze</button>
              </c:otherwise>
            </c:choose>
          </form>
        </td>
      </tr>
    </c:forEach>
  </table>
</div>
<%@ include file="/WEB-INF/views/footer.jspf" %>
