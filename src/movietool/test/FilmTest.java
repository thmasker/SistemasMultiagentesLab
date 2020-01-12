package movietool.test;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import movietool.utils.Film;
import movietool.utils.FilmScrapper;
import movietool.utils.ImdbScrapper;

public class FilmTest {
	private ArrayList<Film> films;

	@Before
	@SuppressWarnings("serial")
	public void setUp(){
		films = new ArrayList<Film>(){
			{
				add(new Film("Checking", 9.9));
			}
		};
	}

	@Test
	public void sortTest() throws IOException {
		ImdbScrapper bot = new ImdbScrapper();
        bot.fetch(FilmScrapper.FilmGenre.ACTION, 30);
		ArrayList<Film> movies = bot.selectFilms(20);
		
		Collections.sort(movies);

		for(int i = 0; i < movies.size() - 1; i++){
			assertTrue(movies.get(i).getRating() >= movies.get(i + 1).getRating());
		}
	}

	@Test
	public void equalsTrue(){
		Film f = new Film("Checking", 5);
		assertTrue(films.contains(f));
	}

	@Test
	public void equalsFalse(){
		Film f = new Film("Not this time", 9.9);
		assertFalse(films.contains(f));
	}
}