package movietool;

import java.io.IOException;
import java.util.ArrayList;
import javax.naming.NameNotFoundException;

import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

import movietool.utils.Film;
import movietool.utils.FilmScrapper;
import movietool.utils.FilmScrapperFactory;
import movietool.utils.FilmScrapper.FilmGenre;

@SuppressWarnings("serial")
public class CollectorAgent extends Agent {
	private FilmScrapper fs;

	protected void setup() {
		Object[] args = getArguments();

		if (args == null) {
			System.out.println(getLocalName() + " did not receive from which website to retrieve. Terminating...");
			this.takeDown();
		}

		try {
			fs = FilmScrapperFactory.createFilmScrapper(args[0].toString());
		} catch (NameNotFoundException nfe) {
			System.out.println(getLocalName() + " " + nfe.getExplanation() + " Terminating...");
			this.takeDown();
		}

		addBehaviour(new CollectorResponder(this,
				MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST)));
	}

	protected void takeDown() {
		System.out.println(getLocalName() + " freeing resources...");
		super.takeDown();
	}

	private class CollectorResponder extends AchieveREResponder {
		public CollectorResponder(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		protected ACLMessage handleRequest(ACLMessage msg) throws NotUnderstoodException {
			String[] contents = msg.getContent().split(";");
			int n_films = Integer.parseInt(contents[1]);

			try {
				// TODO Mirar qué ocurre cuando contents[0] no es un género válido y n_films no es un número
				// TODO n_films hay que regularlo, porque es el valor para el select, no para el fetch
				int actualCount = fs.fetch(FilmGenre.valueOf(contents[0]), n_films);
				System.out.println("> Got " + actualCount + " films");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			try {
				// TODO Mirar qué ocurre cuando n_films no es válido
				ArrayList<Film> selected = fs.selectFilms(n_films);
				System.out.println("> Randomly selected " + selected.size() + " films");
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
			}

			msg = msg.createReply();

			return msg;
		}
	}
}