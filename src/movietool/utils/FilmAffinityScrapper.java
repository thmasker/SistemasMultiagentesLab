package movietool.utils;

import java.io.IOException;

import java.util.EnumMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FilmAffinityScrapper extends FilmScrapper {
	final int FILMS_PER_PAGE = 30;
	
    @SuppressWarnings("serial")
	public FilmAffinityScrapper() {
        super();

        genresMapping = new EnumMap<FilmGenre, String>(FilmGenre.class) {{
            put(FilmGenre.ACTION, "AC");
            put(FilmGenre.DRAMA, "DR");
            put(FilmGenre.ANIMATION, "AN");
			put(FilmGenre.ADVENTURE, "AV");
			put(FilmGenre.WAR, "BE");
			put(FilmGenre.FICTION, "C-F");
			put(FilmGenre.COMEDY, "CO");
			put(FilmGenre.DOCUMENTARY, "DO");
			put(FilmGenre.FANTASY, "FAN");
			put(FilmGenre.FAMILY, "INF");
			put(FilmGenre.THRILLER, "TH");
			put(FilmGenre.MUSICAL, "MU");
			put(FilmGenre.ROMANCE, "RO");
			put(FilmGenre.HORROR, "TE");
			put(FilmGenre.MISTERY_CRIME, "INT");
			put(FilmGenre.WESTERN, "WE");
        }};
    }

    public int fetch(FilmGenre genre, int filmCount) throws IOException {
        Document doc;
		Elements items;
        // TODO: Filter only films
		String url = "https://www.filmaffinity.com/es/topgen.php?genre="  + genresMapping.get(genre) + "&fromyear=&toyear=&country=&nodoc";
    	String title, rating;

        films.clear();

		for(int i = 0; i < filmCount; i += FILMS_PER_PAGE) {
			doc = Jsoup.connect(url).data("from", "" + i).userAgent("Mozilla").post();
            items = doc.select("li > ul");

            for(Element item : items) {
                title = item.select("div.mc-title a").first().text();
                rating = item.select("li.data > div.avg-rating").first().text().replace(',', '.');

                if(title.isEmpty() || !isValidRating(rating))
                    System.out.println("(*) Warning: Invalid movie ['" + title + "', '" + rating + "']");
				else
					films.add(new Film(title, Double.parseDouble(rating)));
            }
		}
        return films.size();
    }
}