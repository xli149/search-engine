import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Servlet for adding new crawls
 * @author chrislee
 *
 */
public class AddNewCrawl extends HttpServlet{

	/**
	 * Default serial number that is not used here
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instance of {@link MultiThreadWebCrawler}
	 */
	MultiThreadWebCrawler webCrawler;

	/**
	 * The title of this link
	 */
	private static final String TITLE = "New Crawl";

	/**
	 * Default pass word
	 */
	private static final String PASSWORD = "123";

	/**
	 * Constructor
	 * @param webCrawler
	 */
	public AddNewCrawl(MultiThreadWebCrawler webCrawler) {

		this.webCrawler = webCrawler;

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

		String seed = request.getParameter("seed");

		seed = seed == null ? "" : seed;

		seed = StringEscapeUtils.escapeHtml4(seed);

		String passWord = request.getParameter("passwd");

		passWord = StringEscapeUtils.escapeHtml4(passWord);

		preFormat(request, response);

		if(passWord.equals(PASSWORD)) {

			if(seed.length() != 0) {

				try {

					URL url = new URL(seed);

					webCrawler.newCrawling(url);

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
		}else {

			out.printf("				<div class=\"box\">%n");

			out.print("<p style=\"color:#FF0000\"> Pass Word Invalid! Please Try Again..</h2>");

			out.printf("				</div>%n");

			out.printf("%n");

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

		out.printf("	       Locations%n");

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

		out.printf("			<h2 class=\"title\"> Links and Locations</h2>%n");

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

		out.printf("							<i class=\"fas fa-spinner fa-pulse\"></i>%n");

		out.printf("						</span>%n");

		out.printf("					</div>%n");

		out.printf("				</div>%n");

		out.printf("%n");

		out.printf("				<div class=\"field\">%n");

		out.printf("					<label class=\"label\">Pass Word Required For Administer</label>%n");

		out.printf("					<div class=\"control has-icons-left\">%n");

		out.printf("						<input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Enter pass word here.\">%n", "passwd");

		out.printf("						<span class=\"icon is-small is-left\">%n");

		out.printf("							<i class=\"fas fa-user\"></i>%n");

		out.printf("						</span>%n");

		out.printf("					</div>%n");

		out.printf("				</div>%n");

		out.printf("%n");

		out.printf("				<div class=\"control\">%n");

		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");

		out.printf("						<i class=\"fas fa-comment\"></i>%n");

		out.printf("						&nbsp;%n");

		out.printf("						Add NewCrawl%n");

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
