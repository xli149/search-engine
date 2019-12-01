import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

public class VisitedHistoryServlet extends CookieBaseServlet {

	/** Used to fetch the history from a cookie. */
	public static final String VISIT_HISTORY = "visited";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String queryString = null;

		if(request.getQueryString() != null) {

			queryString = URLDecoder.decode(request.getQueryString(), StandardCharsets.UTF_8);

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

		// Update visit count as necessary and output information.
		if (visitHistory == null ) {

			visitHistory = new Cookie(VISIT_HISTORY, "");

			out.printf("<h2>Visited Websites<h2>");

			//			out.printf("<p>You don't have any visited history yet</p>");
		}
		else {
			try {

				// We have to do extra work to make sure the String value is safe
				String decoded = URLDecoder.decode(visitHistory.getValue(), StandardCharsets.UTF_8);
				String escaped = StringEscapeUtils.escapeHtml4(decoded);
				//				log.info("Encoded: " + visitDate.getValue() + ", Decoded: " + decoded + ", Escaped: " + escaped);

				out.printf("<h2>Visited Websites<h2>");

				String [] histories = escaped.split("-");

				for(var history : histories) {

					out.printf("<p>%s<p>", history);

				}
			}
			catch (NullPointerException | IllegalArgumentException e) {
				// reset to safe values
				out.printf("Unable to store the history. ");

				visitHistory = new Cookie(VISIT_HISTORY, "");

			}
		}

		out.printf("</p>%n");

		// Checks if the browser indicates visits should not be tracked.
		// This is not a standard header!
		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/DNT
		// https://support.mozilla.org/en-US/kb/how-do-i-turn-do-not-track-feature
		// https://www.macworld.com/article/3338152/apple-safari-removing-do-not-track.html
		if (request.getIntHeader("DNT") != 1 && queryString != null && queryString.length() != 0) {


			//			System.out.println("query: " + "\""+queryString+"\"");

			String decoded = URLDecoder.decode(visitHistory.getValue(), StandardCharsets.UTF_8);

			String update = decoded + "-" + queryString;

			//			System.out.println(update);
			String encoded = URLEncoder.encode(update, StandardCharsets.UTF_8);

			visitHistory.setValue(encoded);

			response.addCookie(visitHistory);

			response.sendRedirect(queryString);
		}



		printForm(request, response);

		finishResponse(request, response);

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html");

		//		historys.clear();

		clearCookies(request,response);

		//		PrintWriter out = response.getWriter();

		//		out.printf("History has been cleaned");

		response.setStatus(HttpServletResponse.SC_OK);

		response.sendRedirect(request.getServletPath());
	}

	private static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getServletPath());
		out.printf("<p><input type=\"submit\" value=\"Clean\"></p>\n%n");
		out.printf("</form>\n%n");
	}

}
