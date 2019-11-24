import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Creates a web server to allow users to fetch HTTP headers for a URL.
 */
public class SearchEngine {

	public final int port;

	private final QueryBuilderInterface queryBuilder;

	public SearchEngine(int port, QueryBuilderInterface queryBuilder) {

		this.port = port;

		this.queryBuilder = queryBuilder;

	}

	/**
	 * Starts a Jetty server on port 8080, and maps /check requests to the
	 * {@link HeaderServlet}.
	 *
	 * @param args - unused
	 * @throws Exception
	 */

	public void server() throws Exception {

		Server server = new Server(port);

		ServletHandler handler = new ServletHandler();

		handler.addServletWithMapping(new ServletHolder(new Servlet(queryBuilder)), "/check" );

		server.setHandler(handler);

		server.start();

		server.join();
	}

}
