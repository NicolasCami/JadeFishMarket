package util;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class RegisterBehaviour extends OneShotBehaviour {
	
	//sender
	private final Agent _agent;
        
        public static final String SELLER = "seller";
        public static final String BUYER = "buyer";
	
	//constructor
	public RegisterBehaviour(Agent a) {
            _agent = a;
	}

	//behaviour action
        @Override
	public void action() {
            ACLMessage msg = new ACLMessage(Performatif.TO_REGISTER);
            msg.addReceiver(new AID(fishmarket.FishMarket.MARCKET_NAME, AID.ISLOCALNAME));
            switch(_agent.getClass().getSimpleName()) {
                case "Seller":
                    msg.setContent(SELLER);
                    break;
                case "Buyer":
                    msg.setContent(BUYER);
                    break;
            }
            _agent.send(msg);
	}

}