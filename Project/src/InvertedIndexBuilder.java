import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/*
 * TODO This is great. The approach with only static methods works great for projects 1 and 2.
 * 
 * However, it does make multithreading later on in project 3 a little more complicated. You can keep
 * this class as is if you want. Or, switch it to this style instead:
 * 
 * private final InvertedIndex index;
 * 
 * public InvertedIndexBuilder(InvertedIndex index) {
 * 		this.index = index;
 * }
 * 
 * public void addStems(Path filePath) throws IOException {
 * 		same code as before, but adding to the index instance member instead of an index method parameter
 * }
 * 
 * public void traverseDirectory(Path start)... same idea same as before but non-static and using the index member
 */

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

	// TODO Needs a better method name. Maybe addStems(...) instead? Use the refactor feature in Eclipse to change the name!
	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param filePath the path of a file
	 * @param elements elements an object of InvertedIndex type
	 * @throws IOException if unable to read or parse file
	 */
	public static void wordStem(Path filePath, InvertedIndex elements) throws IOException {

		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){

			int posOfWord = 1;

			String line = null;

			while((line = reader.readLine()) != null) {

				String [] tokens = TextParser.parse(line);

				String stemmedWords;

				for (int i = 0; i < tokens.length; i++) {

					stemmedWords = stemmer.stem(tokens[i]).toString();

					// TODO Calling filePath.toString() over and over and over again. Save it in a variable before the while loop, and use that variable inside the for loop.
					elements.add(stemmedWords, filePath.toString(), posOfWord);

					posOfWord++;

				}
			}
		}
	}

	/**
	 * Lambda function that checks if a file ends with ".txt" or ".text"
	 */
	public static final Predicate<Path> IS_TEXT = (Path path) -> {

		String name = path.getFileName().toString().toLowerCase();

		// TODO Can condense if/else blocks that just return true or false into one line:
		// TODO return Files.isRegularFile(path) && (name.endsWith(".txt") || name.endsWith(".text"));
		if(Files.isRegularFile(path)

				&& (name.endsWith(".txt")

						|| name.endsWith(".text"))) {

			return true;

		}

		return false;
	};

	/**
	 * Function for checking the existence of a file and pass it to get parsed
	 * @param path the relative path of a file
	 * @param elements elements an object of InvertedIndex type
	 * @throws NullPointerException if file does not exit
	 * @throws IOException if unable to write in the file
	 */
	public static void checkFile(Path path, InvertedIndex elements) throws NullPointerException, IOException{

		if(Files.isReadable(path)) { // TODO I would remove this check. If the file is not readable, an exception should happen. Instead, nothing happens.
			// TODO Actually I recommend removing this method entirely, and making traverseDir public instead of private.

			traversDir(path, elements);

		}

	}

	// TODO Fix the method name here to traverseDirectory(...) instead.
	// TODO I see one warning for this method: "Javadoc: Missing tag for declared exception NullPointerException	InvertedIndexBuilder.java	/Project/src	line 112	Java Problem"
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
	private static void traversDir(Path start, InvertedIndex elements) throws IOException, NullPointerException {
		// TODO This is pretty useful... maybe make it a separate public static method like IS_TEXT?
		ArrayList<Path> paths = Files.walk(start,  FileVisitOption.FOLLOW_LINKS).filter(IS_TEXT).collect(Collectors.toCollection(ArrayList::new));

		Iterator<Path> itr = paths.iterator();

		while(itr.hasNext()) {

			wordStem(itr.next(), elements);

		}

	}

}
