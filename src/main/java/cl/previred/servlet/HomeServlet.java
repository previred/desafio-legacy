package cl.previred.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class HomeServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5228184588684211956L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/index.html").forward(req, resp);
    }
}
