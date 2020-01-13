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

    private ACLMessage msg;
    private ParallelBehaviour pb;
    private ArrayList<Film> films = new ArrayList<Film>();

    public void setup() {
        msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

        pb = new ParallelBehaviour(this, ParallelBehaviour.WHEN_ALL);

        addBehaviour(new InterfaceResponder(this, MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST), MessageTemplate.MatchPerformative(ACLMessage.REQUEST))));
    }

    private class InterfaceResponder extends AchieveREResponder {
        private String genre;
        private int n_films;

        public InterfaceResponder(Agent agent, MessageTemplate mt) {
            super(agent, mt);
        }

        protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
            // TODO Ver si esto está bien del todo (código, vaya)
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
                    AID id = new AID();

                    msg.setContent(genre + ";" + n_films);
                    
                    // IMDB Collector receiver
                    id.setLocalName("IMDB");
                    msg.addReceiver(id);
                    pb.addSubBehaviour(new InterfaceInitiator(myAgent, msg));

                    // FilmAffinity Collector receiver
                    id.setLocalName("FilmAffinity");
                    msg.addReceiver(id);
                    pb.addSubBehaviour(new InterfaceInitiator(myAgent, msg));

                    // MovieDB Collector receiver
                    id.setLocalName("MovieDB");
                    msg.addReceiver(id);
                    pb.addSubBehaviour(new InterfaceInitiator(myAgent, msg));

                    myAgent.addBehaviour(pb);

                    ACLMessage agreeMsg = request.createReply();
                    agreeMsg.setPerformative(ACLMessage.AGREE);
                    return agreeMsg;
                }
                    
            }
        }

        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
            ArrayList<Film> selected = new ArrayList<Film>();

            ACLMessage informMsg = request.createReply();
            informMsg.setPerformative(ACLMessage.INFORM);

            // TODO lo que tiene que hacer el Integrator (seleccionar los n_films DISTINTOS mejores de
            // n_films*3 totales (n_films por cada Collector) ¿?)
            Collections.sort(films);
            int n = n_films;
            while(n > 0){
                Film film = films.get(n);

                if(!selected.contains(film)){
                    selected.add(film);
                    n--;
                }
            }

            try {
                informMsg.setContentObject(selected);
            } catch (IOException ioe) {
                throw new FailureException("Could not serialize content");
            }

            return informMsg;
        }
    }

    private class InterfaceInitiator extends AchieveREInitiator {
        public InterfaceInitiator(Agent a, ACLMessage msg){
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

            // TODO: Ver si la serialización funciona realmente
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