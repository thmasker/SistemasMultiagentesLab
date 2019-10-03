
import java.io.IOException;

public class BotTest {
    public static void main(String[] args) throws IOException {
        FilmScrapper bot = new ImdbScrapper();
        bot.fetch(FilmScrapper.FilmGenre.ACTION, 100);
    }
}