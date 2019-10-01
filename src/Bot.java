import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

import java.io.IOException;

public class Bot{
	private Document doc;
	
	public Bot(String url) throws IOException{
		this.doc = Jsoup.connect(url).get();
		
		Elements titles = doc.select("h3 > a");
		Elements ratings = doc.select("div.ratings-imdb-rating");

		for(int i = 0; i < titles.size(); i++){
			System.out.print(titles.get(i).text() + "\t" + ratings.get(i).text() + "\n");
		}
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
	
	public static void main(String [] args) throws IOException {
		new Bot("https://www.imdb.com/search/title/?genres=romance&start=1&ref_=adv_nxt");
	}
}
