import java.io.IOException;

public class BotTest {
    public static void main(String[] args) throws IOException {
        FilmScrapper.FilmGenre genre = FilmScrapper.FilmGenre.ACTION;
        int downloadCount = 100;
        int actualCount;
        FilmScrapper bot;

        System.out.println("Retrieving " + downloadCount + " films from IMDB...");
        bot = new ImdbScrapper();
        actualCount = bot.fetch(genre, downloadCount);
        System.out.println("> Got " + actualCount + " films");

        System.out.println("Retrieving " + downloadCount + " films from FilmAffinity...");
        bot = new FilmAffinityScrapper();
        actualCount = bot.fetch(genre, downloadCount);
        System.out.println("> Got " + actualCount + " films");

        System.out.println("Retrieving " + downloadCount + " films from TheMovieDB...");
        bot = new MoviedbScrapper();
        actualCount = bot.fetch(genre, downloadCount);
        System.out.println("> Got " + actualCount + " films");
        
        /*for(Film film : bot.films) {
        	System.out.println(film.getTitle() + "\t" + film.getRating());
        }*/
    }
}