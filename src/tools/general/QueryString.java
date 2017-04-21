package tools.general;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author AJoan */
public class QueryString{

	/** TODO ajouter tous les nombreux accents possibles de la langue francaise (vive l'anglais) 
	 *  non words and non numbers (accented words will be also split )	 */
	public static String tfdfpattern="/[^\\d\\w]+/";
	/**
	 * (\s=((\t\n\r)space blank)
	 * semicolon
	 * space
	 * question mark 
	 * exclamation point)) 
	 * REPEATED AT LEAST ONCE OR NOT
	 * https://openclassrooms.com/courses/concevez-votre-site-web-avec-php-et-mysql/memento-des-expressions-regulieres	 */
	public static String mrpattern=tfdfpattern/*"/[\\s,.'?!]+/"*/; 	
	

	/**
	 * Return a set of words contained in a string (only one occurence of a word)
	 * @param string
	 * @param pattern
	 * @return */
	public static Set<String> wordSet(String string,String pattern) {
		return new HashSet<String>(
				Arrays.asList(string.toLowerCase().split(pattern)));}

	/**
	 * Return a list of words contained in a string (multiple occurence of a word is possible)
	 * @param string
	 * @param pattern
	 * @return */
	public static List<String> wordList(String string,String pattern) {
		return Arrays.asList(string.toLowerCase().split(pattern));}



}
