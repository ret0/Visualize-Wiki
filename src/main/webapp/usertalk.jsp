<%@page import="edu.mit.cci.visualize.wiki.vizservlet.WikipediaUsertalkVizServlet"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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
	<script type="text/javascript" src="js/processing.js"></script>
	<script type="text/javascript" src="js/init.js"></script>
</head>

<body>
	<script type="text/javascript">
		// start processing.js
		window.onload=function() {
			jQuery("table").treemap(300, 300, {dataCell:1, labelCell:0});
			var canvas = document.getElementsByTagName('canvas')[0];
			var codeElement = document.getElementById('processing-code');
			var code = codeElement.textContent || codeElement.innerText;
		};
	</script>

 <% WikipediaUsertalkVizServlet servlet = new WikipediaUsertalkVizServlet(request, application); %>
 <%= servlet.magicMethod() %>

</body>
</html>