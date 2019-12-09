import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for webCrawling websites
 * @author chrislee
 *
 */
public class MultiThreadWebCrawler{

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * Limit number per crawl to increase
	 */
	public static final int CHUNCK = 50;

	/**
	 * Logger Object for logging purpose
	 */
	private final static Logger logger = LogManager.getLogger();

	/**
	 * Declare the MultiThreadInvertedIndex variable
	 */
	private final MultiThreadInvertedIndex index;

	/**
	 * The limit of times to crawl a web site
	 */
	private int limit;

	/**
	 * Work queue to be used for passing the tasks
	 */
	private WorkQueue queue;

	/**
	 * A set for checking repeating urls
	 */
	private Set<URL> lookUp;

	/**
	 * Default constructor
	 * @param index the object of MultiThreadInvertedIndex
	 * @param queue Work queue to be used for passing the tasks
	 */
	public MultiThreadWebCrawler(MultiThreadInvertedIndex index, WorkQueue queue) {

		this(index, 50,  queue);

	}

	/**
	 * Constructor
	 * @param index the object of MultiThreadInvertedIndex
	 * @param limit the limit number of link to crawl
	 * @param queue Work queue to be used for passing the tasks
	 */
	public MultiThreadWebCrawler(MultiThreadInvertedIndex index, int limit, WorkQueue queue) {

		this.index = index;

		this.limit = limit;

		this.queue = queue;

		lookUp = new HashSet<>();

	}

	/**
	 * Get the links of
	 * @param url the url to be used for fetching links
	 * @return an arrayList of links
	 * @throws MalformedURLException
	 */
	private ArrayList<URL> getLinks(String url) throws MalformedURLException {

		String html = HtmlFetcher.fetch(url);

		ArrayList<URL> links = LinkParser.listLinks(new URL(url), html);

		return links;

	}

	/**
	 * Static method that reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 * @param url the url to be added into InvertedIndex
	 * @param index the invertedIndex Object
	 * @throws IOException if unable to read or parse file
	 */
	public static void addStem(URL url, InvertedIndex index) throws IOException {

		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		String uncleanedHtml = HtmlFetcher.fetch(url);

		if(uncleanedHtml == null) {

			return;

		}

		String cleanedHtml = null;

		cleanedHtml = HtmlCleaner.stripHtml(uncleanedHtml);

		int posOfWord = 1;

		String [] tokens = TextParser.parse(cleanedHtml);

		String stemmedWords;

		for (int i = 0; i < tokens.length; i++) {

			stemmedWords = stemmer.stem(tokens[i]).toString();

			index.add(stemmedWords, url.toString(), posOfWord);

			posOfWord++;

		}
	}

	/**
	 * Method for synchronized adding limits for a new crawl
	 */
	public synchronized void incrementLimit() {

		limit += CHUNCK;

	}

	/**
	 * Method for adding new seed
	 * @param seed the Url to be crawled
	 * @throws MalformedURLException if Url is not valid
	 */
	public void newCrawling(URL seed) throws MalformedURLException {

		incrementLimit();

		webCrawling(seed);

	}

	/**
	 * Method for crawling webs by BFS
	 * @param seed a new url to be crawled
	 * @throws MalformedURLException if url is not correctly provided
	 */
	public void webCrawling(URL seed) throws MalformedURLException {

		queue.execute(new Task(seed));

		try {

			queue.finished();

		} catch (InterruptedException e) {

			logger.debug("Thread: " + Thread.currentThread().getId() + "gets interrupted");
		}

		logger.debug("travers dirctory finished: Thread: " + Thread.currentThread().getId() );

	}

	/**
	 * Nested class for creating Tasks
	 * @author chrislee
	 */
	private class Task implements Runnable{

		/**
		 * The path of directory
		 */
		private URL url;

		/**
		 * Constructor
		 * @param url the url to be added and generated more urls
		 */
		public Task(URL url) {

			this.url = url;

			lookUp.add(url);

		}

		@Override
		public void run() {

			try {

				InvertedIndex local = new InvertedIndex();

				Iterator<URL> itr = null;

				ArrayList<URL> links = getLinks(url.toString());

				itr = links.iterator();

				MultiThreadWebCrawler.addStem(url, local);

				synchronized(queue) {

					if(itr != null) {

						while(itr.hasNext() && limit > 1) {

							URL url = itr.next();

							if (!lookUp.contains(url)) {

								queue.execute(new Task(url));

								limit--;

							}

						}

						index.addAll(local);
					}
				}

			}catch (IOException e) {

				logger.debug("Thread: " + Thread.currentThread().getId() + "gets IOException");

			}

			logger.debug("Query finished parsing file: " + Thread.currentThread().getId());
		}

	}

}