import java.io.IOException;

public class BotTest {
    public static void main(String[] args) throws IOException {
        FilmScrapper bot = new ImdbScrapper();
        bot.fetch(FilmScrapper.FilmGenre.ACTION, 2);
        
        for(Film film : bot.films) {
        	System.out.println(film.getTitle() + "\t" + film.getRating());
        }
    }
}