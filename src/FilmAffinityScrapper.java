import java.io.IOException;

import java.util.EnumMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    public void fetch(FilmGenre genre, int filmCount) throws IOException {
        Document doc;
		Elements items, titles, ratings;
        // TODO: Filter only films
		String url = "https://www.filmaffinity.com/es/topgen.php?genre="  + genresMapping.get(genre) + "&fromyear=&toyear=&country=&nodoc";
    	
    	films.clear();

		for(int i = 0; i < filmCount; i += FILMS_PER_PAGE) {
			doc = Jsoup.connect(url).data("from", "" + i).userAgent("Mozilla").post();
            items = doc.select("li > ul");
            
            for(int j = 0; j < items.size(); j++) {
                String title = items.get(j).select("div.mc-title a").first().text();
                String rating = items.get(j).select("li.data > div.avg-rating").first().text().replace(',', '.');
                // TODO: Check both values retrieved properly

                // TODO: Check parseDouble exception
                films.add(new Film(title, Double.parseDouble(rating)));
            }
		}
    }
}