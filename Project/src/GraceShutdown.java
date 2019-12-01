import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;

public class GraceShutdown extends HttpServlet{

	Server server;

	public GraceShutdown(Server server) {

		this.server = server;

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		printForm(request, response);

		out.printf("Thanks for visiting our website! See you next time");

		response.setStatus(HttpServletResponse.SC_OK);

		try {
			server.setStopTimeout(0);

			server.stop();

		} catch (Exception e) {

			out.printf("Sorry... unable to shutdown your Engine");

		}

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
