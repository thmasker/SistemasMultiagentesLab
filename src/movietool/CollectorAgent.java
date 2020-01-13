package movietool;

import java.io.IOException;
import java.util.ArrayList;
import javax.naming.NameNotFoundException;

import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
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
		try {
			fs = FilmScrapperFactory.createFilmScrapper(getLocalName());
		} catch (NameNotFoundException nfe) {
			System.out.println("[" + getLocalName() + "] " + nfe.getExplanation() + " - Terminating...");
			this.takeDown();
		}

		addBehaviour(new IntegratorResponder(this, MessageTemplate.and(
			MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
			MessageTemplate.MatchPerformative(ACLMessage.REQUEST))));
	}

	protected void takeDown() {
		System.out.println("[" + getLocalName() + "] freeing resources...");
		super.takeDown();
	}

	private class IntegratorResponder extends AchieveREResponder {
		private FilmScrapper.FilmRequest filmRequest;

		public IntegratorResponder(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
			filmRequest = FilmScrapper.FilmRequest.parse(request.getContent());

            // Check whether request format is valid
            if (filmRequest == null)
                throw new NotUnderstoodException("Invalid format: \"GENRE;FILM_COUNT\" expected");
            // Check whether requested genre can be fetched
            else if (!FilmScrapper.isValidGenre(filmRequest.getGenre())) // Check valid genre
                throw new RefuseException("Can't get films with genre \"" + filmRequest.getGenre() + "\"");
            else {
				ACLMessage agree = request.createReply();
				agree.setPerformative(ACLMessage.AGREE);
				agree.setContent("Fetching films from " + fs.getProvider());
				return agree;
            }
		}

		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
			// Fetch films from provider
			try {
				int actualCount = fs.fetch(
					FilmGenre.valueOf(filmRequest.getGenre()),
					4 * filmRequest.getFilmCount());
				
				System.out.println("[" + getLocalName() + "] " + actualCount + " films fetched");
			} catch (IOException ioe) {
				throw new FailureException("Fetch from \"" + fs.getProvider() + "\" failed");
			}

			// Select random fetched films
			ArrayList<Film> selected = new ArrayList<Film>();
			try {
				selected = fs.selectFilms(filmRequest.getFilmCount());
			} catch (IOException ioe) {
				throw new FailureException("Random selection from \"" + fs.getProvider() + "\" failed");
			}
			System.out.println("[" + getLocalName() + "] " + selected.size() + " films randomly selected");

			ACLMessage inform = request.createReply();
			inform.setPerformative(ACLMessage.INFORM);
			try {
				inform.setContentObject(selected);
			} catch (IOException ioe) {
				throw new FailureException("Could not serialize films from \"" + fs.getProvider() + "\"");
			}
			return inform;
        }
	}
}