/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package buyer;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Calendar;
import util.MessageBehaviour;
import util.Offer;
import util.Performatif;

public class BuyerAnnounceBehaviour extends FSMBehaviour {
    
    private final Offer _offer;
    private final Buyer _agent;
    
    public BuyerAnnounceBehaviour(Buyer agent, Offer offer) {
        super(agent);
        _offer = offer;
        _agent = agent;
        
        //definiton des etats
        registerFirstState(new BidBehaviour(),"C");
        registerState(new AttributeBehaviour(),"D");
        registerState(new GiveBehaviour(),"E");
        registerLastState(new PayBehaviour(),"F");

        //definition des transaction
        registerDefaultTransition("C","C");
        registerTransition("C","D", Performatif.TO_ATTRIBUTE);
        registerTransition("D","E", Performatif.TO_GIVE);
        registerTransition("E","F", Performatif.TO_PAY);
    }
    
    private class BidBehaviour extends Behaviour {
        
        private int step = 0;
        
        public static final int STEP_ANNOUNCE = 0;
        public static final int END = 10;

        @Override
        public void action() {
            //System.out.println(myAgent.getAID().getName() + " (offer " + _offer.toString() + ") wait for bid");
            MessageTemplate modele = MessageTemplate.and(MessageTemplate.MatchPerformative(Performatif.TO_ATTRIBUTE), MessageTemplate.MatchInReplyTo(_offer.getSeller()+_offer.getName()));
            ACLMessage msg = myAgent.receive(modele);
            if (msg != null) {
                //System.out.println(myAgent.getAID().getName()+ " receive msg");
                switch (msg.getPerformative()) {
                    case Performatif.TO_ATTRIBUTE:
                        System.out.println(myAgent.getAID().getName()+ " receive attribute");
                        step = END;
                        break;
                    default:
                        break;
                }
            }
        }
        
        @Override
        public boolean done() {
            return (step==END);
        }
        
        @Override
        public int onEnd() {
            return Performatif.TO_ATTRIBUTE;
        }
    }
    
    private class AttributeBehaviour extends OneShotBehaviour{

        @Override
        public void action() {
            _agent.changeStatus(_offer, BuyerGUI.STEP_ATTRIBUTE_STATUS);
        }
        
        @Override
        public int onEnd() {
            return Performatif.TO_GIVE;
        }
    }
    
    private class GiveBehaviour extends Behaviour{

        private int step = 0;
        
        public static final int STEP_WAIT_FOR_PRODUCT = 0;
        public static final int END = 10;

        @Override
        public void action() {
            MessageTemplate modele = MessageTemplate.and(MessageTemplate.MatchPerformative(Performatif.TO_GIVE), MessageTemplate.MatchInReplyTo(_offer.getSeller()+_offer.getName()));
            ACLMessage msg = myAgent.receive(modele);
            if (msg != null) {
                switch (msg.getPerformative()) {
                    case Performatif.TO_GIVE:
                        _agent.changeStatus(_offer, BuyerGUI.STEP_GIVE_STATUS);
                        System.out.println(myAgent.getAID().getName()+ " receive product");
                        step = END;
                        break;
                    default:
                        break;
                }
            }
            else {
                block();
            }
        }
        
        @Override
        public boolean done() {
            return (step==END);
        }
        
        @Override
        public int onEnd() {
            return Performatif.TO_PAY;
        }
    }
    
    private class PayBehaviour extends Behaviour{

        private long clock = -1;
        private int step = 0;
        
        public static final int STEP_FOR_END = 0;
        public static final int END = 10;
        public static final int DELAY_FOR_END = 2000;

        @Override
        public void action() {
            // initialize delay
            if(clock<0) {
                clock = Calendar.getInstance().getTimeInMillis();
            }
            
            // wait a bit before ending this step
            if(clock > 0 && (clock+DELAY_FOR_END<Calendar.getInstance().getTimeInMillis())) {
                myAgent.addBehaviour(new MessageBehaviour(this.myAgent, Performatif.TO_PAY, new String[]{fishmarket.FishMarket.MARCKET_NAME}, _offer));
                _agent.changeStatus(_offer, BuyerGUI.STEP_PAY_STATUS);
                step = END;
            }
        }
        
        @Override
        public boolean done() {
            return (step==END);
        }
        
        @Override
        public int onEnd() {
            return Performatif.TO_PAY;
        }
    }
    
    @Override
    public int onEnd() {
        return super.onEnd();
    }
}
