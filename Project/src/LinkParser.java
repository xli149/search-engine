import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses URL links from the anchor tags within HTML text.
 */
public class LinkParser {

	/**
	 * Regular expression for parsing the links
	 */
	private final static String regex = "(?is)<a[^\\>]*href\\s*=\\s*\\\\*\\\"*(.+?)\\\"";

	/**
	 * Function for cleaning the url
	 * @param url the url to be cleaned
	 * @return the cleaned url
	 */
	public static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(),
					url.getPort(), url.getPath(), url.getQuery(), null).toURL();
		}
		catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of the
	 * anchor tags in the provided HTML. The links will be converted to absolute
	 * using the base URL and cleaned (removing fragments and encoding special
	 * characters as necessary).
	 *
	 * @param base the base url used to convert relative links to absolute3
	 * @param html the raw html associated with the base url
	 * @return cleaned list of all http(s) links in the order they were found
	 */
	public static ArrayList<URL> listLinks(URL base, String html) {

		ArrayList<URL> links = new ArrayList<>();

		Pattern pattern = Pattern.compile(regex);

		if(html == null) {

			return links;

		}

		Matcher matcher =  pattern.matcher(html);

		while(matcher.find()) {

			try {

				URL newUrl = clean(new URL(base, matcher.group(1)));

				links.add(newUrl);

			} catch (MalformedURLException e) {

				System.out.println("Url formmated wrong");
			}

		}

		return links;
	}

	/**
	 * Demonstrates this class.
	 * @param args unused
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// this demonstrates cleaning
		URL valid = new URL("https://docs.python.org/3/library/functions.html?highlight=string#format");
		System.out.println(" Link: " + valid);
		System.out.println("Clean: " + clean(valid));
		System.out.println();

		// this demonstrates encoding
		URL space = new URL("https://www.google.com/search?q=hello world");
		System.out.println(" Link: " + space);
		System.out.println("Clean: " + clean(space));
		System.out.println();

		// this throws an exception
		URL invalid = new URL("javascript:alert('Hello!');");
		System.out.println(invalid);
	}
}