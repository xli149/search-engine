import java.util.LinkedList;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Creates a web server to allow users to fetch HTTP headers for a URL.
 */
public class SearchEngine  {

	/**
	 * Port to be used
	 */
	public final int port;

	/**
	 * QueryBuilder object passed in to be used
	 */
	private final QueryBuilderInterface queryBuilder;

	/**
	 * The webCrawler object for crawling a webSite
	 */
	private final MultiThreadWebCrawler webCrawler;

	/**
	 * A linkedList for suggest queries
	 */
	private final LinkedList<String> suggestQueries;

	/**
	 * The MultiThreadInvertedIndex object
	 */
	private final MultiThreadInvertedIndex index;
	/**
	 * SearchEngine constructor
	 * @param port the port to be used by server
	 * @param queryBuilder queryBuilder object to be used to create query
	 * @param webCrawler the webCrawler object
	 * @param index the MultiThreadInvertedIndex
	 */
	public SearchEngine(int port, QueryBuilderInterface queryBuilder, MultiThreadWebCrawler webCrawler, MultiThreadInvertedIndex index) {

		this.port = port;

		this.queryBuilder = queryBuilder;

		this.webCrawler = webCrawler;

		this.index = index;

		this.suggestQueries = new LinkedList<>();

	}

	/**
	 * Starts a Jetty server on the port and maps /check requests to the servlet
	 * @throws Exception
	 */

	public void server() throws Exception {

		Server server = new Server(port);

		ResourceHandler resourceHandler = new ResourceHandler();

		resourceHandler.setResourceBase("logo");

		resourceHandler.setDirectoriesListed(true);

		ContextHandler resourceContext = new ContextHandler("/images");

		resourceContext.setHandler(resourceHandler);

		ServletHandler handler = new ServletHandler();

		handler.addServletWithMapping(new ServletHolder(new Servlet(queryBuilder, webCrawler, suggestQueries, index)), "/" );

		handler.addServletWithMapping(new ServletHolder(new LocationServlet(index)), "/counts" );

		handler.addServletWithMapping(new ServletHolder(new SearchHistoryServlet()), "/history");

		handler.addServletWithMapping(new ServletHolder(new VisitedHistoryServlet()), "/visited");

		handler.addServletWithMapping(new ServletHolder(new GraceShutdownServlet(server)), "/shutdown");

		handler.addServletWithMapping(new ServletHolder(new LuckyServlet(queryBuilder)), "/lucky");

		handler.addServletWithMapping(new ServletHolder(new InvertedIndexServlet(index)), "/index");

		handler.addServletWithMapping(new ServletHolder(new AddFavoriteServlet()), "/favorite");

		HandlerList handlers = new HandlerList();

		handlers.setHandlers(new Handler[] { resourceContext, handler});

		server.setHandler(handlers);

		server.start();

		server.join();

	}

}