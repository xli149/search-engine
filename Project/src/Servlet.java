import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;



/**
 * Servlet to GET handle requests to /check.
 */
public class Servlet extends CookieBaseServlet {

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

	private final LinkedList<String> suggestQueries;

	//	private final MultiThreadInvertedIndex index;

	private final SimpleReadWriteLock lock;

	public static final String VISIT_HISTORY = "history";

	public static final String VISIT_LASTTIME = "last";



	/**
	 * Servlet constructor
	 * @param queryBuilder queryBuilder queryBuilder object to be used to create query
	 * @param webCrawler webCrawler object
	 * @param history an arrayList
	 */
	public Servlet(QueryBuilderInterface queryBuilder, MultiThreadWebCrawler webCrawler, LinkedList<String> suggestQueries) {

		super();

		this.queryBuilder = queryBuilder;

		this.webCrawler = webCrawler;

		messages = new ConcurrentLinkedQueue<>();

		this.suggestQueries = suggestQueries;

		//		this.index = index;

		lock = new SimpleReadWriteLock();

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println(request.getRequestURI());



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
		out.printf("	        Search Engine%n");
		out.printf("	      </h1>%n");
		out.printf("	      <h2 class=\"subtitle\">%n");
		out.printf("					<i class=\"fas fa-calendar-alt\"></i>%n");
		out.printf("					&nbsp;Updated %s%n", getDate());
		out.printf("	      </h2>%n");
		out.printf("	    </div>%n");
		out.printf("	  </div>%n");
		out.printf("	</section>%n");
		out.printf("%n");

		getLastTime(request, response);

		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("			<h2 class=\"title\">Links</h2>%n");
		out.printf("%n");




		if (messages.isEmpty()) {
			out.printf("				<p>No Links.</p>%n");
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
		out.printf("					<label class=\"label\">New Crawl</label>%n");
		out.printf("					<div class=\"control has-icons-left\">%n");
		out.printf("						<input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Enter new seed here.\">%n", "seed");
		out.printf("						<span class=\"icon is-small is-left\">%n");
		out.printf("							<i class=\"fas fa-user\"></i>%n");
		out.printf("						</span>%n");
		out.printf("					</div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("				<div class=\"field\">%n");

		out.printf("                  <p>favorite queries</p> ");

		out.printf("<tr>");


		for(int i = 0; i < 5 && i< suggestQueries.size(); i++) {

			out.printf("<td>%s </td>", suggestQueries.get(i) );
		}
		out.printf("</tr>");


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

		out.printf("			<form method=\"%s\" action=\"%s\">%n", "GET", "/history");
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-comment\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("						History%n");
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

		//		System.out.println(request.getRequestURI());

		response.setContentType("text/html");

		String message = request.getParameter("message");

		String seed = request.getParameter("seed");

		seed = seed == null ? "" : seed;

		message = message == null ? "" : message;

		seed = StringEscapeUtils.escapeHtml4(seed);

		message = StringEscapeUtils.escapeHtml4(message);

		if(seed.length() != 0) {

			System.out.println("seed: " + "\"" + seed + "\"");

			URL url = new URL(seed);

			webCrawler.webCrawling(url);

			messages.add("<p> New links resources has been added</p>");

		}



		addHistory(request, response, message);

		suggestQueries.addFirst(message);



		queryBuilder.parseLinks(message, false);

		//		System.out.println(message);

		List<InvertedIndex.SearchResult> links = queryBuilder.results(message);

		//		System.out.println(links);

		if(links != null) {

			for(int i = 0; i < links.size(); i++) {

				String link = links.get(i).getLocation();

				String formatted = String.format("<p><a href=\"%s\">%s</a> </p>", link, link);

				// TODO synchronized on it
				messages.add(formatted);



			}
		}

		response.setStatus(HttpServletResponse.SC_OK);

		//		System.out.println(request.getServletPath());

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

	private void getLastTime(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		Map<String, Cookie> cookies = getCookieMap(request);

		Cookie visitLast = cookies.get(VISIT_LASTTIME);

		if(visitLast == null) {

			visitLast = new Cookie(VISIT_LASTTIME, "");

			String encoded = URLEncoder.encode(getShortDate(), StandardCharsets.UTF_8);

			visitLast.setValue(encoded);

			response.addCookie(visitLast);

			//			out.printf("	<section class=\"hero is-primary is-bold\">%n");

			out.print("<p>Welcome, this is your first time visiting this website</p>");

			//			out.print("</section>");

		}
		else {
			String decoded = URLDecoder.decode(visitLast.getValue(), StandardCharsets.UTF_8);

			String escaped = StringEscapeUtils.escapeHtml4(decoded);

			out.printf("</p>Last time log in was at %s</p>", escaped);

		}

		String encoded = URLEncoder.encode(getShortDate(), StandardCharsets.UTF_8);

		visitLast.setValue(encoded);

		response.addCookie(visitLast);
	}

	public void addHistory(HttpServletRequest request, HttpServletResponse response, String message) {

		Map<String, Cookie> cookies = getCookieMap(request);

		Cookie visitHistory = cookies.get(VISIT_HISTORY);


		if (visitHistory == null ) {

			visitHistory = new Cookie(VISIT_HISTORY, "");


		}

		if (request.getIntHeader("DNT") != 1) {

			System.out.println("got here");

			String decoded = URLDecoder.decode(visitHistory.getValue(), StandardCharsets.UTF_8);

			String update = decoded + "-" + message;

			String encoded = URLEncoder.encode(update, StandardCharsets.UTF_8);

			visitHistory.setValue(encoded);

			response.addCookie(visitHistory);
		}
		//		else {
		//			clearCookies(request, response);
		//			out.printf("<p>Your visits will not be tracked.</p>");
		//		}

	}
}


