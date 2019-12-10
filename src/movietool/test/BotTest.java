package movietool.test;

import java.io.IOException;

import java.util.ArrayList;

import movietool.utils.Film;
import movietool.utils.FilmAffinityScrapper;
import movietool.utils.FilmScrapper;
import movietool.utils.ImdbScrapper;
import movietool.utils.MoviedbScrapper;

public class BotTest {
    public static void main(String[] args) throws IOException {
        FilmScrapper.FilmGenre genre = FilmScrapper.FilmGenre.ACTION;
        int downloadCount = 100;
        int actualCount;
        FilmScrapper bot;

//        for(int i = 0; i < 10; i++) {
//            System.out.println("Random num: " + TrueRandomGenerator.getInt(0,100));
//        }

        System.out.println("Retrieving " + downloadCount + " films from IMDB...");
        bot = new ImdbScrapper();
        actualCount = bot.fetch(genre, downloadCount);
        System.out.println("> Got " + actualCount + " films");
        
        ArrayList<Film> selectedIMDB = bot.selectFilms(5);
        System.out.println("> Randomly selected " + selectedIMDB.size() + " films");
        
        for(Film film : selectedIMDB)
        	System.out.println(film.getRating() + "\t" + film.getTitle());



        System.out.println("Retrieving " + downloadCount + " films from FilmAffinity...");
        bot = new FilmAffinityScrapper();
        actualCount = bot.fetch(genre, downloadCount);
        System.out.println("> Got " + actualCount + " films");
        
        ArrayList<Film> selectedAff = bot.selectFilms(5);
        System.out.println("> Randomly selected " + selectedAff.size() + " films");
        
        for(Film film : selectedAff)
        	System.out.println(film.getRating() + "\t" + film.getTitle());



        System.out.println("Retrieving " + downloadCount + " films from TheMovieDB...");
        bot = new MoviedbScrapper();
        actualCount = bot.fetch(genre, downloadCount);
        System.out.println("> Got " + actualCount + " films");
        
        ArrayList<Film> selectedMDB = bot.selectFilms(5);
        System.out.println("> Randomly selected " + selectedMDB.size() + " films");
        
        for(Film film : selectedMDB)
        	System.out.println(film.getRating() + "\t" + film.getTitle());
    }
}