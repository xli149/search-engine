import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
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

	/**
	 * Title of a link
	 */
	private static final String TITLE = "Link Checker";

	/**
	 * Instance of queryBuilder to be used
	 */
	private final QueryBuilderInterface queryBuilder;

	/**
	 * Instance of webCrawler to be used
	 */
	private final MultiThreadWebCrawler webCrawler;

	/**
	 * A linkedList of suggested Queries
	 */
	private final LinkedList<String> suggestQueries;

	/**
	 * Constant String variable for cookie to use
	 */
	public static final String SEARCH_HISTORY = "history";

	/**
	 * Constant String variable for cookie to use
	 */
	public static final String VISIT_LASTTIME = "last";

	/**
	 * Servlet constructor
	 * @param queryBuilder queryBuilder queryBuilder object to be used to create query
	 * @param webCrawler webCrawler object
	 * @param suggestQueries linkedList of suggested queries
	 * @param index
	 */
	public Servlet(QueryBuilderInterface queryBuilder, MultiThreadWebCrawler webCrawler, LinkedList<String> suggestQueries, MultiThreadInvertedIndex index) {

		super();

		this.queryBuilder = queryBuilder;

		this.webCrawler = webCrawler;

		this.suggestQueries = suggestQueries;

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");

		preFormat(request, response);

		postFormat(request, response);

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		response.setContentType("text/html");

		String message = request.getParameter("message");

		String seed = request.getParameter("seed");

		seed = seed == null ? "" : seed;

		message = message == null ? "" : message;

		seed = StringEscapeUtils.escapeHtml4(seed);

		message = StringEscapeUtils.escapeHtml4(message);

		preFormat(request, response);

		if(seed.length() != 0) {

			try {

				URL url = new URL(seed);

				webCrawler.webCrawling(url);

				out.printf("				<div class=\"box\">%n");

				out.print("<p> New links resources has been added</p>");

				out.printf("				</div>%n");

				out.printf("%n");

			}catch(MalformedURLException ex) {

				out.printf("				<div class=\"box\">%n");

				out.print("<p style=\"color:#FF0000\"> Invalid URL for Crawling!</h2>");

				out.printf("				</div>%n");

				out.printf("%n");

			}

		}

		addHistory(request, response, message);

		suggestQueries.addFirst(message);

		List<InvertedIndex.SearchResult> links;

		if(request.getParameter("select") == null || request.getParameter("select").equals("partial")) {

			links = queryBuilder.parseLinks(message, false);

		}
		else {

			links = queryBuilder.parseLinks(message, true);

		}

		if(links != null  && links.size() != 0) {

			for(int i = 0; i < links.size(); i++) {

				String link = links.get(i).getLocation();

				String formatted = String.format("<p><a href=/visited?%s>%s</a>  <a style=\"color:#FF0000\" href=/favorite?%s>[favorite]</a> </p>", link, link, link);

				out.printf("				<div class=\"box\">%n");

				out.print(formatted);

				out.printf("				</div>%n");

				out.printf("%n");

			}

		}else {

			out.printf("				<div class=\"box\">%n");

			out.print("No Result");

			out.printf("				</div>%n");

			out.printf("%n");

		}

		postFormat(request, response);

		response.setStatus(HttpServletResponse.SC_OK);

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

	/**
	 * Function for get last login time
	 * @param request the httpServlet request
	 * @param response the httpServleet response
	 * @throws IOException if the socket is not writable
	 */
	private void getLastTime(HttpServletRequest request, HttpServletResponse response) throws IOException {

		PrintWriter out = response.getWriter();

		Map<String, Cookie> cookies = getCookieMap(request);

		Cookie visitLast = cookies.get(VISIT_LASTTIME);

		if(visitLast == null) {

			visitLast = new Cookie(VISIT_LASTTIME, "");

			String encoded = URLEncoder.encode(getShortDate(), StandardCharsets.UTF_8);

			visitLast.setValue(encoded);

			response.addCookie(visitLast);

			out.printf("	<section class=\"hero is-primary is-bold\">%n");

			out.printf("	  <div class=\"hero-body\">%n");

			out.print("<p>Welcome, this is your first time visiting this website</p>");

			out.printf("	  </div>%n");

			out.printf("	</section>%n");

		}
		else {

			String decoded = URLDecoder.decode(visitLast.getValue(), StandardCharsets.UTF_8);

			String escaped = StringEscapeUtils.escapeHtml4(decoded);

			out.printf("	<section class=\"hero is-primary is-bold\">%n");

			out.printf("	  <div class=\"hero-body\">%n");

			out.printf("</p>Last time log in was at %s</p>", escaped);

			out.printf("	  </div>%n");

			out.printf("	</section>%n");

		}

		String encoded = URLEncoder.encode(getShortDate(), StandardCharsets.UTF_8);

		visitLast.setValue(encoded);

		response.addCookie(visitLast);
	}

	/**
	 * Function for adding histories
	 * @param request the httpServlet request
	 * @param response the httpServleet response
	 * @param message history message to be added
	 */
	public void addHistory(HttpServletRequest request, HttpServletResponse response, String message) {

		Map<String, Cookie> cookies = getCookieMap(request);

		Cookie visitHistory = cookies.get(SEARCH_HISTORY);

		if (visitHistory == null ) {

			visitHistory = new Cookie(SEARCH_HISTORY, "");

		}

		if (request.getIntHeader("DNT") != 1) {

			String decoded = URLDecoder.decode(visitHistory.getValue(), StandardCharsets.UTF_8);

			String update = decoded + "-" + message;

			update = update + "<" + getDate() + ">";

			String encoded = URLEncoder.encode(update, StandardCharsets.UTF_8);

			visitHistory.setValue(encoded);

			response.addCookie(visitHistory);
		}

	}

	/**
	 * Function for preparing the html script
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

		out.printf("			<form method=\"%s\" action=\"%s\">%n", "GET", "/shutdown");

		out.printf("				<div class=\"control\">%n");

		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");

		out.printf("						<i class=\"fas fa-comment\"></i>%n");

		out.printf("						&nbsp;%n");

		out.printf("						%n");

		out.printf("					</button>%n");

		out.printf("			  </div>%n");

		out.printf("			</form>%n");

		out.printf("	<section class=\"hero is-primary is-bold\">%n");

		out.printf("	  <div class=\"hero-body\">%n");

		out.printf("	    <div class=\"container\">%n");

		out.printf("	      <h1 class=\"title\">%n");

		out.print("<img src=\"images/brand.png\" width=\"60\" height=\"50\"></img>");

		out.print("<p> <font size=\"3\" face=\"arial\" color=\"white\"><i>Everything is possible</i></font><p>");

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

		out.printf("				<div class=\"box\">%n");

		out.printf("				  <label class=\"label\">Favorite Queries:</label>%n");

		for(int i = 0; i < 5 && i< suggestQueries.size(); i++) {

			out.printf("				<div class=\"box\">%n");

			out.printf("<p>%s </p>", suggestQueries.get(i) );

			out.printf("				</div>%n");

		}

		out.printf("				</div>%n");


		out.printf("				  <label class=\"label\">Message</label>%n");

		out.printf("				  <div class=\"control\">%n");

		out.printf("				    <textarea class=\"textarea\" name=\"%s\" placeholder=\"Enter your message here in alphabetic order.\"></textarea>%n", "message");

		out.printf("				  </div>%n");

		out.printf("				</div>%n");

		out.printf("%n");

		out.printf("				<div class=\"box\">%n");

		out.printf("<details>");

		out.printf("<summary>Choose search method</summary>");

		out.printf("<label><input type=\"radio\" name=\"select\" value=\"partial\"> Partial</label>");

		out.printf("<label style=\"margin-left:20px\"><input type=\"radio\" name=\"select\" value=\"exact\"> Exact</label>");

		out.printf("</details>");

		out.printf("%n");

		out.printf("				</div>%n");

		out.printf("				<div class=\"control\">%n");

		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");

		out.printf("						<i class=\"fas fa-comment\"></i>%n");

		out.printf("						&nbsp;%n");

		out.printf("						Post Message%n");

		out.printf("					</button>%n");

		out.printf("			  </div>%n");

		out.printf("			</form>%n");

		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", "/lucky");

		out.printf("					<div class=\"control has-icons-left\">%n");

		out.printf("						<input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Enter new message here.\">%n", "message");

		out.printf("						<span class=\"icon is-small is-left\">%n");

		out.printf("							<i class=\"fas fa-user\"></i>%n");

		out.printf("						</span>%n");

		out.printf("					</div>%n");

		out.printf("%n");

		out.printf("				<div class=\"control\">%n");

		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");

		out.printf("						<i class=\"fas fa-comment\"></i>%n");

		out.printf("						&nbsp;%n");

		out.printf("						I feel Lucky%n");

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

		out.printf("						Search History%n");

		out.printf("					</button>%n");

		out.printf("			  </div>%n");

		out.printf("			</form>%n");

		out.printf("			<form method=\"%s\" action=\"%s\">%n", "GET", "/visited");

		out.printf("				<div class=\"control\">%n");

		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");

		out.printf("						<i class=\"fas fa-comment\"></i>%n");

		out.printf("						&nbsp;%n");

		out.printf("						Visited History%n");

		out.printf("					</button>%n");

		out.printf("			  </div>%n");

		out.printf("			</form>%n");

		out.printf("			<form method=\"%s\" action=\"%s\">%n", "GET", "/index");

		out.printf("				<div class=\"control\">%n");

		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");

		out.printf("						<i class=\"fas fa-comment\"></i>%n");

		out.printf("						&nbsp;%n");

		out.printf("						InvertedIndex%n");

		out.printf("					</button>%n");

		out.printf("			  </div>%n");

		out.printf("			</form>%n");

		out.printf("			<form method=\"%s\" action=\"%s\">%n", "GET", "/favorite");

		out.printf("				<div class=\"control\">%n");

		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");

		out.printf("						<i class=\"fas fa-comment\"></i>%n");

		out.printf("						&nbsp;%n");

		out.printf("						Favorite%n");

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
	}
}