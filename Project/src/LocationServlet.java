import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LocationServlet extends HttpServlet{

	private final MultiThreadInvertedIndex index;

	public LocationServlet(MultiThreadInvertedIndex index) {

		this.index = index;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Map<String, Integer> counts = index.getCounts();

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		printForm(request, response);

		for(var count : counts.entrySet()) {

			//TODO Unsolved: link cannot be found

			//			URL cleanedUrl = LinkParser.clean(new URL(count.getKey()));

			String formatted = String.format("<p><a href=\"%s\">%s</a> : %s </p>",count.getKey(), count.getKey(), count.getValue());

			out.printf(formatted);


		}

		response.setStatus(HttpServletResponse.SC_OK);

		//		response.sendRedirect(request.getServletPath());

	}

	private static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.printf("<form method=\"get\" action=\"%s\">%n", request.getServletPath());
		out.printf("<table cellspacing=\"0\" cellpadding=\"2\"%n");
		out.printf("<tr>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"link\" maxlength=\"50\" size=\"20\">%n");
		out.printf("\t</td>%n");
		out.printf("</table>%n");
		out.printf("<p><input type=\"submit\" value=\"Check Link\"></p>\n%n");
		out.printf("</form>\n%n");
	}

}
