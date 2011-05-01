package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import edu.mit.cci.visualize.wiki.vizservlet.WikipediaUsertalkVizServlet;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public final class usertalk_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.Vector _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\"> \n");
      out.write("\n");
      out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
      out.write("<head>\n");
      out.write("\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n");
      out.write("\t<title>Get TOP-10 Contributors</title>\n");
      out.write("\t<meta http-equiv=\"Content-Style-Type\" content=\"text/css\" />\n");
      out.write("\t<meta http-equiv=\"Content-Script-Type\" content=\"text/javascript\" />\n");
      out.write("\t<link href=\"css/flexgrid.css\" rel=\"stylesheet\" type=\"text/css\" />\n");
      out.write("\t<script type=\"text/javascript\" src=\"js/jquery-1.2.1.js\"></script>\n");
      out.write("\t<script type=\"text/javascript\" src=\"js/treemap.js\"></script>\n");
      out.write("\t<script type=\"text/javascript\" src=\"js/processing.js\"></script>\n");
      out.write("\t<script type=\"text/javascript\" src=\"js/init.js\"></script>\n");
      out.write("</head>\n");
      out.write("\n");
      out.write("<body>\n");
      out.write("\t<script type=\"text/javascript\">\n");
      out.write("\t\t// start processing.js\n");
      out.write("\t\twindow.onload=function() {\n");
      out.write("\t\t\tjQuery(\"table\").treemap(300, 300, {dataCell:1, labelCell:0});\n");
      out.write("\t\t\tvar canvas = document.getElementsByTagName('canvas')[0];\n");
      out.write("\t\t\tvar codeElement = document.getElementById('processing-code');\n");
      out.write("\t\t\tvar code = codeElement.textContent || codeElement.innerText;\n");
      out.write("\t\t};\n");
      out.write("\t</script>\n");
      out.write("\n");
      out.write(" ");
 WikipediaUsertalkVizServlet servlet = new WikipediaUsertalkVizServlet(request, application); 
      out.write('\n');
      out.write(' ');
      out.print( servlet.magicMethod() );
      out.write("\n");
      out.write("\n");
      out.write("</body>\n");
      out.write("</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
