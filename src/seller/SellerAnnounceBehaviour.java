package seller;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.Calendar;
import util.MessageBehaviour;
import util.Offer;
import util.Performatif;

public class SellerAnnounceBehaviour extends FSMBehaviour {
    
    private Offer _offer;
    private final Seller _agent;
    
    public SellerAnnounceBehaviour(Seller agent, Offer offer) {
        super(agent);
        _offer = offer;
        _agent = agent;
        
        //definiton des etats
        registerFirstState(new AnnounceBehaviour(),"B");
        registerState(new OneBidBehaviour(),"C");
        registerState(new SeveralBidBehaviour(),"D");
        registerState(new AttributeBehaviour(),"E");
        registerState(new GiveBehaviour(),"F");
        registerLastState(new PayBehaviour(),"G");

        //definition des transaction
        registerDefaultTransition("B","B");
        registerTransition("B","B", Performatif.TO_ANNOUNCE);
        registerTransition("B","C", Performatif.TO_BID);
        registerTransition("C","D", Performatif.TO_BID);
        registerTransition("D","D", Performatif.TO_BID);
        registerTransition("D","B", Performatif.TO_ANNOUNCE);
        registerTransition("C","E", Performatif.TO_ATTRIBUTE);
        registerTransition("E","F", Performatif.TO_GIVE);
        registerTransition("F","G", Performatif.TO_PAY);
    }
    
    private class AnnounceBehaviour extends Behaviour {
        
        private long clock = -1;
        private int step = 0;
        
        public static final int STEP_ANNOUNCE = 0;
        public static final int STEP_WAIT_FOR_BID = 1;
        public static final int END = 10;

        @Override
        public void action() {
            switch(step) {
                case STEP_ANNOUNCE:
                    if(_offer != null) {
                        myAgent.addBehaviour(new MessageBehaviour(this.myAgent, Performatif.TO_ANNOUNCE, new String[]{fishmarket.FishMarket.MARCKET_NAME}, _offer));
                        step = STEP_WAIT_FOR_BID;
                        clock = Calendar.getInstance().getTimeInMillis();
                    }
                    break;
                case STEP_WAIT_FOR_BID:
                    MessageTemplate modele = MessageTemplate.and(MessageTemplate.MatchPerformative(Performatif.TO_BID), MessageTemplate.MatchInReplyTo(_offer.getName()));
                    ACLMessage msg = myAgent.receive(modele);
                    if (msg != null) {
                        switch (msg.getPerformative()) {
                            case Performatif.TO_BID:
                                System.out.println(myAgent.getAID().getName()+ " receive bid from AnnounceBehaviour");
                                try {
                                    _agent.newBid(msg.getContentObject());
                                } catch (UnreadableException ex) {
                                    System.out.println(myAgent.getAID().getName()+ " can't read bid");
                                }
                                step = END;
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
            
            // if waiting too much, send another annouce with a lower price
            if(clock > 0 && (clock+_offer.getDelay()<Calendar.getInstance().getTimeInMillis())) {
                _agent.changeStatus(_offer, SellerGUI.STEP_REANNOUNCE_STATUS);
                System.out.println(myAgent.getAID().getName()+ " delay is over, re-annouce");
                _offer.setPrice(_offer.getPrice()-_offer.getStep());
                step = STEP_ANNOUNCE;
                clock = -1;
            }
        }

        @Override
        public boolean done() {
            return (step==END);
        }
        
        @Override
        public int onEnd() {
            step = STEP_ANNOUNCE;
            clock = -1;
            return Performatif.TO_BID;
        }
    }
    
    private class OneBidBehaviour extends Behaviour {

        private long clock = -1;
        private int step = 0;
        
        public static final int STEP_WAIT_FOR_BID = 0;
        public static final int END = 10;
        public static final int END_WITH_BID = 20;

        @Override
        public void action() {
            _agent.changeStatus(_offer, SellerGUI.STEP_ONE_BID_STATUS);
            switch(step) {
                case STEP_WAIT_FOR_BID:
                    MessageTemplate modele = MessageTemplate.and(MessageTemplate.MatchPerformative(Performatif.TO_BID), MessageTemplate.MatchInReplyTo(_offer.getName()));
                    ACLMessage msg = myAgent.receive(modele);
                    if (msg != null) {
                        switch (msg.getPerformative()) {
                            case Performatif.TO_BID:
                                System.out.println(myAgent.getAID().getName()+ " receive bid from OneBid");
                                try {
                                    _agent.newBid(msg.getContentObject());
                                    clock = Calendar.getInstance().getTimeInMillis();
                                } catch (UnreadableException ex) {
                                    System.out.println(myAgent.getAID().getName()+ " can't read bid");
                                }
                                step = END_WITH_BID;
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
            
            // initialize delay
            if(clock<0) {
                clock = Calendar.getInstance().getTimeInMillis();
            }
            
            // if waiting too much, end
            if(clock > 0 && (clock+_offer.getDelay()<Calendar.getInstance().getTimeInMillis())) {
                System.out.println(myAgent.getAID().getName()+ " delay is over, end");
                step = END;
            }
        }

        @Override
        public boolean done() {
            return (step==END) || (step==END_WITH_BID);
        }
        
        @Override
        public int onEnd() {
            clock = -1;
            if(step==END_WITH_BID) {
                step = STEP_WAIT_FOR_BID;
                return Performatif.TO_BID;
            }
            step = STEP_WAIT_FOR_BID;
            return Performatif.TO_ATTRIBUTE;
        }
    }
    
    private class SeveralBidBehaviour extends Behaviour {

        private long clock = -1;
        private int step = 0;
        
        public static final int STEP_WAIT_FOR_BID = 0;
        public static final int END = 10;

        @Override
        public void action() {
            _agent.changeStatus(_offer, SellerGUI.STEP_SEVERAL_BID_STATUS);
            switch(step) {
                case STEP_WAIT_FOR_BID:
                    MessageTemplate modele = MessageTemplate.and(MessageTemplate.MatchPerformative(Performatif.TO_BID), MessageTemplate.MatchInReplyTo(_offer.getName()));
                    ACLMessage msg = myAgent.receive(modele);
                    if (msg != null) {
                        switch (msg.getPerformative()) {
                            case Performatif.TO_BID:
                                System.out.println(myAgent.getAID().getName()+ " receive bid from SeveralBid");
                                try {
                                    _agent.newBid(msg.getContentObject());
                                    clock = Calendar.getInstance().getTimeInMillis();
                                } catch (UnreadableException ex) {
                                    System.out.println(myAgent.getAID().getName()+ " can't read bid");
                                }
                                step = END;
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
            
            // initialize delay
            if(clock<0) {
                clock = Calendar.getInstance().getTimeInMillis();
            }
            
            // if waiting too much, end, it will re-announce with higher price
            if(clock > 0 && (clock+_offer.getDelay()<Calendar.getInstance().getTimeInMillis())) {
                _agent.changeStatus(_offer, SellerGUI.STEP_REANNOUNCE_STATUS);
                System.out.println(myAgent.getAID().getName()+ " delay is over, end with several bids");
                step = END;
            }
        }

        @Override
        public boolean done() {
            return (step==END);
        }
        
        @Override
        public int onEnd() {
            clock = -1;
            step = STEP_WAIT_FOR_BID;
            _offer.setPrice(_offer.getPrice()+_offer.getStep());
            return Performatif.TO_ANNOUNCE;
        }
    }
    
    private class AttributeBehaviour extends OneShotBehaviour{

        @Override
        public void action() {
            _agent.changeStatus(_offer, SellerGUI.STEP_ATTRIBUTE_STATUS);
            System.out.println(myAgent.getAID().getName()+ " attribute offer : " + _offer);
            myAgent.addBehaviour(new MessageBehaviour(this.myAgent, Performatif.TO_ATTRIBUTE, new String[]{fishmarket.FishMarket.MARCKET_NAME}, _offer));
        }
        
        @Override
        public int onEnd() {
            return Performatif.TO_GIVE;
        }
    }
    
    private class GiveBehaviour extends Behaviour{

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
                _agent.changeStatus(_offer, SellerGUI.STEP_GIVE_STATUS);
                System.out.println(myAgent.getAID().getName()+ " give product : " + _offer);
                myAgent.addBehaviour(new MessageBehaviour(this.myAgent, Performatif.TO_GIVE, new String[]{fishmarket.FishMarket.MARCKET_NAME}, _offer));
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
    
    private class PayBehaviour extends Behaviour{

        private int step = 0;
        
        public static final int STEP_WAIT_FOR_MONEY_MONEY_MONEY = 0;
        public static final int END = 10;

        @Override
        public void action() {
            switch(step) {
                case STEP_WAIT_FOR_MONEY_MONEY_MONEY:
                    MessageTemplate modele = MessageTemplate.and(MessageTemplate.MatchPerformative(Performatif.TO_PAY), MessageTemplate.MatchInReplyTo(_offer.getName()));
                    ACLMessage msg = myAgent.receive(modele);
                    if (msg != null) {
                        switch (msg.getPerformative()) {
                            case Performatif.TO_PAY:
                                System.out.println(myAgent.getAID().getName()+ " receive money !");
                                _agent.changeStatus(_offer, SellerGUI.STEP_PAY_STATUS);
                                step = END;
                                break;
                            default:
                                break;
                        }
                    }
                    else {
                        block();
                    }
                    break;
                default:
                    break;
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
