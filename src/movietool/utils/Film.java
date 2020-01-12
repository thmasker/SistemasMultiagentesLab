package movietool.utils;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Film implements Comparable<Film>, Serializable {
	private String title;
	private double rating;
	
	public Film(String title, double rating) {
		this.title = title;
		this.rating = rating;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	@Override
	public int compareTo(Film film) {
		return (int) ((film.getRating() - this.rating) * 100);
	}

	@Override
	public boolean equals(Object obj){
		if(this == obj){
			return true;
		} else if(obj == null || this.getClass() != obj.getClass()){
			return false;
		} else {
			Film film = (Film) obj;
			return this.title.equals(film.title);
		}
	}
}
