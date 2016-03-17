<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="de.mariokramer.wsrlock.model.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<%@include file="2_Head.jsp"%>

<title>Documents Overview</title>

</head>

<body onload="">
	<%@include file="4_Navbar.jsp"%>
	<div class="container">
		<section class="row">
			<div class="col-xs-18 col-sm-12 col-lg-12">
				<div class="table-responsive">
					<table class="table table-striped" id="docTable">
						<thead>
							<tr>
								<th>#</th>
								<th>Document name</th>
								<th>Document preview</th>
								<th></th>
							</tr>
						</thead>
						<tbody>							
							<c:forEach var="doc" items="${documents}" varStatus="counter">
								<tr id="row-${doc.docId}">
									<td>${doc.docId}</td>
									<td>${doc.docName}</td>
									<td>${doc.docValue}</td>
									<td>
										<c:url value="/readDoc?docId=${doc.docId}" var="showUrl" />
										<a onclick="showDoc()" href="${showUrl}" type="button" class="btn btn-primary" id="showDoc">Show</a>
										<a onclick="delDoc(${doc.docId})" type="button" class="btn btn-danger" id="delDocId${doc.docId}">x</a>
									</td>
								</tr>
							</c:forEach>
								<tr id="rowadd">
									<td></td>
									<td>
										<input type="text" name="docName" id="docName" class="form-control" placeholder="New document name">
									</td>
									<td>
										<input type="text" name="docValue" id="docValue" class="form-control" placeholder="Put a value here..">
									</td>
									<td>
										<button onclick="addDoc()" class="btn btn-primary" type="button" id="addDoc1" name="addDoc">+</button>
									</td>
								</tr>							
						</tbody>
					</table>			
				</div>
			</div>
		</section>
		<!-- Ende row2 -->
	</div>
	<!-- Ende Container -->
	<sec:authentication property="credentials" var="cred"/>
	
	<input type="hidden" id="pageName" value="start">
	<input type="hidden" id="cred" value="${cred}">
	<input type="hidden" id="sessionId" value="${pageContext.session.id}">
</body>
<%@include file="3_HeadCon.jsp" %>
</html>