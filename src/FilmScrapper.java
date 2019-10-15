import java.io.IOException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.regex.*;

public abstract class FilmScrapper {
	public enum FilmGenre {
		ACTION,
		DRAMA,
		ANIMATION,
		ADVENTURE,
		WAR,
		FICTION,
		COMEDY,
		DOCUMENTARY,
		FANTASY,
		FAMILY,
		THRILLER,
		MUSICAL,
		ROMANCE,
		HORROR,
		MISTERY_CRIME,
		WESTERN
	}
	protected EnumMap<FilmGenre, String> genresMapping;
	protected ArrayList<Film> films;
	private Pattern ratingPattern;
	
	public FilmScrapper() {
		this.films = new ArrayList<Film>();
		this.ratingPattern = Pattern.compile("\\d{1,3}.?\\d*");
	}

	protected boolean isValidRating(String rating) {
		return ratingPattern.matcher(rating).matches();
	}
	
	/*
     * genre: Filter fetched films by genre
	 * filmCount: Fetch at least this number of films
	 * 
	 * Return:
	 *     int	Actual number of fetched films (Should be >= filmCount)
	 */
	public abstract int fetch(FilmGenre genre, int filmCount) throws IOException;
	
	/*
	 * n_films:	Number of films to be selected randomly
	 * 
	 * Return:
	 * 		ArrayList<Film>	Selected films based on true random numbers
	 */
	public ArrayList<Film> selectFilms(int n_films) throws IOException{
		ArrayList<Film> selected = new ArrayList<Film>();
		
		int [] randoms = random(n_films);
		
		for(int random : randoms) {
			selected.add(films.get(random));
		}
		
		return selected;
	}
	
	/*
	 * n_films: Number of randoms to be generated
	 * 
	 * Return:
	 * 		int []	Randomly generated numbers
	 */
	protected int [] random(int n_films) throws IOException {
		int [] n_generated = new int[n_films];
		
		for(int i = 0; i < n_films; i++) {
			n_generated[i] = TrueRandomGenerator.getInt(0, this.films.size());
		}
		return n_generated;
	}

	public ArrayList<Film> getFilms() {
		return films;
	}
}
