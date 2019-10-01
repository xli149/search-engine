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
	 * Declaration of invetedIndex object
	 */
	private final InvertedIndex index;

	/**
	 * @param index InvertedIndex object
	 */
	public InvertedIndexBuilder(InvertedIndex index) {

		this.index = index;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param filePath the path of a file
	 * @throws IOException if unable to read or parse file
	 */
	public void addStem(Path filePath) throws IOException {

		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){

			int posOfWord = 1;

			String line = null;

			while((line = reader.readLine()) != null) {

				String [] tokens = TextParser.parse(line);

				String stemmedWords;

				String path = filePath.toString();

				for (int i = 0; i < tokens.length; i++) {

					stemmedWords = stemmer.stem(tokens[i]).toString();

					index.add(stemmedWords, path, posOfWord);

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

		return Files.isRegularFile(path) && (name.endsWith(".txt") || name.endsWith(".text"));

	};

	//	public static final Supplier<Path> PATHS = (Path path) -> {
	//
	//		Files.walk(path,  FileVisitOption.FOLLOW_LINKS)
	//		.filter(IS_TEXT)
	//		.collect(Collectors.toCollection(ArrayList::new));
	//	}


	/**
	 * Recursively checking if a file descriptor points to a file or not
	 * pass it to parse if it is a file, otherwise get a list of sub-folders
	 * and call itself again
	 *
	 * @param start starting path of stream
	 * @throws IOException if the file is unable to read
	 * @throws NullPointerException if the path is null
	 */
	public void traversDirectory(Path start) throws IOException, NullPointerException {
		// TODO This is pretty useful... maybe make it a separate public static method like IS_TEXT?
		ArrayList<Path> paths = Files.walk(start,  FileVisitOption.FOLLOW_LINKS).filter(IS_TEXT).collect(Collectors.toCollection(ArrayList::new));

		Iterator<Path> itr = paths.iterator();

		while(itr.hasNext()) {

			addStem(itr.next());

		}

	}

}
