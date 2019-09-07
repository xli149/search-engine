import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;



/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class Driver {

	public static Map<String, String> mapOfArgs;
	public static TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements;
	public static TreeMap<String, Integer> counts;
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 * trying to make some changes
	 */
	
	public Path PathOfInput(Map<String, String> mapOfArgs) {
		if(mapOfArgs.containsKey("-path") && (mapOfArgs.get("-path") != null)) {
			return Path.of(mapOfArgs.get("-path"));
		}
		return null;
	}
	
	public Path PathOfOutput(Map<String, String> mapOfArgs) {
		if(mapOfArgs.containsKey("-index") && (mapOfArgs.get("-index")!= null)) {
			return Path.of(mapOfArgs.get("-index"));
			
		}
		else if(mapOfArgs.containsKey("-index") && (mapOfArgs.get("-index") == null)){
			return Path.of("index.json");
		}
		else {
			return null;
		}
	}
	public Path CountsOutput(Map<String, String> mapOfArgs) {
		if(mapOfArgs.containsKey("-counts") && (mapOfArgs.get("-counts")!= null)) {
			return Path.of(mapOfArgs.get("-counts"));
			
		}
		else if(mapOfArgs.containsKey("-counts") && mapOfArgs.get("-counts") == null) {
			return Path.of("counts.json");
		}
		else {
			return null;
		}
	}
	

	
	public static void main(String[] args) throws FileNotFoundException, IOException{
		
		// store initial start time
		Instant start = Instant.now();
		 //TODO Fill in and modify this method as necessary.
		System.out.println(Arrays.toString(args));
		//create a new parser to parse argument;
		ArgumentParser parser = new ArgumentParser();
		Driver dri = new Driver();
		InvertedIndex index = new InvertedIndex();
		//initialize the Treemap
		elements = new TreeMap<>();
		counts = new TreeMap<>();
		//elements.put("Hello", new HashMap<>());
		//get the parsered args
		mapOfArgs = parser.parse(args);
		
		//get the path
		Path relativePath = dri.PathOfInput(mapOfArgs);
		
		//stemming lines and store elements in nested array
		if(relativePath != null) {
			index.ProcessFiles(relativePath, elements, counts);
		}
		
		System.out.println("size of returned elements " + elements.size());
		
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

	/*
	 * TODO: Delete this after reading...
	 *
	 * Generally, "driver" classes are responsible for setting up and calling other
	 * classes, usually from a main() method that parses command-line parameters. If
	 * the driver were only responsible for a single class, we use that class name.
	 * For example, "PizzaDriver" is what we would name a driver class that just
	 * sets up and calls the "Pizza" class.
	 */
}
