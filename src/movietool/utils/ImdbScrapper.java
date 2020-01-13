package movietool.utils;

import java.io.IOException;

import java.util.EnumMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ImdbScrapper extends FilmScrapper {
	final int FILMS_PER_PAGE = 50;

	public String getProvider() { return "IMDB"; }
	
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

    public int fetch(FilmGenre genre, int filmCount) throws IOException {
        Document doc;
		Elements items;
		String url, title, rating;
    	
    	films.clear();

		for(int i = 0; i < filmCount; i += FILMS_PER_PAGE) {
			url = "https://www.imdb.com/search/title/?user_rating=1.0,&title_type=feature,tv_movie&genres=" + genresMapping.get(genre)
					+ "&start=" + (50*i + 1) + "&ref_=adv_nxt";
			doc = Jsoup.connect(url).get();
			items = doc.select("div.lister-item-content");
            
            for(Element item : items) {
				title = item.select("h3 > a").first().text();
				try{
					rating = item.select("div.ratings-imdb-rating").first().text();
				} catch(NullPointerException npe){
					rating = "";
				}

				if(title.isEmpty() || !isValidRating(rating))
                    System.out.println("(*) Warning: Invalid movie ['" + title + "', '" + rating + "']");
				else
					films.add(new Film(title, Double.parseDouble(rating)));
            }
		}

        return films.size();
    }
}