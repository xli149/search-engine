import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Unity Class for parsing the file and store the word index and word count
 *
 * @author chrislee
 * @version Fall 2019
 *
 */
public class InvertedIndex{

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH; // TODO Keep

	// TODO The rest of these should be local variables within the methods they are needed
	/** The position of each word in a file starts at 1 */
	private static int posOfWord = 1;
	/** The total number of words in a file starts at 0*/
	private static int numOfWorlds = 0;
	/** The name of each file*/
	private static String fileName;

	/**
	 *
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param elements the collection of stemming the words and its position
	 *
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static void uniqueStems(String line, TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		uniqueStems(line, new SnowballStemmer(DEFAULT), elements);
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @param elements the collection of stemming the words and its position
	 *
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static void uniqueStems(String line, Stemmer stemmer, TreeMap<String, TreeMap<String, TreeSet<Integer>>>elements) {
		// TODO int posOfWord = 1;

		String [] s = TextParser.parse(line); // TODO Avoid 1 letter variable names, String[] tokens = TextParser.parse(...)
		String stemmedWords;
		for(int i = 0; i < s.length; i++) {
			stemmedWords = stemmer.stem(s[i]).toString();
			// TODO A lot of the logic below belongs in an inverted inded add(...) method.
			 if(!elements.containsKey(stemmedWords.toLowerCase())) {
				 TreeMap<String, TreeSet<Integer>> newPair = new TreeMap<>();
				 TreeSet<Integer> pos = new TreeSet<>();
				 pos.add(posOfWord);
				 newPair.put(fileName, pos);
				 elements.put(stemmedWords.toLowerCase(), newPair);
			 }
			 else  {
				 if(!elements.get(stemmedWords.toLowerCase()).containsKey(fileName)) {
					 TreeSet<Integer> pos = new TreeSet<>();
					 pos.add(posOfWord);
					 elements.get(stemmedWords.toLowerCase()).put(fileName, pos);
				 }
				 else {
					 elements.get(stemmedWords.toLowerCase()).get(fileName).add(posOfWord);
				 }
			 }
			 numOfWorlds++;
			 posOfWord++;
		}
	}

	// TODO Usually have a space after all key words, e.g. "for (...)" and after curly braces like "} catch (IOException...)"

	// TODO public static void uniqueStems(Path inputFile,  InvertedIndex elements) throws IOException {
	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @param elements the collection of stemming the words and its position
	 * @throws IOException if unable to read or parse file
	 */
	public static void uniqueStems(Path inputFile,  TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) throws IOException {
		try(

				BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
		){
			fileName = inputFile.toString();
			String line = null;
			while((line = reader.readLine()) != null) {
				uniqueStems(line, elements);

			}

			posOfWord = 1;
		}catch(IOException e) {
			System.out.print("Please check if the file exist");
		}
	}

	// TODO Add a blank line in between methods
	// TODO Make all the methods in this class static
	// TODO Better method names


		// TODO Change to processFiles(...)

	/**
	 * Convert a path to a file descriptor and pass it to a reading file function
	 *
	 * @param relativePath the relative path of a file
	 * @param elements an empty treeMap for storing word index
	 * @param counts an empty treeMap for storing number of words in each file
	 * @throws FileNotFoundException if the file is not found
	 * @throws IOException if it is unable to read the file
	 * @throws NullPointerException if the file descriptor is null
	 *
	 *  @see #iterateFiles()
	 */
	public void ProcessFiles(Path relativePath, TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, TreeMap<String, Integer> counts) throws FileNotFoundException, IOException{
		try {
			// TODO Cannot use the file class at all
			// TODO Keep things as a Path object as long as possible, until just before adding to your index
				File file = relativePath.toFile();
				iterateFiles(file, elements, counts);
		}catch(NullPointerException e) {
			System.out.println("The path is not valid");
		}
	}

	/**
	 * Recursively checking if a file descriptor points to a file or not
	 * pass it to parse if it is a file, otherwise get a list of sub-folders
	 * and call itself again
	 *
	 * @param file descriptor of a file
	 * @param elements an empty treeMap to store word index
	 * @param counts an empty treeMap to store number of words in a file
	 * @throws IOException if the file is unable to read
	 */
	private void iterateFiles(File file, TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements,  TreeMap<String, Integer> counts) throws IOException{
		// TODO Going to have to do directory traversal without using File and listFiles()
		// TODO https://github.com/usf-cs212-fall2019/lectures/blob/master/Files%20and%20Exceptions/src/DirectoryStreamDemo.java
		if(file != null) {
			if(file.isDirectory()) {
				File[] files = file.listFiles();
				for(File f: files) {
					iterateFiles(f, elements, counts);
				}
			}
			else if(file.isFile()) {
				if(file.getName().toLowerCase().endsWith(".txt") || file.getName().toLowerCase().endsWith(".text")) {
					uniqueStems(file.toPath(), elements);
					if(numOfWorlds != 0) {
						counts.put(fileName, numOfWorlds);
					}
					numOfWorlds = 0;
				}
			}

		}
	}
}
