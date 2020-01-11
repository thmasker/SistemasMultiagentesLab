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

		if(args == null){
			System.out.println(getLocalName() + " did not receive from which website to retrieve. Terminating...");
			this.takeDown();
		}

		try {
			fs = FilmScrapperFactory.createFilmScrapper(args[0].toString());
		} catch (NameNotFoundException nfe) {
			System.out.println(getLocalName() + " " + nfe.getExplanation() + " Terminating...");
			this.takeDown();
		}

		addBehaviour(new CollectorResponder(this, MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST)));
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
			ArrayList<Film> selected = new ArrayList<Film>();
			String[] contents = msg.getContent().split(";");
			int n_films = Integer.parseInt(contents[1]);

			try {
				// TODO Mirar qué ocurre cuando contents[0] no es un género válido y n_films no es un número
				int actualCount = fs.fetch(FilmGenre.valueOf(contents[0]), n_films * 4);
				System.out.println(getLocalName() + "> Got " + actualCount + " films");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			try {
				// TODO Mirar qué ocurre cuando n_films no es válido
				selected = fs.selectFilms(n_films);
				System.out.println(getLocalName() + "> Randomly selected " + selected.size() + " films");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			msg = msg.createReply();
			try {
				msg.setContentObject(selected);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			return msg;
		}
	}
}