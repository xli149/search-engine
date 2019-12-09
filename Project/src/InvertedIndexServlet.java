import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chrislee
 *
 */
public class InvertedIndexServlet extends HttpServlet {

	/**
	 * Default serial ID not used
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Title of a link
	 */
	private static final String TITLE = "Index";

	/**
	 * Instance of {@link MultiThreadInvertedIndex}
	 */
	MultiThreadInvertedIndex index;

	/**
	 * Constructor
	 * @param index
	 */
	public InvertedIndexServlet(MultiThreadInvertedIndex index) {

		this.index = index;

	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Set<String> words = index.getWords();

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		preFormat(request, response);

		for(var word : words) {

			out.printf("<details>");

			out.printf("<p><summary>");

			out.printf("<font size=\"6\" face=\"arial\" color=\"black\"> %s: </font>", word);

			out.printf("</summary></p>");

			out.printf("<br>");

			Set<String> urls = index.getLocations(word);

			for(var url : urls) {

				if(index.getPositions(word, url).size() != 0) {

					String formatted = String.format("<p style=\"margin-left:20px\"><i>Locations: </i><a href=\"%s\">%s</a> </p>",url, url);

					out.printf("				<div class=\"box\">%n");

					out.printf(formatted);

					out.printf("				</div>%n");

					out.printf("%n");

					Set<Integer> positions = index.getPositions(word, url);

					out.printf("<details>");

					out.printf("<summary style=\"margin-left:20px\">");

					out.printf("<i>Positions:</i>");

					out.printf("</summary>");

					out.printf("				<div style=\"margin-left:80px\" class=\"box\">%n");

					out.print("<p>");

					var iterator = positions.iterator();

					if(iterator.hasNext()) {

						var position = iterator.next();

						out.print(position.toString());

					}

					while(iterator.hasNext()) {

						var position = iterator.next();

						out.print(", ");

						out.print(position.toString());

					}

					out.printf("				</div>%n");

					out.print("</p>");

					out.printf("</details>");

					out.printf("<br>");

				}
			}

			out.printf("</details>");

			out.print("<br>");

		}

		postFormat(request, response);

		response.setStatus(HttpServletResponse.SC_OK);

	}

	/**
	 * Function for prepare the html script
	 * @param request the httpServlet request
	 * @param response the httpServleet response
	 * @throws IOException if the socket is not writable
	 */
	public void preFormat(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		out.printf("<!DOCTYPE html>%n");

		out.printf("<html>%n");

		out.printf("<head>%n");

		out.printf("	<meta charset=\"utf-8\">%n");

		out.printf("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");

		out.printf("	<title>%s</title>%n", TITLE);

		out.printf("	<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bulma@0.8.0/css/bulma.min.css\">%n");

		out.printf("	<script defer src=\"https://use.fontawesome.com/releases/v5.3.1/js/all.js\"></script>%n");

		out.printf("</head>%n");

		out.printf("%n");

		out.printf("<body>%n");

		out.printf("	<section class=\"hero is-primary is-bold\">%n");

		out.printf("	  <div class=\"hero-body\">%n");

		out.printf("	    <div class=\"container\">%n");

		out.printf("	      <h1 class=\"title\">%n");

		out.print("<img src=\"images/brand.png\" width=\"60\" height=\"50\"></img>");

		out.print("<p> <font size=\"3\" face=\"arial\" color=\"white\"><i>Everything is possible</i></font><p>");

		out.printf("	        InvertedIndex%n");

		out.printf("	      </h1>%n");

		out.printf("	      <h2 class=\"subtitle\">%n");

		out.printf("					<i class=\"fas fa-calendar-alt\"></i>%n");

		out.printf("					&nbsp;Updated %s%n", getDate());

		out.printf("	      </h2>%n");

		out.printf("	    </div>%n");

		out.printf("	  </div>%n");

		out.printf("	</section>%n");

		out.printf("%n");

		out.printf("	<section class=\"section\">%n");

		out.printf("		<div class=\"container\">%n");

		out.printf("			<h2 class=\"title\">InvertedIndex</h2>%n");

		out.printf("%n");

	}

	/**
	 * Function for finishing the html script
	 * @param request the httpServlet request
	 * @param response the httpServleet response
	 * @throws IOException if the socket is not writable
	 */
	public void postFormat(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		out.printf("			</div>%n");

		out.printf("%n");

		out.printf("		</div>%n");

		out.printf("	</section>%n");

		out.printf("%n");

		out.printf("	<footer class=\"footer\">%n");

		out.printf("	  <div class=\"content has-text-centered\">%n");

		out.printf("	    <p>%n");

		out.printf("	      This request was handled by thread %s.%n", Thread.currentThread().getName());

		out.printf("	    </p>%n");

		out.printf("	  </div>%n");

		out.printf("	</footer>%n");

		out.printf("</body>%n");

		out.printf("</html>%n");
	}

	/**
	 * Function for getting the current date
	 * @return a formatted date
	 */
	private static String getDate() {

		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";

		DateFormat formatter = new SimpleDateFormat(format);

		return formatter.format(new Date());

	}

}