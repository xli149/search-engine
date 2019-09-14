import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Unity Class for parsing the file and store the word index and word count
 *
 * @author chrislee
 * @version Fall 2019
 *
 */
public class InvertedIndexBuilder{

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	// TODO The rest of these should be local variables within the methods they are needed
	/** The position of each word in a file starts at 1 */
	private static int posOfWord = 1;
	/** The total number of words in a file starts at 0*/
	private static int numOfWorlds = 0;
	/** The name of each file*/
	private static String fileName;

	private InvertedIndex addIndex;

	public InvertedIndexBuilder() {
		addIndex = new InvertedIndex();
	}
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
	 * @see #wordStem(String, Stemmer)
	 */

	public void wordStem(String line) {
		wordStem(line, new SnowballStemmer(DEFAULT));
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @param elements the collection of stemming the words and its position
	 *
	 * @see #wordStem(String, Stemmer)
	 */
	public void wordStem(String line, Stemmer stemmer) {

		String [] tokens = TextParser.parse(line);
		String stemmedWords;
		for(int i = 0; i < tokens.length; i++) {
			stemmedWords = stemmer.stem(tokens[i]).toString();
			addIndex.addIndex(stemmedWords, fileName, posOfWord);

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
	public void wordStem(Path inputFile) throws IOException {
		try(
				BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
				){
			fileName = inputFile.toString();
			String line = null;
			while((line = reader.readLine()) != null) {
				wordStem(line);

			}

			posOfWord = 1;
		}catch(IOException e) {
			System.out.print("Please check if the file exist");
		}
	}

	// TODO Add a blank line in between methods
	// TODO Make all the methods in this class static
	// TODO Better method names
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
	public void processFiles(Path relativePath) {
		try {
			// TODO Cannot use the file class at all
			// TODO Keep things as a Path object as long as possible, until just before adding to your index
			File file = relativePath.toFile();
			iterateFiles(file);
		}catch(NullPointerException | IOException e) {
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
	private void iterateFiles(File file) throws IOException{
		// TODO Going to have to do directory traversal without using File and listFiles()
		// TODO https://github.com/usf-cs212-fall2019/lectures/blob/master/Files%20and%20Exceptions/src/DirectoryStreamDemo.java
		if(file != null) {
			if(file.isDirectory()) {
				File[] files = file.listFiles();
				for(File f: files) {
					iterateFiles(f);
				}
			}
			else if(file.isFile()) {
				if(file.getName().toLowerCase().endsWith(".txt") || file.getName().toLowerCase().endsWith(".text")) {
					wordStem(file.toPath());
					if(numOfWorlds != 0) {
						//counts.put(fileName, numOfWorlds);
						addIndex.addWordCounts(fileName, numOfWorlds);
					}
					numOfWorlds = 0;


				}
			}

		}
	}

	public InvertedIndex getInvertedIndexObject() {
		return addIndex;
	}
}
