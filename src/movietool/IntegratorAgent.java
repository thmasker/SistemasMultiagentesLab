package movietool;

import java.util.StringTokenizer;

import jade.core.Agent;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.proto.AchieveREResponder;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class IntegratorAgent extends Agent {
    public void setup() {
        addBehaviour(new InterfaceResponder(this, MessageTemplate.and(
            MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
        )));
    }

    class InterfaceResponder extends AchieveREResponder {
        public InterfaceResponder(Agent agent, MessageTemplate template) {
            super(agent, template);
        }

        protected ACLMessage handleRequest(ACLMessage request)
            throws NotUnderstoodException, RefuseException {

            /*StringTokenizer data = new StringTokenizer(request.getContent());
            String genre = data.nextToken();
            int count = data.nextInt();*/
            
            if(true) {  // Check valid format
                if(true) { // Check valid genre
                    ACLMessage agreeMsg = request.createReply();
                    agreeMsg.setPerformative(ACLMessage.AGREE);
                    return agreeMsg;
                } else throw new RefuseException("Genre not valid");
            } else throw new NotUnderstoodException("Invalid format");
        }

        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response)
            throws FailureException {
            
            return null;
        }
    }
}