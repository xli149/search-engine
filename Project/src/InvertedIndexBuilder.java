import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
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
	 *
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param posOfWord the position of a stemmed word
	 * @param filePath the path of a file
	 * @param elements elements an object of InvertedIndex type
	 * @return position of a stemmed word
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 */
	public static int wordStem(String line, int posOfWord, Path filePath, InvertedIndex elements) {
		return wordStem(line, new SnowballStemmer(DEFAULT), posOfWord, filePath, elements);
	}

	/**
	 * Returns a set of unique (no duplicates) cleaned and stemmed words parsed
	 * from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @param posOfWord the position of a word
	 * @param filePath the path of a file
	 * @param elements elements an object of InvertedIndex type
	 * @return the position of a word
	 */
	public static int wordStem(String line, Stemmer stemmer, int posOfWord, Path filePath, InvertedIndex elements) {
		String [] tokens = TextParser.parse(line);
		String stemmedWords;
		for (int i = 0; i < tokens.length; i++) {
			stemmedWords = stemmer.stem(tokens[i]).toString();
			elements.addIndex(stemmedWords, filePath.toString(), posOfWord);
			posOfWord++;
		}
		return posOfWord;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param filePath the path of a file
	 * @param elements elements an object of InvertedIndex type
	 * @throws IOException if unable to read or parse file
	 */
	public static void wordStem(Path filePath, InvertedIndex elements) throws IOException {
		int posOfWord = 1;
		String line = null;
		try(
				BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
				){

			while((line = reader.readLine()) != null) {

				posOfWord = wordStem(line, posOfWord, filePath, elements);


			}
			if(posOfWord != 1) {
				elements.addWordCounts(filePath.toString(), posOfWord - 1);
			}
		} catch(IOException e) {
			System.out.print("Please check if the file exist");
		}
	}

	/**
	 * Lambda function that checks if a file ends with ".txt" or ".text"
	 */
	public static final Predicate<Path> IS_TEXT = (Path path) ->{
		// TODO Safe toString.toLowercase and reuse instead of calling twice
		if(Files.isRegularFile(path)
				&& (path.getFileName().toString().toLowerCase().endsWith(".txt")
						|| path.getFileName().toString().toLowerCase().endsWith(".text"))) {
			return true;
		}
		return false;
	};
	/**
	 * Lambda function for setting attribute
	 */
	public static final BiPredicate<Path, BasicFileAttributes> IS_TEXT_ATTR = (path, attr) -> IS_TEXT.test(path);

	/**
	 * Function for checking the existence of a file and pass it to get parsed
	 * @param relativePath the relative path of a file
	 * @param elements elements an object of InvertedIndex type
	 */
	public static void checkFile(Path relativePath, InvertedIndex elements) {
		try {
			if(relativePath.toFile().exists()) {
				traversDir(relativePath, elements);
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
	 * @param elements an object of InvertedIndex type
	 *
	 * @throws IOException if the file is unable to read
	 */
	private static void traversDir(Path start, InvertedIndex elements) throws IOException {

		Files.find(start, Integer.MAX_VALUE, IS_TEXT_ATTR, FileVisitOption.FOLLOW_LINKS).forEach((Path path)->{
			try {
				wordStem(path, elements);
			} catch (IOException e) {
				System.out.println("Input output fails");
			}

		});
	}

}
