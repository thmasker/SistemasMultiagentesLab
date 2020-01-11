package movietool;

import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.proto.AchieveREResponder;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import movietool.utils.FilmScrapper;

@SuppressWarnings("serial")
public class IntegratorAgent extends Agent {
    public void setup() {
        addBehaviour(new InterfaceResponder(this, MessageTemplate.and(
            MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
        )));
    }

    class InterfaceResponder extends AchieveREResponder {
        private String genre;
        private int n_films;

        public InterfaceResponder(Agent agent, MessageTemplate mt) {
            super(agent, mt);
        }

        protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
            // TODO Ver si esto está bien del todo (código, vaya)
            String [] contents = request.getContent().split(";");
            
            /*StringTokenizer data = new StringTokenizer(request.getContent());
            String genre = data.nextToken();
            int count = data.nextInt();*/
            
            if(contents.length != 2) {  // Check valid format
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
            } else throw new NotUnderstoodException("Invalid format: \"GENRE;N_FILMS\" expected");
        }

        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
            
            return null;
        }
    }
}