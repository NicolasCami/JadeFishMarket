/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fishmarket;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import util.Performatif;
import util.RegisterBehaviour;

public class FishMarketBehaviour extends CyclicBehaviour {

	//parent agent
	private final FishMarket _agent;
	
	public FishMarketBehaviour(FishMarket a) {
            _agent = a;
	}

	@Override
	public void action() {
            //receive messages
            ACLMessage msg = myAgent.receive();
            if(msg != null) {
                switch(msg.getPerformative()) {
                    
                    // a new agent is registering
                    case Performatif.TO_REGISTER:
                        switch (msg.getContent()) {
                            case RegisterBehaviour.SELLER:
                                _agent.registerSeller(msg.getSender().getLocalName());
                                break;
                            case RegisterBehaviour.BUYER:
                                _agent.registerBuyer(msg.getSender().getLocalName());
                                break;
                        }
                        break;
                        
                    case Performatif.TO_ANNOUNCE:
                        try {
                            _agent.newAnnounce(msg.getContentObject());
                        } catch (UnreadableException ex) {
                            System.out.println("error with serialized object");
                        }
                        break;
                        
                    case Performatif.TO_BID:
                        try {
                            _agent.newBid(msg.getContentObject());
                        } catch (UnreadableException ex) {
                            System.out.println("error with serialized object");
                        }
                        break;
                        
                    case Performatif.TO_ATTRIBUTE:
                        try {
                            _agent.attribute(msg.getContentObject());
                        } catch (UnreadableException ex) {
                            System.out.println("error with serialized object");
                        }
                        break;
                        
                    case Performatif.TO_GIVE:
                        try {
                            _agent.give(msg.getContentObject());
                        } catch (UnreadableException ex) {
                            System.out.println("error with serialized object");
                        }
                        break;
                        
                    case Performatif.TO_PAY:
                        try {
                            _agent.pay(msg.getContentObject());
                        } catch (UnreadableException ex) {
                            System.out.println("error with serialized object");
                        }
                        break;
                }
            }
            else {
                block();
            }

	}


}