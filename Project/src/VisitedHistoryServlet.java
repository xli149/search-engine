import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Servlet class for handling visited histories
 * @author chrislee
 *
 */
public class VisitedHistoryServlet extends CookieBaseServlet {

	/**
	 * Serial ID not used
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Title of the link
	 */
	private static final String TITLE = "Visited History";

	/**
	 * Used to fetch the history from a cookie.
	 */
	public static final String VISIT_HISTORY = "visited";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		preFormat(request, response);

		String queryString = null;

		if(request.getQueryString() != null) {

			queryString = URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8);

			queryString = StringEscapeUtils.escapeHtml4(queryString);

		}

		log.info("GET " + request.getRequestURL().toString());

		if (request.getRequestURI().endsWith("favicon.ico")) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		prepareResponse("Cookies!", response);

		Map<String, Cookie> cookies = getCookieMap(request);

		Cookie visitHistory = cookies.get(VISIT_HISTORY);

		PrintWriter out = response.getWriter();

		out.printf("<p>");

		if (visitHistory == null ) {

			visitHistory = new Cookie(VISIT_HISTORY, "");

			out.printf("<h2>Visited Websites<h2>");

		}
		else {
			try {

				String decoded = URLDecoder.decode(visitHistory.getValue(), StandardCharsets.UTF_8);

				String escaped = StringEscapeUtils.escapeHtml4(decoded);

				String [] histories = escaped.split("-");

				for(var history : histories) {

					out.printf("<p>%s<p>", history);

				}
			}
			catch (NullPointerException | IllegalArgumentException e) {

				out.printf("Unable to store the history. ");

				visitHistory = new Cookie(VISIT_HISTORY, "");

			}
		}

		out.printf("</p>%n");

		if (request.getIntHeader("DNT") != 1 && queryString != null && queryString.length() != 0) {

			String decoded = URLDecoder.decode(visitHistory.getValue(), StandardCharsets.UTF_8);

			String update = decoded + "-" + queryString;

			String encoded = URLEncoder.encode(update, StandardCharsets.UTF_8);

			visitHistory.setValue(encoded);

			response.addCookie(visitHistory);

			response.sendRedirect(queryString);
		}

		postFormat(request, response);

		finishResponse(request, response);

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html");

		clearCookies(request,response);

		response.setStatus(HttpServletResponse.SC_OK);

		response.sendRedirect(request.getServletPath());
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

		out.printf("	        Visited History%n");

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

		out.printf("			<h2 class=\"title\">History</h2>%n");

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

		out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());

		out.printf("<p><input type=\"submit\" value=\"Clean\"></p>\n%n");

		out.printf("</form>\n%n");

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