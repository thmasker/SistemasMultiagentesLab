package movietool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import movietool.utils.Film;
import movietool.utils.FilmScrapper;
import movietool.utils.FilmScrapperFactory;

@SuppressWarnings("serial")
public class IntegratorAgent extends Agent {
    private ArrayList<Film> films = new ArrayList<Film>();

    public void setup() {
        addBehaviour(new IntegratorResponder(this, MessageTemplate.and(
            MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST))));
    }

    protected void takeDown() {
		System.out.println("[" + getLocalName() + "] freeing resources...");
		super.takeDown();
	}

    private class IntegratorResponder extends AchieveREResponder {
        public IntegratorResponder(Agent agent, MessageTemplate mt) {
            super(agent, mt);
        }

        protected ACLMessage getCollectorRequest(String provider, FilmScrapper.FilmRequest filmRequest) {
            ACLMessage collectorRequest = new ACLMessage(ACLMessage.REQUEST);
            collectorRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            collectorRequest.setContent(filmRequest.toString());
            collectorRequest.addReceiver(new AID(provider, AID.ISLOCALNAME));

            return collectorRequest;
        }

        protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
            FilmScrapper.FilmRequest filmRequest = FilmScrapper.FilmRequest.parse(request.getContent());

            // Check whether request format is valid
            if (filmRequest == null)
                throw new NotUnderstoodException("Invalid format: \"GENRE;FILM_COUNT\" expected");
            // Check whether requested genre can be fetched
            else if (!FilmScrapper.isValidGenre(filmRequest.getGenre())) // Check valid genre
                throw new RefuseException("Can't get films with genre \"" + filmRequest.getGenre() + "\"");
            else {
                ParallelBehaviour pb = new ParallelBehaviour(this.myAgent, ParallelBehaviour.WHEN_ALL) {
                    public int onEnd() {
                        // Get Interface request message
                        ACLMessage request = (ACLMessage)getDataStore().get(REQUEST_KEY);

                        // Sort fetched films by rating and limit up to user selected maximum
                        Collections.sort(films);
                        ArrayList<Film> selected = new ArrayList<Film>();
                        for(Film film : films) {
                            if(!selected.contains(film))
                                selected.add(film);

                            if(selected.size() >= filmRequest.getFilmCount()) break;
                        }

                        // Create Interface response message
                        ACLMessage response = request.createReply();
                        response.setPerformative(ACLMessage.INFORM);
                        try {
                            response.setContentObject(selected);
                        } catch(IOException ioe) {
                            response.setPerformative(ACLMessage.FAILURE);
                            response.setContent("Could not serialize films");
                        }

                        // Set response message so AchieveREResponder can access it
                        getDataStore().put(RESULT_NOTIFICATION_KEY, response);
                        return super.onEnd();
                    }
                };

                // Add one initiator per provider
                for(String provider : FilmScrapperFactory.PROVIDERS)
                    pb.addSubBehaviour(
                        new IntegratorInitiator(this.myAgent,
                            getCollectorRequest(provider, filmRequest)));

                // Custom ParallelBehaviour will prepare the result
                registerPrepareResultNotification(pb);

                ACLMessage agree = request.createReply();
                agree.setPerformative(ACLMessage.AGREE);
                agree.setContent("Preparing up to " + filmRequest.getFilmCount() + " films");
                return agree;
            }
        }
    }


    private class IntegratorInitiator extends AchieveREInitiator {
        public IntegratorInitiator(Agent a, ACLMessage msg){
            super(a, msg);
        }

        protected void handleAgree(ACLMessage agree) {
            System.out.println("[" + getLocalName() + "] AGREE: " + agree.getContent());
        }

        protected void handleRefuse(ACLMessage refuse) {
            System.out.println("[" + getLocalName() + "] REFUSE: " + refuse.getContent());
        }

        protected void handleNotUnderstood(ACLMessage notUnderstood) {
            System.out.println("[" + getLocalName() + "] NOT-UNDERSTOOD: " + notUnderstood.getContent());
        }

        @SuppressWarnings("unchecked")
        protected void handleInform(ACLMessage inform) {
            ArrayList<Film> fetched = null;
            try {
                fetched = (ArrayList<Film>) inform.getContentObject();
            } catch (UnreadableException ue){
                System.out.println("[" + getLocalName() + "] INFORM: could not deserialize films from message");
                return;
            }

            for(Film film : fetched)
                films.add(film);
        }

        protected void handleFailure(ACLMessage failure) {
            System.out.println("[" + getLocalName() + "] FAILURE: " + failure.getContent());
        }
    }
}