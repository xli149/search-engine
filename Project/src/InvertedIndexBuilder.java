import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

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

	/**
	 * Declaration of an object of InvertedIndex type
	 */
	private static InvertedIndex addIndex;

	/**
	 * Constructor
	 */
	public InvertedIndexBuilder() {
		addIndex = new InvertedIndex();
	}
	/**
	 *
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param posOfWord
	 * @param filePath
	 * @return position of a stemmed word
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 */

	public static int wordStem(String line, int posOfWord, Path filePath) {
		return wordStem(line, new SnowballStemmer(DEFAULT), posOfWord, filePath);
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @param posOfWord the position of a word
	 * @param filePath the path of a file
	 * @return the position of a word
	 */
	public static int wordStem(String line, Stemmer stemmer, int posOfWord, Path filePath) {
		String [] tokens = TextParser.parse(line);
		String stemmedWords;
		for (int i = 0; i < tokens.length; i++) {
			stemmedWords = stemmer.stem(tokens[i]).toString();
			addIndex.addIndex(stemmedWords, filePath.toString(), posOfWord);
			posOfWord++;
		}
		return posOfWord;
	}

	// TODO public static void uniqueStems(Path inputFile,  InvertedIndex elements) throws IOException {
	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param filePath the path of a file
	 * @throws IOException if unable to read or parse file
	 */
	public static void wordStem(Path filePath) throws IOException {
		int posOfWord = 1;
		String line = null;
		try(
				BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
				){

			while((line = reader.readLine()) != null) {

				posOfWord = wordStem(line, posOfWord, filePath);


			}
			if(posOfWord != 1) {
				addIndex.addWordCounts(filePath.toString(), posOfWord - 1);
			}
		} catch(IOException e) {
			System.out.print("Please check if the file exist");
		}
	}

	// TODO Make all the methods in this class static
	// TODO Better method names

	/**
	 * Lambda function that checks if a file ends with ".txt" or ".text"
	 */
	public static final Predicate<Path> IS_TEXT = (Path path) ->{
		if(Files.isRegularFile(path)
				&& (path.getFileName().toString().toLowerCase().endsWith(".txt")
						|| path.getFileName().toString().toLowerCase().endsWith(".text")))
			return true;
		return false;
	};
	/**
	 * Lambda function for setting attribute
	 */
	public static final BiPredicate<Path, BasicFileAttributes> IS_TEXT_ATTR = (path, attr) -> IS_TEXT.test(path);

	/**
	 * Function for checking the existence of a file and pass it to get parsed
	 * @param relativePath the relative path of a file
	 */
	public static void checkFile(Path relativePath) {
		try {
			if(relativePath.toFile().exists()) {
				traversDir(relativePath);
			}

		} catch(NullPointerException | IOException e) {
			System.out.println("The path is not valid");
		}
	}


	/**
	 * Recursively checking if a file descriptor points to a file or not
	 * pass it to parse if it is a file, otherwise get a list of sub-folders
	 * and call itself again
	 *
	 * @param start starting path of stream
	 *
	 * @throws IOException if the file is unable to read
	 */
	private static void traversDir(Path start) throws IOException {

		Files.find(start, Integer.MAX_VALUE, IS_TEXT_ATTR).forEach((Path path)->{
			try {
				wordStem(path);
			} catch (IOException e) {
				System.out.println("Input output fails");
			}

		});
	}

	/**
	 * @return an object of InvertedIndex type
	 */
	public InvertedIndex getInvertedIndexObject() {
		return addIndex;
	}
}
