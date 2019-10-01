import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Bot {	
	@SuppressWarnings("serial")
	private Map<String, String> genFILM = new HashMap<String, String>(){
		{
			put("acción", "AC");
			put("animación", "AN");
			put("aventuras", "AV");
			put("bélico", "BE");
			put("ficción", "C-F");
//			put("negro", "F-N");
			put("comedia", "CO");
			put("documental", "DO");
			put("drama", "DR");
			put("fantasía", "FAN");
			put("infantil", "INF");
			put("thriller", "TH");
			put("musical", "MU");
			put("romance", "RO");
			put("terror", "TE");
			put("intriga", "INT");
			put("western", "WE");
		}
	};
	
	@SuppressWarnings("serial")
	private Map<String, String> genIMDB = new HashMap<String, String>(){
		{
			put("acción", "action");
			put("animación", "animation");
			put("aventuras", "adventure");
			put("bélico", "war");
			put("ficción", "sci-fi");
//			put("negro", "film-noir");
			put("comedia", "comedy");
			put("documental", "documentary");
			put("drama", "drama");
			put("fantasía", "fantasy");
			put("infantil", "family");
			put("thriller", "thriller");
			put("musical", "musical");
			put("romance", "romance");
			put("terror", "horror");
			put("intriga", "crime,mistery");
			put("western", "western");
		}
	};
	
	@SuppressWarnings("serial")
	private Map<String, String> genMOVIE = new HashMap<String, String>(){
		{
			put("acción", "&with_genres%5B%5D=28");
			put("animación", "&with_genres%5B%5D=16");
			put("aventuras", "&with_genres%5B%5D=12");
			put("bélico", "&with_genres%5B%5D=10752");
			put("ficción", "&with_genres%5B%5D=878");
//			put("negro", "&with_genres%5B%5D=");
			put("comedia", "&with_genres%5B%5D=35");
			put("documental", "&with_genres%5B%5D=99");
			put("drama", "&with_genres%5B%5D=18");
			put("fantasía", "&with_genres%5B%5D=14");
			put("infantil", "&with_genres%5B%5D=10751");
			put("thriller", "&with_genres%5B%5D=80&with_genres%5B%5D=9648&with_genres%5B%5D=53");
			put("musical", "&with_genres%5B%5D=10402");
			put("romance", "&with_genres%5B%5D=10749");
			put("terror", "&with_genres%5B%5D=27");
			put("intriga", "&with_genres%5B%5D=80&with_genres%5B%5D=9648&with_genres%5B%5D=53");
			put("western", "&with_genres%5B%5D=37");
		}
	};
	
	private String web, genre, url;
	
	public Bot(String web, String genre) {
		this.web = web;
		this.genre = genre;
	}
	
	public void getFilms(int pages) throws IOException {
		for(int i=0; i < pages; i++) {
			switch(this.web) {
				case "IMDB":
					this.url = "https://www.imdb.com/search/title/?genres=" + genIMDB.get(this.genre) + "&start=" + (50*i + 1) + "&ref_=adv_nxt";
					break;
				case "FILMAFFINITY":
					this.url = "https://www.filmaffinity.com/es/topgen.php?genre=" + genFILM.get(this.genre);
					break;
				case "MOVIEDB":
					this.url = "https://www.themoviedb.org/discover/movie?language=es&list_style=1&media_type=movie&page="
							+ i + "&primary_release_year=0&sort_by=popularity.desc&vote_count.gte=0" + genMOVIE.get(genre);
					break;
			}
			
			parseFilms();
		}
	}
	
	private void parseFilms() throws IOException {
		Elements titles = null, ratings = null;
		Document doc = Jsoup.connect(this.url).get();
		
		switch(this.web) {
			case "IMDB":
				titles = doc.select("h3 > a");
				ratings = doc.select("div.ratings-imdb-rating");
				break;
			case "FILMAFFINITY":
				titles = doc.select("");
				ratings = doc.select("");
				break;
			case "MOVIEDB":
				titles = doc.select("");
				ratings = doc.select("");
				break;
		}

		for(int i = 0; i < titles.size(); i++){
			System.out.print(titles.get(i).text() + "\t" + ratings.get(i).text() + "\n");
		}
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public static void main(String [] args) throws IOException {
		Bot bot = new Bot("IMDB", "acción");
		bot.getFilms(2);
	}
}
