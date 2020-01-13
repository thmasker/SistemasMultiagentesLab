// TODO COMPROBAR QUE EL MAKEFILE FUNCIONE CORRECTAMENTE EN LINUX
package movietool;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;

import movietool.utils.Film;
import movietool.utils.FilmScrapper;

@SuppressWarnings("serial")
public class InterfaceAgent extends Agent {
    private void debug(String msg) {
        System.out.println("[INTERFACE] " + msg);
    }

    private Scanner sc = new Scanner(System.in);
    private ACLMessage msg;

    protected void setup() {
        msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        msg.addReceiver(new AID("Integrator", AID.ISLOCALNAME));

        addBehaviour(new InterfaceInitiator(this, msg));
    }

    protected void takeDown(){
        System.out.println(getLocalName() + " freeing resources...");
        System.out.println("\n(C) Alberto Velasco Mata and Diego Pedregal Hidalgo, 2020\n");
        super.takeDown();
    }

    private class InterfaceInitiator extends AchieveREInitiator {
        public InterfaceInitiator(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        protected int requestInt(){
            while(true) {
                try{
                    int n = sc.nextInt();
                    return n;
                } catch(InputMismatchException ime){
                    System.out.println(getLocalName() + " ERROR: You must enter an integer number");
                }
            }
        }

        protected java.util.Vector prepareRequests(ACLMessage request) {
            debug("Preparing request to " + request.getAllReceiver().next());


            System.out.println("How many movies do you want to get?");
            int n_films = requestInt();

            for(int i = 0; i < FilmScrapper.FilmGenre.values().length; i++){
                System.out.println("\t- " + FilmScrapper.FilmGenre.values()[i]);
            }
            System.out.println("\nGenre?");
            String genre = sc.next();

            request.setContent(genre + ";" + n_films);

            debug("Request ready: " + request);
            return super.prepareRequests(request);
        }

        protected void handleAgree(ACLMessage agree){
            System.out.println(getLocalName() + " AGREE: Integrator will provide the requested films");
        }

        protected void handleRefuse(ACLMessage refuse){
            System.out.println(getLocalName() + " REFUSE: " + refuse.getContent());
        }

        protected void handleNotUnderstood(ACLMessage notUnderstood){
            System.out.println(getLocalName() + " NOT-UNDERSTOOD: " + notUnderstood.getContent());
        }

        @SuppressWarnings("unchecked")
        protected void handleInform(ACLMessage inform){
            //debug("Received: " + inform.getContent());
            ArrayList<Film> films = null;

            // TODO: Ver si la serialización funciona realmente
            try {
                films = (ArrayList<Film>) inform.getContentObject();
            } catch (UnreadableException ue){
                System.out.println(getLocalName() + " INFORM: could not deserialize message content");
            }
            
            if(films == null) return;
            
            System.out.println("------ FILMS SELECTED -------");
            for(Film film : films){
                System.out.println("\t- " + film.getRating() + "\t" + film.getTitle());
            }
        }

        protected void handleFailure(ACLMessage failure){
            System.out.println(getLocalName() + " FAILURE: " + failure.getContent());
        }

        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }
    }
}