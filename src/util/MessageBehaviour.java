package util;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.io.Serializable;

public class MessageBehaviour extends OneShotBehaviour {
	
	private final Agent         _agent;
        private final int           _performatif;
        private final String[]      _receivers;
        private final Serializable  _object;
        private ACLMessage          _msg;
        
        public static final String SELLER = "seller";
        public static final String BUYER = "buyer";
	
	//constructor
	public MessageBehaviour(Agent a, int p, String[] r) {
            _agent = a;
            _performatif = p;
            _receivers = r;
            _object = null;
            _msg = new ACLMessage(_performatif);
	}
        public MessageBehaviour(Agent a, int p, String[] r, Serializable o) {
            _agent = a;
            _performatif = p;
            _receivers = r;
            _object = o;
            _msg = new ACLMessage(_performatif);
	}
        
        public void setRealSender(String name) {
            _msg.setInReplyTo(name);
        }

	//behaviour action
        @Override
	public void action() {
            for(String r : _receivers) {
                _msg.addReceiver(new AID(r, AID.ISLOCALNAME));
            }
            if(_object != null) {
                try {
                    _msg.setContentObject(_object);
                } catch (IOException ex) {
                    System.out.println("can't serialize object : " + _object.toString());
                }
            }
            //System.out.println("message request : " + msg.toString());
            _agent.send(_msg);
	}

}