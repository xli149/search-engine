import java.io.IOException;
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

		InvertedIndex elements = new InvertedIndex();

		InvertedIndexBuilder builder = new InvertedIndexBuilder(elements);

		QueryBuilder queryBuilder = new QueryBuilder(elements);

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

				elements.indexToJson(path);

			}
			catch (IOException e) {

				System.out.println("Unable to write inverted index to JSON file: " + path);
			}
		}

		if (parser.hasFlag("-counts")) {

			Path path = parser.getPath("-counts", Path.of("counts.json"));

			try {

				elements.wordCountToJson(path);

			}
			catch (IOException e){

				System.out.println("Unable to write word count to JSON file: " + path);
			}
		}

		if(parser.hasFlag("-query")) {

			Path path = parser.getPath("-query");

			try{

				if(parser.hasFlag("-exact")) {

					queryBuilder.parseFile(path, true);

				}
				else {

					queryBuilder.parseFile(path, false);

				}
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

		Duration elapsed = Duration.between(start, Instant.now());

		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();

		System.out.printf("Elapsed: %f seconds%n", seconds);

	}
}

