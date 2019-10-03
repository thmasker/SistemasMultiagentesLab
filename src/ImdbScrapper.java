
import java.io.IOException;

import org.jsoup.select.Elements;
import java.util.EnumMap;

public class ImdbScrapper extends FilmScrapper {

    public ImdbScrapper() {
        super();

        genresMapping = new EnumMap<FilmGenre, String>(FilmGenre.class) {{
            put(FilmGenre.ACTION, "action");
            put(FilmGenre.DRAMA, "drama");
        }};
    }

    public void fetch(FilmGenre genre, int pages) throws IOException {
        films.clear();

        // Download and process HTML, fill 'films'
    }
}