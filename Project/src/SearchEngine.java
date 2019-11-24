import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Creates a web server to allow users to fetch HTTP headers for a URL.
 */
public class SearchEngine {

	/**
	 * Port to be used
	 */
	public final int port;

	/**
	 * QueryBuilder object passed in to be used
	 */
	private final QueryBuilderInterface queryBuilder;

	/**
	 * SearchEngine constructor
	 * @param port the port to be used by server
	 * @param queryBuilder queryBuilder object to be used to create query
	 */
	public SearchEngine(int port, QueryBuilderInterface queryBuilder) {

		this.port = port;

		this.queryBuilder = queryBuilder;

	}

	/**
	 * Starts a Jetty server on the port and maps /check requests to the servlet
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
