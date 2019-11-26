import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Demonstrates how to create, use, and clear cookies.
 *
 * @see CookieBaseServlet
 * @see CookieIndexServlet
 * @see CookieConfigServlet
 */
@SuppressWarnings("serial")
public class CookieIndexServlet extends CookieBaseServlet {


	//	public CookieIndexServlet(String his)

	/** Used to fetch the history from a cookie. */
	public static final String VISIT_HISTORY = "history";


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

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

			//			visitHistory = new Cookie(VISIT_HISTORY, "");

			out.printf("<p>You don't have any history</p>");
		}
		else {
			try {

				// We have to do extra work to make sure the String value is safe
				String decoded = URLDecoder.decode(visitHistory.getValue(), StandardCharsets.UTF_8);
				String escaped = StringEscapeUtils.escapeHtml4(decoded);
				//				log.info("Encoded: " + visitDate.getValue() + ", Decoded: " + decoded + ", Escaped: " + escaped);

				out.printf("<h2>History<h2>");

				String [] histories = escaped.split("-");

				for(var history : histories) {

					out.printf("<p>%s<p>", history);

				}
			}
			catch (NullPointerException | IllegalArgumentException e) {
				// reset to safe values

				visitHistory = new Cookie(VISIT_HISTORY, "");

				out.printf("Unable to store the history. ");
			}
		}

		out.printf("</p>%n");

		// Checks if the browser indicates visits should not be tracked.
		// This is not a standard header!
		// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/DNT
		// https://support.mozilla.org/en-US/kb/how-do-i-turn-do-not-track-feature
		// https://www.macworld.com/article/3338152/apple-safari-removing-do-not-track.html
		//		if (request.getIntHeader("DNT") != 1) {
		//
		//			String decoded = URLDecoder.decode(visitHistory.getValue(), StandardCharsets.UTF_8);
		//
		//			String update = decoded + "-" + request.getParameter();
		//			String encoded = URLEncoder.encode(update, StandardCharsets.UTF_8);
		//			visitHistory.setValue(encoded);
		//			response.addCookie(visitHistory);
		//		}
		//		else {
		//			clearCookies(request, response);
		//			out.printf("<p>Your visits will not be tracked.</p>");
		//		}


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