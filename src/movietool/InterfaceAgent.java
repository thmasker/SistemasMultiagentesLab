package movietool;

import java.util.Scanner;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import movietool.utils.FilmScrapper;

public class InterfaceAgent extends Agent {
    private static final long serialVersionUID = 1L;

    protected void setup() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        
        AID id = new AID();
        id.setLocalName("Integrator");

        msg.addReceiver(id);

        InterfaceManager fsm = new InterfaceManager();

        fsm.registerFirstState(new InterfaceRequester(), "Requester");
        fsm.registerState(new InterfaceInitiator(this, msg, 0, ""), "Initiator");
        fsm.registerState(new InterfaceDeleter(), "Deleter");

        fsm.registerDefaultTransition("Requester", "Initiator");
        fsm.registerDefaultTransition("Initiator", "Deleter");
        fsm.registerTransition("Deleter", "Requester", 0);

        addBehaviour(fsm);
    }

    protected void takeDown(){
        System.out.println(getLocalName() + " freeing resources...");
        super.takeDown();
    }

    private class InterfaceManager extends FSMBehaviour {
        private static final long serialVersionUID = 1L;

        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }
    }

    private class InterfaceRequester extends OneShotBehaviour {
        private static final long serialVersionUID = 1L;

        public void action() {
            Scanner sc = new Scanner(System.in);

            System.out.println("How many movies do you want to get?");
            int n_films = sc.nextInt();

            for(int i = 0; i < FilmScrapper.FilmGenre.values().length; i++){
                System.out.println((i+1) + ". " + FilmScrapper.FilmGenre.values()[i]);
            }
            System.out.println("\nGenre?");
            String genre = sc.next();

            sc.close();
        }
    }

    private class InterfaceDeleter extends OneShotBehaviour {
        private static final long serialVersionUID = 1L;

        private boolean finish;
        
        public void action(){

        }

        public int onEnd(){
            return 0;
        }
    }

    private class InterfaceInitiator extends AchieveREInitiator {
        private static final long serialVersionUID = 1L;
        
        private int n_films;
        private String genre;

        public InterfaceInitiator(Agent a, ACLMessage msg, int n_films, String genre){
            super(a, msg);
            this.n_films = n_films;
            this.genre = genre;
        }

        protected void handleAgree(ACLMessage msg){

        }

        protected void handleRefuse(ACLMessage msg){

        }

        protected void handleNotUnderstood(ACLMessage msg){

        }

        protected void handleInform(ACLMessage msg){

        }

        protected void handleFailure(ACLMessage msg){

        }
    }
}