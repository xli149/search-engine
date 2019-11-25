import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Servlet to GET handle requests to /check.
 */
public class Servlet extends HttpServlet {

	/** ID used for serialization, which we are not using. */
	private static final long serialVersionUID = 1L;

	/**
	 * Displays a form where users can enter a URL to check. When the button is
	 * pressed, submits the URL back to /check as a GET request.
	 *
	 * If a URL was included as a parameter in the GET request, fetch and display
	 * the HTTP headers of that URL.
	 */

	/**
	 * queryBuilder to be used
	 */
	private final QueryBuilderInterface queryBuilder;


	/**
	 * Servlet constructor
	 * @param queryBuilder queryBuilder queryBuilder object to be used to create query
	 */
	public Servlet(QueryBuilderInterface queryBuilder) {

		super();

		this.queryBuilder = queryBuilder;

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		printForm(request, response);

		String input = request.getParameter("link");

		if(input != null) {

			queryBuilder.parseLine(input, false);

			List<InvertedIndex.SearchResult> links = queryBuilder.results(input);

			if(links != null) {

				for(int i = 0; i < links.size(); i++) {

					out.print("<p>" + links.get(i).getLocation() + "</p>");

				}

			}

		}

		response.setStatus(HttpServletResponse.SC_OK);

	}

	/**
	 * Utility function for html formating
	 * @param request the http request
	 * @param response the http response
	 * @throws IOException if the output stream cannot be written in
	 */
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