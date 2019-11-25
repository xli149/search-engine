import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;



/**
 * Servlet to GET handle requests to /check.
 */
public class Servlet extends HttpServlet {

	/** ID used for serialization, which we are not using. */
	private static final long serialVersionUID = 1L;

	private ConcurrentLinkedQueue<String> messages;

	/**
	 * Displays a form where users can enter a URL to check. When the button is
	 * pressed, submits the URL back to /check as a GET request.
	 *
	 * If a URL was included as a parameter in the GET request, fetch and display
	 * the HTTP headers of that URL.
	 */

	private static final String TITLE = "Link Checker";

	/**
	 * queryBuilder to be used
	 */
	private final QueryBuilderInterface queryBuilder;

	private final MultiThreadWebCrawler webCrawler;


	/**
	 * Servlet constructor
	 * @param queryBuilder queryBuilder queryBuilder object to be used to create query
	 */
	public Servlet(QueryBuilderInterface queryBuilder, MultiThreadWebCrawler webCrawler) {

		super();

		this.queryBuilder = queryBuilder;

		this.webCrawler = webCrawler;

		messages = new ConcurrentLinkedQueue<>();

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");

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
		out.printf("	        Message Board%n");
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
		out.printf("			<h2 class=\"title\">Messages</h2>%n");
		out.printf("%n");

		if (messages.isEmpty()) {
			out.printf("				<p>No messages.</p>%n");
		}
		else {
			for (String message : messages) {
				out.printf("				<div class=\"box\">%n");
				out.printf(message);
				out.printf("				</div>%n");
				out.printf("%n");
				messages.poll();
			}
		}

		out.printf("			</div>%n");
		out.printf("%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("			<h2 class=\"title\">Add Message</h2>%n");
		out.printf("%n");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		out.printf("				<div class=\"field\">%n");
		out.printf("					<label class=\"label\">Name</label>%n");
		out.printf("					<div class=\"control has-icons-left\">%n");
		out.printf("						<input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Enter your name here.\">%n", "username");
		out.printf("						<span class=\"icon is-small is-left\">%n");
		out.printf("							<i class=\"fas fa-user\"></i>%n");
		out.printf("						</span>%n");
		out.printf("					</div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("				<div class=\"field\">%n");
		out.printf("				  <label class=\"label\">Message</label>%n");
		out.printf("				  <div class=\"control\">%n");
		out.printf("				    <textarea class=\"textarea\" name=\"%s\" placeholder=\"Enter your message here.\"></textarea>%n", "message");
		out.printf("				  </div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-comment\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("						Post Message%n");
		out.printf("					</button>%n");
		out.printf("			  </div>%n");
		out.printf("			</form>%n");

		out.printf("			<form method=\"%s\" action=\"%s\">%n", "GET", "/counts");
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-comment\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("						Locations Browser%n");
		out.printf("					</button>%n");
		out.printf("			  </div>%n");
		out.printf("			</form>%n");

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

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");

		String message = request.getParameter("message");

		message = message == null ? "" : message;

		message = StringEscapeUtils.escapeHtml4(message);

		queryBuilder.parseLinks(message, false);

		System.out.println(message);

		List<InvertedIndex.SearchResult> links = queryBuilder.results(message);

		System.out.println(links);

		if(links != null) {

			for(int i = 0; i < links.size(); i++) {

				String link = links.get(i).getLocation();

				String formatted = String.format("<p><a href=\"%s\">%s</a> </p>", link, link);

				// TODO synchronized on it
				messages.add(formatted);

			}
		}

		response.setStatus(HttpServletResponse.SC_OK);

		System.out.println(request.getServletPath());

		response.sendRedirect(request.getServletPath());
	}

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}


