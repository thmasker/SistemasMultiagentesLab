import java.io.IOException;

import java.util.EnumMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ImdbScrapper extends FilmScrapper {
	final int FILMS_PER_PAGE = 50;
	
    @SuppressWarnings("serial")
	public ImdbScrapper() {
        super();

        genresMapping = new EnumMap<FilmGenre, String>(FilmGenre.class) {{
            put(FilmGenre.ACTION, "action");
            put(FilmGenre.DRAMA, "drama");
            put(FilmGenre.ANIMATION, "animation");
			put(FilmGenre.ADVENTURE, "adventure");
			put(FilmGenre.WAR, "war");
			put(FilmGenre.FICTION, "sci-fi");
			put(FilmGenre.COMEDY, "comedy");
			put(FilmGenre.DOCUMENTARY, "documentary");
			put(FilmGenre.FANTASY, "fantasy");
			put(FilmGenre.FAMILY, "family");
			put(FilmGenre.THRILLER, "thriller");
			put(FilmGenre.MUSICAL, "musical");
			put(FilmGenre.ROMANCE, "romance");
			put(FilmGenre.HORROR, "horror");
			put(FilmGenre.MISTERY_CRIME, "crime,mistery");
			put(FilmGenre.WESTERN, "western");
        }};
    }

    public void fetch(FilmGenre genre, int filmCount) throws IOException {
        Document doc;
		Elements items, titles, ratings;
		String url;
    	
    	films.clear();

        // Download and process HTML, fill 'films'		
		for(int i = 0; i < (filmCount / FILMS_PER_PAGE); i++) {
			url = "https://www.imdb.com/search/title/?user_rating=1.0,&genres=" + genresMapping.get(genre)
					+ "&start=" + (50*i + 1) + "&ref_=adv_nxt";
			doc = Jsoup.connect(url).get();
			
			items = doc.select("div.lister-item-content");
			titles = items.select("h3 > a");
			ratings = items.select("div.ratings-imdb-rating");
			
			for(int j = 0; j < titles.size(); j++) {
				films.add(new Film(titles.get(j).text(), Double.parseDouble(ratings.get(j).text())));
			}
		}
    }
}