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
			System.out.println(getLocalName() + " " + nfe.getExplanation() + " Terminating...");
			this.takeDown();
		}

		addBehaviour(new CollectorResponder(this, MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST), MessageTemplate.MatchPerformative(ACLMessage.REQUEST))));
	}

	protected void takeDown() {
		System.out.println(getLocalName() + " freeing resources...");
		super.takeDown();
	}

	private class CollectorResponder extends AchieveREResponder {
		private String genre;
		private int n_films;

		public CollectorResponder(Agent a, MessageTemplate mt) {
			super(a, mt);
		}

		protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
            String [] contents = request.getContent().split(";");
            
            if(contents.length != 2)  // Check valid format
				throw new NotUnderstoodException("Invalid format: \"GENRE;N_FILMS\" expected");
			else {
                genre = contents[0];

                try {
                    n_films = Integer.parseInt(contents[1]);
                } catch (NumberFormatException nfe) {
                    throw new NotUnderstoodException("Invalid format: \"GENRE;N_FILMS\" expected");
                }

                if(FilmScrapper.isValidGenre(genre)) {  // Check valid genre
                    ACLMessage agreeMsg = request.createReply();
                    agreeMsg.setPerformative(ACLMessage.AGREE);
                    return agreeMsg;
                } else throw new RefuseException("Genre not valid");
            }
		}

		protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
			ArrayList<Film> selected = new ArrayList<Film>();

			try {
				int actualCount = fs.fetch(FilmGenre.valueOf(genre), n_films * 4);
				System.out.println(getLocalName() + "> Got " + actualCount + " films");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			try {
				selected = fs.selectFilms(n_films);
				System.out.println(getLocalName() + "> Randomly selected " + selected.size() + " films");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			ACLMessage informMsg = request.createReply();
			informMsg.setPerformative(ACLMessage.INFORM);
			try {
				informMsg.setContentObject(selected);
			} catch (IOException ioe) {
				throw new FailureException("Could not serialize content");
			}

			return informMsg;
        }
	}
}