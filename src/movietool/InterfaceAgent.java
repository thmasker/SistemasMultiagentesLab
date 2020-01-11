// TODO COMPROBAR QUE EL MAKEFILE FUNCIONE CORRECTAMENTE EN LINUX
package movietool;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;

import movietool.utils.Film;
import movietool.utils.FilmScrapper;

@SuppressWarnings("serial")
public class InterfaceAgent extends Agent {
    private ACLMessage msg;

    protected void setup() {
        AID id = new AID();
        id.setLocalName("Integrator");

        msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(id);

        InterfaceManager fsm = new InterfaceManager();

        fsm.registerFirstState(new InterfaceRequester(), "Requester");
        fsm.registerState(new InterfaceInitiator(this, msg), "Initiator");
        fsm.registerState(new InterfaceRepeater(), "Repeater");
        fsm.registerLastState(new InterfaceEnder(), "Ender");

        fsm.registerDefaultTransition("Requester", "Initiator");
        fsm.registerDefaultTransition("Initiator", "Repeater");
        fsm.registerTransition("Repeater", "Requester", 1);
        fsm.registerTransition("Repeater", "Ender", 0);

        addBehaviour(fsm);
    }

    protected void takeDown(){
        System.out.println(getLocalName() + " freeing resources...");
        super.takeDown();
    }

    private class InterfaceManager extends FSMBehaviour {
        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }
    }

    /**
     * InterfaceRequester ask the user both the number of movies to retrieve and their genre
     */
    private class InterfaceRequester extends OneShotBehaviour {
        // TODO requestInt() no funciona bien.
        // TODO Cuando requestInt() funcione, comprobar la repetición del proceso
        private Scanner sc = new Scanner(System.in);

        private int n_films;
        private String genre;

        public void action() {
            System.out.println("How many movies do you want to get?");
            n_films = this.requestInt();

            for(int i = 0; i < FilmScrapper.FilmGenre.values().length; i++){
                System.out.println("\t- " + FilmScrapper.FilmGenre.values()[i]);
            }
            System.out.println("\nGenre?");
            genre = sc.next();

            msg.setContent(genre + ";" + n_films);
        }

        private int requestInt(){
            while(true){
                try{
                    int n = sc.nextInt();
                    return n;
                } catch(InputMismatchException ime){
                    System.out.println(getLocalName() + " ERROR: You must enter an integer number");
                }
            }
        }
    }

    /**
     * If user does not want to repeat the process, this behaviour ends the agent
     */
    private class InterfaceRepeater extends OneShotBehaviour {
        private boolean finish = false;

        public void action(){
            Scanner sc = new Scanner(System.in);

            System.out.println("\nDo you want to select more movies? (Y/N)");
            String option = sc.next();
            sc.close();

            if(option.charAt(0) == 'Y' || option.charAt(0) == 'y')
                finish = true;
        }

        public int onEnd(){
            return finish ? 1 : 0;
        }
    }

    /**
     * FSMBehaviour's last state
     */
    private class InterfaceEnder extends OneShotBehaviour {
        public void action(){
            System.out.println("\n(C) Alberto Velasco Mata and Diego Pedregal Hidalgo, 2020\n");
        }
    }

    private class InterfaceInitiator extends AchieveREInitiator {
        public InterfaceInitiator(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        protected void handleAgree(ACLMessage msg){
            System.out.println(getLocalName() + " AGREE: Integrator will provide the requested films");
        }

        protected void handleRefuse(ACLMessage msg){
            System.out.println(getLocalName() + " REFUSE: " + msg.getContent());
        }

        protected void handleNotUnderstood(ACLMessage msg){
            System.out.println(getLocalName() + " NOT-UNDERSTOOD: " + msg.getContent());
        }

        @SuppressWarnings("unchecked")
        protected void handleInform(ACLMessage msg){
            ArrayList<Film> films = new ArrayList<Film>();

            // TODO: Ver si la serialización funciona realmente
            try {
                films = (ArrayList<Film>) msg.getContentObject();
            } catch (UnreadableException ue){
                System.out.println(getLocalName() + " INFORM: could not deserialize message content");
            }

            System.out.println("------ FILMS SELECTED -------");
            for(Film film: films){
                System.out.println("\t- " + film.getRating() + "\t" + film.getTitle());
            }
        }

        protected void handleFailure(ACLMessage msg){
            System.out.println(getLocalName() + " FAILURE: Could not establish communication with Integrator");
        }
    }
}