import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LuckyServlet extends HttpServlet{

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		ServletContext context = getServletContext();

		String luckyUrl = (String) context.getAttribute("luckValue");



		//		String firstUrl = (String)request.getAttribute("luckLink");

		//		out.print("asdfasdfasdfas");

		//		System.out.println(i);


		System.out.println(luckyUrl);

		response.sendRedirect(luckyUrl);

		response.setStatus(HttpServletResponse.SC_OK);
	}

}
