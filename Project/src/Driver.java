import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;



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
	/** A collection of word index*/
	public static TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements;
	/** A collection of number of words in a file*/
	public static TreeMap<String, Integer> counts;

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

	// TODO The only place you should never throw an exception is Driver.main
	/*
	 * TODO
	 * Driver should not have any "generally useful" code. Anything that another developer
	 * might find useful should be in a separate class.
	 *
	 *
	 * public class InvertedIndex {
	 * 		make this a "data structure" class
	 * 		how to store and output information
	 * 		(no string or file parsing directly)
	 *
	 * 		private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements;
	 *
	 *		constructor
	 *		toString
	 *
	 *		add(String wordStem, String filePath, int position)
	 *
	 *		writeIndex or toJSON...
	 * }
	 *
	 * public class InvertedIndexBuilder {
	 * 		the code you have now in your current inverted index class
	 * 		parsing the directories, opening files, stemming, etc.
	 * 		call index.add(...) to add things to your inverted index data structure
	 * }
	 *
	 */

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 * @throws FileNotFoundException if the file is unable to be found
	 * @throws IOException if the file is unable to read or write in
	 * trying to make some changes
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException{
		Instant start = Instant.now();
		System.out.println(Arrays.toString(args));
		ArgumentParser parser = new ArgumentParser();
		Driver dri = new Driver();
		InvertedIndex index = new InvertedIndex();
		elements = new TreeMap<>();
		counts = new TreeMap<>();
		mapOfArgs = parser.parse(args);
		Path relativePath = dri.PathOfInput(mapOfArgs);
		if(relativePath != null) {
			index.ProcessFiles(relativePath, elements, counts);
		}
		Path outPutFile = dri.PathOfOutput(mapOfArgs);
		if(outPutFile != null) {
			SimpleJsonWriter.asNestedObject(elements, outPutFile);
		}
		Path countsFilePath = dri.CountsOutput(mapOfArgs);
		if(countsFilePath != null) {
			System.out.println("path: " + countsFilePath.toString());
			SimpleJsonWriter.wordCountsPrinter(counts, countsFilePath);
		}
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}
}
