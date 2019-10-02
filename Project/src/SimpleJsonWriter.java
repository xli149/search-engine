import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where
 * newlines are used to separate elements and nested elements are indented.
 * test..
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2019
 */
public class SimpleJsonWriter {

	private static void queryPrinter(Map.Entry<String, String> entry, Writer writer, int level) throws IOException {



		quote(entry.getKey(), writer, level);

		writer.write(": ");

		if(entry.getKey().equals("where")) {

			quote(entry.getValue().toString(), writer, 0);

		}
		else {
			writer.write(entry.getValue().toString());

		}
	}

	private static void queryUtility(LinkedHashMap<String, String> elements, Writer writer, int level) throws IOException {

		var iterator = elements.entrySet().iterator();

		indent("{", writer, level);

		if(iterator.hasNext()) {

			writer.write("\n");

			var entry = iterator.next();

			queryPrinter(entry, writer, level + 1);

		}

		while(iterator.hasNext()) {

			writer.write(",");

			writer.write("\n");

			var entry = iterator.next();

			queryPrinter(entry, writer, level + 1);


		}

		writer.write("\n");

		indent("}", writer, level);

	}

	private static void writeEntryThrid(ArrayList<LinkedHashMap<String, String>> elements, Writer writer, int level) throws IOException {

		writer.write("[");

		var iterator = elements.iterator();

		if(iterator.hasNext()) {

			writer.write("\n");

			var element = iterator.next();

			queryUtility(element, writer, level);



		}
		while(iterator.hasNext()) {

			writer.write(",");

			writer.write("\n");

			var element = iterator.next();

			queryUtility(element, writer, level);

		}

		//TODO find where caused this
		writer.write("\n");

		indent("]", writer, level - 1);

	}



	private static void asQueryObject(Map.Entry<String, ArrayList<LinkedHashMap<String, String>>> entry, Writer writer, int level) throws IOException{

		quote(entry.getKey(), writer, level);

		writer.write(": ");

		var elements = entry.getValue();


		writeEntryThrid(elements, writer, level + 1);




	}
	private static void asNestedQueryObject(TreeMap<String, ArrayList<LinkedHashMap<String, String>>> queries, Writer writer, int level) throws IOException{

		var iterator = queries.entrySet().iterator();

		writer.write("{");

		if(iterator.hasNext()) {

			writer.write("\n");

			var entry = iterator.next();

			asQueryObject(entry, writer, level + 1);


		}

		while(iterator.hasNext()) {

			writer.write(",");

			writer.write("\n");

			var entry = iterator.next();

			asQueryObject(entry, writer, level + 1);

		}

		writer.write("\n");

		writer.write("}");

	}

	public static void asNestedQueryObject(Path path,  TreeMap<String, ArrayList<LinkedHashMap<String, String>>> elements) throws IOException {

		try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){

			asNestedQueryObject(elements, writer, 0);

		}

	}

	/**
	 * Utility function for reducing repeated code
	 * @param entry entry set of String, Integer pair
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if the file is unable to write or read
	 */
	private static void writeEntry(Map.Entry<String, Integer> entry, Writer writer, int level) throws IOException{

		quote(entry.getKey(), writer, level);

		writer.write(": ");

		writer.write(entry.getValue().toString());
	}

	/**
	 * Writes the word count of each file as a pretty JSON object to a file
	 * @param counts the number of words of each file
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if the file is unable to write or read
	 */
	public static void wordsCountsPrinter(TreeMap<String, Integer> counts, Writer writer, int level) throws IOException{

		writer.write("{");

		var iterator = counts.entrySet().iterator();

		if(iterator.hasNext()) {

			writer.write("\n");

			var element  = iterator.next();

			writeEntry(element, writer, level + 1);

		}

		while(iterator.hasNext()) {

			writer.write(",");

			writer.write("\n");

			var element = iterator.next();

			writeEntry(element, writer, level + 1);

		}
		writer.write("\n}");
	}

	/**
	 * Writes the word count of each file as a pretty JSON object to a file
	 * @param counts the number of words of each file
	 * @param path 	 the path of a file
	 * @throws IOException if the file is unable to write or read
	 */
	public static void wordCountsPrinter(TreeMap<String, Integer> counts, Path path) throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

			wordsCountsPrinter(counts, writer,0);

		}
	}
	/**
	 * Utility function for reducing repeated code
	 * @param entry entry set of String, Integer pair
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if the file is unable to write or read
	 */
	private static void writeEntrySecond(Map.Entry<String, TreeSet<Integer>> entry, Writer writer, int level) throws IOException{

		quote(entry.getKey(), writer, level);

		writer.write(": ");

		asSet(entry.getValue(), writer, level + 1);

	}

	/**
	 * Utility function for reducing repeated code
	 * @param entry entry set of String, Integer pair
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if the file is unable to write or read
	 */
	private static void writeEntryFirst(Map.Entry<String,TreeMap<String,TreeSet<Integer>>> entry, Writer writer, int level) throws IOException{

		quote(entry.getKey(), writer, level);

		writer.write(": ");

		var elements = entry.getValue();

		asObject(elements, writer, level + 1);

	}

	/**
	 *  Writes index of each stemmed word in JSON object
	 * @param elements a collection of Integers
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if the file is unable to write or read
	 */
	public static void asSet(TreeSet<Integer> elements, Writer writer, int level)throws IOException{

		writer.write("[");

		var iterator = elements.iterator();

		if(iterator.hasNext()) {

			writer.write("\n");

			indent(iterator.next(), writer, level);

		}

		while(iterator.hasNext()) {

			writer.write(",");

			writer.write("\n");

			indent(iterator.next(), writer, level);

		}

		indent("\n]", writer, level);

	}

	/**
	 *  Writes file paths of each file in JSON object
	 * @param elements a TreeMap of String and TreeSet pairs
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if the file is unable to write or read
	 */
	public static void  asObject(TreeMap<String, TreeSet<Integer>> elements, Writer writer, int level)throws IOException {

		writer.write("{");

		var iterator = elements.entrySet().iterator();

		if(iterator.hasNext()) {

			writer.write("\n");

			var element = iterator.next();

			writeEntrySecond(element, writer, level);

		}

		while(iterator.hasNext()) {

			writer.write(",");

			writer.write("\n");

			var element = iterator.next();

			writeEntrySecond(element, writer, level);

		}

		indent("\n}", writer, level);

	}


	/**
	 * Writes stemmed words as a nested pretty JSON object. The generic notation used
	 * allows this method to be used for any type of map with any type of nested
	 * collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if the file is unable to read or write
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer, int level) throws IOException {

		writer.write("{");

		var iterator = elements.entrySet().iterator();

		if(iterator.hasNext()) {

			writer.write("\n");

			var element = iterator.next();

			writeEntryFirst(element, writer, level + 1);

		}

		while(iterator.hasNext()) {

			writer.write(",");

			writer.write("\n");

			var element = iterator.next();

			writeEntryFirst(element, writer, level + 1);

		}

		writer.write("\n}");
	}

	/**
	 * Writes the elements as a nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path) throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

			asNestedObject(elements, writer, 0);

		}

	}
	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */
	public static void indent(Writer writer, int times) throws IOException {

		for (int i = 0; i < times; i++) {

			writer.write('\t');

		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {

		indent(element.toString(), writer, times);

	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {

		indent(writer, times);

		writer.write(element);

	}

	/**
	 * Writes the element surrounded by {@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */
	public static void quote(String element, Writer writer) throws IOException {

		writer.write('"');

		writer.write(element);

		writer.write('"');

	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {

		indent(writer, times);

		quote(element, writer);

	}


}
