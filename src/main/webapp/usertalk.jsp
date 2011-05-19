<%@page import="edu.mit.cci.visualize.wiki.vizservlet.WikipediaUsertalkVizServlet" %>
<%@page import="edu.mit.cci.visualize.wiki.util.Const" %>
<%@page import="java.util.List" %>
<%@page import="edu.mit.cci.visualize.wiki.collector.ArticleContributions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"> 

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Get TOP-10 Contributors</title>
	<meta http-equiv="Content-Style-Type" content="text/css" />
	<meta http-equiv="Content-Script-Type" content="text/javascript" />
	<link href="css/flexgrid.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="js/jquery-1.2.1.js"></script>
	<script type="text/javascript" src="js/treemap.js"></script>
	<script type="text/javascript" src="js/processing-1.1.0.min.js"></script>
	<script type="text/javascript" src="js/init.js"></script>
</head>

<body>
	<script type="text/javascript">
		// start processing.js
		window.onload=function() {
			jQuery("table#contrib-map").treemap(300, 300, {dataCell:1, labelCell:0});
			var canvas = document.getElementsByTagName('canvas')[0];
			var codeElement = document.getElementById('processing-code');
			var code = codeElement.textContent || codeElement.innerText;
		};
	</script>

<% 
	WikipediaUsertalkVizServlet servletz = new WikipediaUsertalkVizServlet(request);
	List<ArticleContributions> contributions = servletz.getContributions();
	List<String> userIds = servletz.prepareUserIDs(contributions);
%>


<script id="processing-code" type="application/processing">
    <%@ include file="skeleton/spring.jsp" %>
</script>

<div><canvas width="<%= Const.CANVAS_SIZE %>" height="<%= Const.CANVAS_SIZE %>" /></div>

<table>
    <tbody>
            <tr>
	            <c:forEach items="<%= ArticleContributions.getExperiencecolors() %>" var="color">
	                <td style="background-color: <c:out value="${color}" />">cc</td>
	            </c:forEach>
            </tr>
    </tbody>
</table>
 
<table id="contrib-map">
	<tbody>
		<c:forEach items="<%= contributions %>" var="cont">
			<tr>
				<td><c:out value="${cont.userID}" /></td>
				<td><c:out value="${cont.numberOfChanges}" /></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<p id ="map" />

</body>
</html>