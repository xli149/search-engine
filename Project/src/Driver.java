import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author chrislee
 * @version Fall 2019
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args){

		System.out.println("Driver start");

		Instant start = Instant.now();

		ArgumentParser parser = new ArgumentParser(args);

		InvertedIndex index;

		MultiThreadInvertedIndex threadSafe = null;

		InvertedIndexBuilder builder;

		QueryBuilderInterface queryBuilder;

		WorkQueue workers = null;

		MultiThreadWebCrawler webCrawler = null;

		SearchEngine searchEngine;

		String seed;

		int limit;

		int port;

		if(parser.hasValue("-limit")) {

			limit = Integer.parseInt(parser.getString("-limit"));

			limit = limit > 0 ? limit : 50;

		}
		else{

			limit = 50;
		}

		if(parser.hasFlag("-threads")) {

			int threads;

			try {

				threads = Integer.parseInt(parser.getString("-threads", "5"));

				if(threads <= 0) {

					threads = 5;
				}

				workers = new WorkQueue(threads);

			} catch(NumberFormatException ex) {

				workers = new WorkQueue();

			}

			threadSafe = new MultiThreadInvertedIndex();

			index = threadSafe;

			builder = new MultiThreadInvertedIndexBuilder(threadSafe, workers);

			queryBuilder = new MultiThreadQueryBuilder(threadSafe, workers);

		}
		else {
			index = new InvertedIndex();

			builder = new InvertedIndexBuilder(index);

			queryBuilder = new QueryBuilder(index);

		}

		if(parser.hasFlag("-url")) {

			try {

				threadSafe = new MultiThreadInvertedIndex();

				if(!parser.hasFlag("-thread")) {

					workers = new WorkQueue();

					index = threadSafe;

					builder = new MultiThreadInvertedIndexBuilder(threadSafe, workers);

					queryBuilder = new MultiThreadQueryBuilder(threadSafe, workers);
				}

				seed = parser.getString("-url");

				webCrawler = new MultiThreadWebCrawler(threadSafe,limit, workers);

				webCrawler.webCrawling(new URL(seed));

			} catch (MalformedURLException e) {

				System.out.println("Invalid url");

			}

		}

		if (parser.hasFlag("-path")) {

			Path path = parser.getPath("-path");

			try{

				builder.traversDirectory(path);

			}
			catch(IOException | NullPointerException e) {

				System.out.println("file path is not valid, please check the file path: " + path);

			}

		}

		if (parser.hasFlag("-index")) {

			Path path = parser.getPath("-index", Path.of("index.json"));

			try {

				index.indexToJson(path);

			}
			catch (IOException e) {

				System.out.println("Unable to write inverted index to JSON file: " + path);
			}
		}

		if (parser.hasFlag("-counts")) {

			Path path = parser.getPath("-counts", Path.of("counts.json"));

			try {

				index.wordCountToJson(path);

			}
			catch (IOException e){

				System.out.println("Unable to write word count to JSON file: " + path);
			}
		}

		if(parser.hasFlag("-query")) {

			Path path = parser.getPath("-query");

			try{

				queryBuilder.parseFile(path, parser.hasFlag("-exact"));

			}
			catch(IOException e) {

				System.out.println("Unable to read the query " + path);

			}
			catch(NullPointerException e) {

				System.out.println("Path is not valid " + path);

			}

		}

		if(parser.hasFlag("-results")) {

			Path path = parser.getPath("-results", Path.of("results.json"));

			try {

				queryBuilder.queryToJson(path);

			}
			catch(IOException e) {

				System.out.println("Unable to write query in the file " + path);

			}

		}

		if(parser.hasFlag("-port")) {

			port = Integer.parseInt(parser.getString("-port", "8080"));

			searchEngine = new SearchEngine(port, queryBuilder, webCrawler, threadSafe);

			try {

				searchEngine.server();

			} catch (Exception e) {

				System.out.println("Server is unable to set up");

			}

		}

		if(workers != null) {

			workers.shutDown();

		}

		Duration elapsed = Duration.between(start, Instant.now());

		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();

		System.out.printf("Elapsed: %f seconds%n", seconds);

	}
}