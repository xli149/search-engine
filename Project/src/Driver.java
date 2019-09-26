import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;



/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @author chrislee
 * @version Fall 2019
 */
public class Driver {

	/*
	 * TODO Can simplify a bit.
	 */
	/**
	 * Getting the path of file for reading a file by checking a map
	 * @param mapOfArgs a collection of  parsed arguments from command line
	 * @return the path of a file if the key exist or return null if the key doesn't exist
	 */
	public Path pathOfInput(Map<String, String> mapOfArgs) {

		if(mapOfArgs.containsKey("-path") && mapOfArgs.get("-path") != null) {

			return Path.of(mapOfArgs.get("-path"));

		}

		return null;

	}

	/**
	 * Getting the path of file for writing the word index of a file by checking
	 * a map
	 * @param mapOfArgs a collection of  parsed arguments from command line
	 * @return the path of a file or a default path if the key exist,
	 * or return null if the key doesn't exist
	 */
	public Path pathOfOutput(Map<String, String> mapOfArgs) {

		if(mapOfArgs.containsKey("-index") && mapOfArgs.get("-index")!= null) {

			return Path.of(mapOfArgs.get("-index"));

		}

		else if(mapOfArgs.containsKey("-index") && mapOfArgs.get("-index") == null){

			return Path.of("index.json");
		}

		else {

			return null;

		}

	}

	/**
	 * Getting the path of file for writing the word count of a file by checking
	 * a map
	 * @param mapOfArgs a collection of  parsed arguments from command line
	 * @return the path of a file or a default path if the key exist,
	 * or return null if the key doesn't exist
	 */
	public Path countsOutput(Map<String, String> mapOfArgs) {

		if(mapOfArgs.containsKey("-counts") && mapOfArgs.get("-counts")!= null) {

			return Path.of(mapOfArgs.get("-counts"));

		}

		else if(mapOfArgs.containsKey("-counts") && mapOfArgs.get("-counts") == null) {

			return Path.of("counts.json");
		}

		else {

			return null;

		}

	}

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args){


		Instant start = Instant.now();


		/* TODO Try this for Driver instead
			ArgumentParser parser = new ArgumentParser(args);
			InvertedIndex elements = new InvertedIndex();

			if (parser.hasFlag("-path")) {

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

			}
		 */


		ArgumentParser parser = new ArgumentParser(args);

		//Driver dri = new Driver();

		InvertedIndex elements = new InvertedIndex();

		//Map<String, String> mapOfArgs = parser.parse(args);
		//
		//			Path relativePath = dri.pathOfInput(mapOfArgs);
		//
		//			if(relativePath != null) {
		//
		//				InvertedIndexBuilder.checkFile(relativePath, elements);
		//
		//			}
		//
		//			Path outPutFile = dri.pathOfOutput(mapOfArgs);
		//
		//			if(outPutFile != null) {
		//
		//				elements.indexToJson(outPutFile);
		//
		//			}
		//
		//			Path countsFilePath = dri.countsOutput(mapOfArgs);
		//
		//			if(countsFilePath != null) {
		//
		//				elements.wordCountToJson(countsFilePath);
		//
		//			}

		if (parser.hasFlag("-path")) {

			Path path = parser.getPath("-path");

			try{

				InvertedIndexBuilder.checkFile(path, elements);

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

			System.out.println("GOT HERERE");

			Path path = parser.getPath("-count", Path.of("counts.json"));

			System.out.println(path.toString());

			try {

				elements.wordCountToJson(path);

				BufferedReader rd = Files.newBufferedReader(path);

				String line = null;

				while((line = rd.readLine()) != null) {
					System.out.println(line);
				}

				System.out.println("Is the file readable after writting in?  " + Files.isReadable(path));

			}
			catch (IOException | NullPointerException e){
				System.out.println("Uable to write word count to JSON file: " + path);
			}
		}







		Duration elapsed = Duration.between(start, Instant.now());

		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();

		System.out.printf("Elapsed: %f seconds%n", seconds);

	}
}

