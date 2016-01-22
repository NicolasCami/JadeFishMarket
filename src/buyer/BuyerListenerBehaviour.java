/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package buyer;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import util.Performatif;

public class BuyerListenerBehaviour extends CyclicBehaviour {

	//parent agent
	private final Buyer _agent;
	
	public BuyerListenerBehaviour(Buyer a) {
            _agent = a;
	}

	@Override
	public void action() {
            //receive messages
            MessageTemplate modele = MessageTemplate.MatchPerformative(Performatif.TO_ANNOUNCE);			 
            ACLMessage msg = myAgent.receive(modele);
            if (msg != null) {
                //System.out.println(myAgent.getAID().getName()+ " receive msg");
                switch (msg.getPerformative()) {
                    case Performatif.TO_ANNOUNCE:
                        //System.out.println(myAgent.getAID().getName()+ " receive announce");
                        try {
                            _agent.newAnnounce(msg.getContentObject());
                        } catch (UnreadableException ex) {
                            System.out.println(myAgent.getAID().getName()+ " can't read announce");
                        }
                        break;
                    default:
                        break;
                }
            }
            else {
                block();
            }
	}


}