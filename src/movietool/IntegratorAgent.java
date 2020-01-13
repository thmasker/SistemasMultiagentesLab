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
import jade.domain.FIPAAgentManagement.FailureException;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import movietool.utils.Film;
import movietool.utils.FilmScrapper;

@SuppressWarnings("serial")
public class IntegratorAgent extends Agent {
    private void debug(String msg) {
        System.out.println("[INTEGRATOR] " + msg);
    }
    
    private ArrayList<Film> films = new ArrayList<Film>();

    public void setup() {
        addBehaviour(new InterfaceResponder(this, MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST), MessageTemplate.MatchPerformative(ACLMessage.REQUEST))));
    }

    private class InterfaceResponder extends AchieveREResponder {
        private String genre;
        private int n_films;

        public InterfaceResponder(Agent agent, MessageTemplate mt) {
            super(agent, mt);
        }

        protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
            String[] contents = request.getContent().split(";");

            if (contents.length != 2) // Request format is valid
                throw new NotUnderstoodException("Invalid format: \"GENRE;N_FILMS\" expected");
            else {
                genre = contents[0];

                try {
                    n_films = Integer.parseInt(contents[1]);
                } catch (NumberFormatException nfe) {
                    throw new NotUnderstoodException("Invalid format: \"GENRE;N_FILMS\" expected");
                }

                if (!FilmScrapper.isValidGenre(genre)) // Check valid genre
                    throw new RefuseException("Genre not valid");
                else {
                    ParallelBehaviour pb = new ParallelBehaviour(this.myAgent, ParallelBehaviour.WHEN_ALL) {
                        public int onEnd() {
                            // Get Interface request message
                            ACLMessage request = (ACLMessage)getDataStore().get(REQUEST_KEY);

                            Collections.sort(films);
                            ArrayList<Film> selected = new ArrayList<Film>();                            

                            debug("FILM count: " + films.size());
                            // TODO: (Fix) Way less than n_films are selected

                            for(Film film : films) {
                                if(!selected.contains(film))
                                    selected.add(film);

                                if(selected.size() >= n_films) break;
                            }

                            // Create Interface response message
                            ACLMessage response = request.createReply();
                            response.setPerformative(ACLMessage.INFORM);
                            try {
                                response.setContentObject(selected);
                            } catch(IOException ioe) {
                                response.setPerformative(ACLMessage.FAILURE);
                                response.setContent("Could not serialize content");
                                //throw new FailureException("Could not serialize content");
                            }

                            // Set response message so AchieveREResponder can access it
                            getDataStore().put(RESULT_NOTIFICATION_KEY, response);
                            return super.onEnd();
                        }
                    };

                    ACLMessage collectorRequest = new ACLMessage(ACLMessage.REQUEST);
                    collectorRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
                    collectorRequest.setContent(genre + ";" + n_films);

                    // IMDB Collector Initiator
                    collectorRequest.addReceiver(new AID("IMDB", AID.ISLOCALNAME));
                    pb.addSubBehaviour(new CollectorInitiator(this.myAgent, collectorRequest));
                    collectorRequest.clearAllReceiver();

                    // FilmAffinity Collector Initiator
                    collectorRequest.addReceiver(new AID("FilmAffinity", AID.ISLOCALNAME));
                    pb.addSubBehaviour(new CollectorInitiator(this.myAgent, collectorRequest));
                    collectorRequest.clearAllReceiver();

                    // TheMovieDB Collector Initiator
                    collectorRequest.addReceiver(new AID("MovieDB", AID.ISLOCALNAME));
                    pb.addSubBehaviour(new CollectorInitiator(this.myAgent, collectorRequest));


                    // ParallelBehaviour will prepare the result
                    registerPrepareResultNotification(pb);

                    ACLMessage agreeMsg = request.createReply();
                    agreeMsg.setPerformative(ACLMessage.AGREE);
                    return agreeMsg;
                }
            }
        }
    }


    private class CollectorInitiator extends AchieveREInitiator {
        public CollectorInitiator(Agent a, ACLMessage msg){
            super(a, msg);
        }

        protected void handleAgree(ACLMessage agree){
            System.out.println(getLocalName() + " AGREE: Collector will provide the requested films");
        }

        protected void handleRefuse(ACLMessage refuse){
            System.out.println(getLocalName() + " REFUSE: " + refuse.getContent());
        }

        protected void handleNotUnderstood(ACLMessage notUnderstood){
            System.out.println(getLocalName() + " NOT-UNDERSTOOD: " + notUnderstood.getContent());
        }

        @SuppressWarnings("unchecked")
        protected void handleInform(ACLMessage inform){
            ArrayList<Film> selected = new ArrayList<Film>();

            try {
                selected = (ArrayList<Film>) inform.getContentObject();
            } catch (UnreadableException ue){
                System.out.println(getLocalName() + " INFORM: could not deserialize message content");
            }

            for(Film film: selected){
                films.add(film);
            }
        }

        protected void handleFailure(ACLMessage failure){
            System.out.println(getLocalName() + " FAILURE: " + failure.getContent());
        }
    }
}