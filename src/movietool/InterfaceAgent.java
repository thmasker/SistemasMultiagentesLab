package movietool;

import java.util.Scanner;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

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
     * InterfaceRequester pide al usuario el número de películas que desea obtener, y el género de las mismas 
     */
    private class InterfaceRequester extends OneShotBehaviour {
        private int n_films;
        private String genre;

        public void action() {
            Scanner sc = new Scanner(System.in);

            System.out.println("How many movies do you want to get?");
            n_films = sc.nextInt();

            for(int i = 0; i < FilmScrapper.FilmGenre.values().length; i++){
                System.out.println("\t- " + FilmScrapper.FilmGenre.values()[i]);
            }
            System.out.println("\nGenre?");
            genre = sc.next();
            sc.close();

            msg.setContent(n_films + ";" + genre);
        }
    }

    /**
     * Si el usuario no quiere repetir el proceso, este comportamiento finaliza el agente
     */
    private class InterfaceRepeater extends OneShotBehaviour {
        private boolean finish = false;
        
        public void action(){
            Scanner sc = new Scanner(System.in);

            System.out.println("Do you want to select more movies? (Y/N)");
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
     * Último estado del FSMBehaviour
     */
    private class InterfaceEnder extends OneShotBehaviour {
        public void action(){
            System.out.println("(C) Alberto Velasco Mata y Diego Pedregal Hidalgo, 2020");
        }
    }

    private class InterfaceInitiator extends AchieveREInitiator {
        public InterfaceInitiator(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        protected void handleAgree(ACLMessage msg){
            System.out.println(getLocalName() + " AGREE: El Integrator proporcionará las películas deseadas");
        }

        protected void handleRefuse(ACLMessage msg){
            System.out.println(getLocalName() + " REFUSE: El Integrator ha rechazado la petición. Vuelva a intentarlo más tarde");
        }

        protected void handleNotUnderstood(ACLMessage msg){
            System.out.println(getLocalName() + " NOT-UNDERSTOOD: El Integrator no entiende la petición. Vuelva a intentarlo");
        }

        protected void handleInform(ACLMessage msg){
            //TODO: Obtener lista de películas y mostrarla al usuario
        }

        protected void handleFailure(ACLMessage msg){
            System.out.println(getLocalName() + " FAILURE: No se establecer comunicación con el Integrator");
        }
    }
}