import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
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
	 * @throws IOException if file is unreachable
	 * @see SnowballStemmer
	 * @see #DEFAULT
	 */
	public static int wordStem(String line, int posOfWord, Path filePath, InvertedIndex elements)throws IOException {

		// TODO Inefficient, creates a new stemmer for every single line
		// TODO Remove this method and integrate directly into your wordStem method instead
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
	 * @throws IOException if the file is unreachable
	 */
	public static int wordStem(String line, Stemmer stemmer, int posOfWord, Path filePath, InvertedIndex elements) throws IOException{ // TODO Remove and integrate into wordStem

		String [] tokens = TextParser.parse(line);

		String stemmedWords;

		for (int i = 0; i < tokens.length; i++) {

			stemmedWords = stemmer.stem(tokens[i]).toString();

			elements.add(stemmedWords, filePath.toString(), posOfWord);

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
		// TODO Stemmer stemmer = ....
		try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){

			int posOfWord = 1;

			String line = null;
			
			String location = filePath.toString();

			while((line = reader.readLine()) != null) {

				posOfWord = wordStem(line, posOfWord, filePath, elements);

				/* TODO
				String [] tokens = TextParser.parse(line);
				
				loop through tokens and immediate add after stemming to the index
				*/
			}
		}
	}
	
	// TODO Choose which one you need between IS_TEXT and IS_TEXT_ATTR

	/**
	 * Lambda function that checks if a file ends with ".txt" or ".text"
	 */
	public static final Predicate<Path> IS_TEXT = (Path path) ->{

		String name = path.getFileName().toString().toLowerCase();

		if(Files.isRegularFile(path)

				&& (name.endsWith(".txt")

						|| name.endsWith(".text"))) {

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
	 * @throws NullPointerException if file does not exit
	 * @throws IOException if unable to write in the file
	 */
	public static void checkFile(Path relativePath, InvertedIndex elements) throws NullPointerException, IOException{

		if(relativePath.toFile().exists()) { // TODO Do not convert toFile().... Files.isReadable(...)

			traversDir(relativePath, elements);

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
		/*
		 * TODO This combines stream pipelines and functional programming with non-functional programming
		 * 
		 * (1) Create a method that is functional that just returns a list of paths. A separate method can be non-functional and loop through those paths and add to the index.
		 * 
		 * 
		 * (2) Either do not use any functional programming and instead of Files.find use a DirectoryStream. (There is lecture code that shows this.)
		 */
		Iterator<Path> locations =  Files.find(start, Integer.MAX_VALUE, IS_TEXT_ATTR, FileVisitOption.FOLLOW_LINKS).iterator();

		while(locations.hasNext()) {

			wordStem(locations.next(), elements);

		}

	}

}
