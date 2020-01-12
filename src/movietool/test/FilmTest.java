package movietool.test;

import org.junit.Before;
import org.junit.Test;
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
	public void setUp() throws IOException {
        ImdbScrapper bot = new ImdbScrapper();
        bot.fetch(FilmScrapper.FilmGenre.ACTION, 30);
        films = bot.selectFilms(20);
	}

	@Test
	public void sortTest(){
		Collections.sort(films);

		for(int i = 0; i < films.size() - 1; i++){
			assertTrue(films.get(i).getRating() >= films.get(i + 1).getRating());
		}
	}
}