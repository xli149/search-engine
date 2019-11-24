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
	 * Logger Object for logging purpose
	 */
	private final static Logger logger = LogManager.getLogger();

	/**
	 * Declare the MultiThreadInvertedIndex variable
	 */
	private final MultiThreadInvertedIndex index;

	private int limit;

	private WorkQueue queue;

	/**
	 * The seed url
	 */
	private URL seed;

	private Set<URL> lookUp;

	/**
	 * Default constructor
	 * @param index the object of MultiThreadInvertedIndex
	 * @param seed the seed link for web crawling
	 */
	public MultiThreadWebCrawler(MultiThreadInvertedIndex index, URL seed, WorkQueue queue) {

		this(index, 50, seed, queue);

	}

	/**
	 * Constructor
	 * @param index the object of MultiThreadInvertedIndex
	 * @param limit the limit number of link to crawl
	 * @param seed the seed link for web crawling
	 */
	public MultiThreadWebCrawler(MultiThreadInvertedIndex index, int limit, URL seed, WorkQueue queue) {

		this.index = index;

		this.limit = limit;

		this.seed = seed;

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

		//		if (url.equals(new URL("https://www.cs.usfca.edu/~cs212/docs/jdk-12.0.2_doc-all/api/java.desktop/java/awt/desktop/AboutHandler.html"))) {
		//			//			System.out.println("BOOM");
		//			//			System.out.println(uncleanedHtml);
		//			logger.debug(uncleanedHtml);
		//			cleanedHtml = HtmlCleaner.stripBlockElements(uncleanedHtml);
		//
		//			//			cleanedHtml = HtmlCleaner.stripTags(cleanedHtml);
		//
		//			System.out.println(cleanedHtml);
		//
		//
		//
		//		}
		//		else {
		cleanedHtml = HtmlCleaner.stripHtml(uncleanedHtml);
		//		}

		int posOfWord = 1;



		String [] tokens = TextParser.parse(cleanedHtml);

		String stemmedWords;

		//		logger.debug(url);
		//
		//		ArrayList<String> test = new ArrayList<String>();
		//
		//		Collections.addAll(test, tokens);

		//		logger.debug(test);

		for (int i = 0; i < tokens.length; i++) {

			stemmedWords = stemmer.stem(tokens[i]).toString();

			//			logger.debug(tokens[i]);

			//			if(i%10 == 0) {
			//
			//				//				logger.debug("\n");
			//			}

			index.add(stemmedWords, url.toString(), posOfWord);

			posOfWord++;

		}
	}

	/**
	 * Method for crawling webs by BFS
	 * @param threads number of worker threads
	 * @throws MalformedURLException
	 */
	public void webCrawling() throws MalformedURLException {

		//		logger.debug("travers website's link starts: Thread: " + Thread.currentThread().getId());

		queue.execute(new Task(seed));

		try {

			queue.finished();

			//			queue.shutDown();

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
		 * @param index object to MultiThreadInvertedIndex
		 * @param queue work queue
		 * @param limit number allowed for parsing links
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

				if(url.toString().startsWith("https://www.cs.usfca.edu/~cs212/docs/jdk-12.0.2_doc-all/api/java.desktop/java/awt/desktop/AboutHandle")){
					System.out.println(local);
				}

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