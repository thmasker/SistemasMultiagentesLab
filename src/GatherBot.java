import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.jsoup.select.Elements;

public abstract class GatherBot {
	protected String genre;
	protected String url;
	protected Map<String, String> genres;
	protected ArrayList<Film> films;
	
	public GatherBot(String genre) {
		this.genre = genre;
	}
	
	public abstract void get_data(int pages) throws IOException;	//	Descarga html
	protected abstract void parseData(Elements titles, Elements ratings);		//	Almacena la información deseada del html descargado

	public ArrayList<Film> getFilms() {
		return films;
	}

	public void setFilms(ArrayList<Film> films) {
		this.films = films;
	}
}
