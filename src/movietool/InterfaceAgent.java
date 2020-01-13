package movietool;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import jade.core.Agent;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;

import movietool.utils.Film;
import movietool.utils.FilmScrapper;

@SuppressWarnings("serial")
public class InterfaceAgent extends Agent {
    private Scanner sc = new Scanner(System.in);

    protected void setup() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        msg.addReceiver(new AID("Integrator", AID.ISLOCALNAME));

        addBehaviour(new InterfaceInitiator(this, msg));
    }

    protected void takeDown(){
        System.out.println("[" + getLocalName() + "] freeing resources...");
        System.out.println("\n(C) Alberto Velasco Mata and Diego Pedregal Hidalgo, 2020\n");
        super.takeDown();
    }

    private class InterfaceInitiator extends AchieveREInitiator {
        public InterfaceInitiator(Agent a, ACLMessage request) {
            super(a, request);
        }

        protected Vector prepareRequests(ACLMessage request) {
            /// [ USER INTERACTION ]
            /// Film count
            System.out.print("How many movies do you want to get?\n>> ");
            int filmCount;
            while(true) {
                try{
                    filmCount = Integer.parseInt(sc.nextLine().trim());
                    break;
                } catch(NumberFormatException ime){
                    System.out.print("[" + getLocalName() + "] ERROR: You must enter an integer number\n>> ");
                }
            }
            /// Genre
            System.out.println("Choose a genre from below:");
            for(int i = 0; i < FilmScrapper.FilmGenre.values().length; i++)
                System.out.println("\t> " + FilmScrapper.FilmGenre.values()[i]);
            System.out.print("\n>> ");
            String genre = sc.nextLine().trim();

            // Fill Integrator request
            request.setContent(genre + ";" + filmCount);
            return super.prepareRequests(request);
        }

        protected void handleAgree(ACLMessage agree){
            System.out.println("[" + getLocalName() + "] AGREE: " + agree.getContent());
        }

        protected void handleRefuse(ACLMessage refuse){
            System.out.println("[" + getLocalName() + "] REFUSE: " + refuse.getContent());
        }

        protected void handleNotUnderstood(ACLMessage notUnderstood){
            System.out.println("[" + getLocalName() + "] NOT-UNDERSTOOD: " + notUnderstood.getContent());
        }

        @SuppressWarnings("unchecked")
        protected void handleInform(ACLMessage inform){
            ArrayList<Film> films = null;

            try {
                films = (ArrayList<Film>) inform.getContentObject();
            } catch (UnreadableException ue){
                System.out.println("[" + getLocalName() + "] INFORM: could not deserialize message content");
                return;
            }

            System.out.println( "  *********************************************************  \n" +
                                "  *                         FILMS                         *  \n" +
                                "  *********************************************************  ");
            for(Film film : films)
                System.out.println("\t(" + film.getRating() + ")\t" + film.getTitle());
        }

        protected void handleFailure(ACLMessage failure){
            System.out.println("[" + getLocalName() + "] FAILURE: " + failure.getContent());
        }

        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }
    }
}