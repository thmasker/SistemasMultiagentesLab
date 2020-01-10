package movietool.utils;

import javax.naming.NameNotFoundException;

public class FilmScrapperFactory {
	public static FilmScrapper createFilmScrapper(String scrapper) throws NameNotFoundException {
		if(scrapper.equals("FilmAffinity")){
			return new FilmAffinityScrapper();
		} else if(scrapper.equals("IMDB")){
			return new ImdbScrapper();
		} else if(scrapper.equals("MovieDB")){
			return new MoviedbScrapper();
		} else {
			throw new NameNotFoundException("Requested WEBSITE is not available.");
		}
	}
}