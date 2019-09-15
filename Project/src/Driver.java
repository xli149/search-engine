import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
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
	 * TODO
	 * Should not have so many static members, and maybe not in this class.
	 */

	/** A map of parsed arguments from command line*/
	public static Map<String, String> mapOfArgs;

	/**
	 * Getting the path of file for reading a file by checking a map
	 * @param mapOfArgs a collection of  parsed arguments from command line
	 * @return the path of a file if the key exist or return null if the key doesn't exist
	 */
	public Path PathOfInput(Map<String, String> mapOfArgs) {
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
	public Path PathOfOutput(Map<String, String> mapOfArgs) {
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
	public Path CountsOutput(Map<String, String> mapOfArgs) {
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
		System.out.println(Arrays.toString(args));
		ArgumentParser parser = new ArgumentParser();
		Driver dri = new Driver();
		InvertedIndexBuilder index = new InvertedIndexBuilder();
		mapOfArgs = parser.parse(args);
		Path relativePath = dri.PathOfInput(mapOfArgs);
		if(relativePath != null) {
			InvertedIndexBuilder.checkFile(relativePath);
		}
		Path outPutFile = dri.PathOfOutput(mapOfArgs);
		if(outPutFile != null) {
			index.getInvertedIndexObject().indexToJson(outPutFile);
		}
		Path countsFilePath = dri.CountsOutput(mapOfArgs);
		if(countsFilePath != null) {
			index.getInvertedIndexObject().wordCountToJson(countsFilePath);
		}
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
