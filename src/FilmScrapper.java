import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;

public abstract class FilmScrapper {
	public enum FilmGenre {
		ACTION,
		DRAMA
		//...
	}
	protected EnumMap<FilmGenre, String> genresMapping;
	protected ArrayList<Film> films;
	
	public FilmScrapper() {
		this.films = new ArrayList<Film>();
	}
	
	public abstract void fetch(FilmGenre genre, int filmCount) throws IOException;
	
	public ArrayList<Film> getFilms() {
		return films;
	}
}
