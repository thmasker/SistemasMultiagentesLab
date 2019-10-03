import java.io.IOException;

import java.util.EnumMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MoviedbScrapper extends FilmScrapper {
	final int FILMS_PER_PAGE = 20;
	
	@SuppressWarnings("serial")
	public MoviedbScrapper() {
		super();
		
		genresMapping = new EnumMap<FilmGenre, String>(FilmGenre.class){{
			put(FilmGenre.ACTION, "&with_genres%5B%5D=28");
			put(FilmGenre.ANIMATION, "&with_genres%5B%5D=16");
			put(FilmGenre.ADVENTURE, "&with_genres%5B%5D=12");
			put(FilmGenre.WAR, "&with_genres%5B%5D=10752");
			put(FilmGenre.FICTION, "&with_genres%5B%5D=878");
			put(FilmGenre.COMEDY, "&with_genres%5B%5D=35");
			put(FilmGenre.DOCUMENTARY, "&with_genres%5B%5D=99");
			put(FilmGenre.DRAMA, "&with_genres%5B%5D=18");
			put(FilmGenre.FANTASY, "&with_genres%5B%5D=14");
			put(FilmGenre.FAMILY, "&with_genres%5B%5D=10751");
			put(FilmGenre.THRILLER, "&with_genres%5B%5D=80&with_genres%5B%5D=9648&with_genres%5B%5D=53");
			put(FilmGenre.MUSICAL, "&with_genres%5B%5D=10402");
			put(FilmGenre.ROMANCE, "&with_genres%5B%5D=10749");
			put(FilmGenre.HORROR, "&with_genres%5B%5D=27");
			put(FilmGenre.MISTERY_CRIME, "&with_genres%5B%5D=80&with_genres%5B%5D=9648&with_genres%5B%5D=53");
			put(FilmGenre.WESTERN, "&with_genres%5B%5D=37");
		}};
	}

	public int fetch(FilmGenre genre, int filmCount) throws IOException {
		Document doc;
		Elements items;
		String url, title, rating;
		int count = 0;
		
		films.clear();
		
		for(int i = 0; i < (filmCount / FILMS_PER_PAGE + 1); i++) {	// pages + 1 porque la primera iteraci�n no coge informaci�n sobre pel�culas
			url = "https://www.themoviedb.org/discover/movie?language=es&list_style=1&media_type=movie&page="
					+ i + "&primary_release_year=0&sort_by=popularity.desc&vote_count.gte=0" + genresMapping.get(genre);
			doc = Jsoup.connect(url).get();
			items = doc.select("div.info");

			for(Element item : items) {
				title = items.select("div.flex a").first().attr("title");
				rating = item.select("div.user_score_chart").first().attr("data-percent");

				films.add(new Film(title, Double.parseDouble(rating) / 10.0));
				count++;
			}
		}
		return count;
	}
}
