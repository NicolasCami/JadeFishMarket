/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package buyer;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import util.MessageBehaviour;
import util.Offer;
import util.Performatif;
import util.RegisterBehaviour;

public class Buyer extends GuiAgent {
    
    public static final int MODE_MANUAL = 0;
    public static final int MODE_AUTO = 1;
    
    private List<Offer>                     _offers;
    private List<Offer>                     _subscriptions;
    private List<BuyerAnnounceBehaviour>    _behaviours;
    transient protected BuyerGUI            _gui;
    private int                             _mode;
    private int                             _bidLimit;
    private boolean                         _started;
    
    /**********************************
     * AGENT
     **********************************/

    @Override
    protected void setup() {

        System.out.println("Agent "+getAID().getName()+ " is ready.");

        _offers = new ArrayList<>();
        _subscriptions = new ArrayList<>();
        _behaviours = new ArrayList<>();
        _mode = MODE_MANUAL;
        _started = false;
        _bidLimit = 0;
        
        addBehaviour(new RegisterBehaviour(this));
        addBehaviour(new BuyerListenerBehaviour(this));
        
        _gui = new BuyerGUI(this);
        _gui.deploy();
    }

    @Override
    protected void takeDown() {
        _gui.dispose();
        System.out.println("Agent "+ getAID().getName()+ " terminating.");
    }
    
    /******************************
     * UTILITY
     ******************************/
    
    protected void newAnnounce(Serializable o) {
        Offer offer = (Offer) o;
        
        // update lists
        if(_offers.contains(offer)) {
            System.out.println(getAID().getName()+ " Update offer : " + o.toString());
            int index = _offers.indexOf(offer);
            _offers.set(index, offer);
            if(_subscriptions.contains(offer)) {
                index = _subscriptions.indexOf(offer);
                _subscriptions.set(index, offer);
                // auto bid
                autoBid(offer);
            }
        }
        else {
            System.out.println(getAID().getName()+ " New offer : " + o.toString());
            _offers.add(offer);
        }
        
        // update GUI
        updateGuiList();
    }
    
    protected void computeSubscriptions(Offer[] subscriptions) {
        _subscriptions.clear();
        _subscriptions.addAll(Arrays.asList(subscriptions));
    }
    
    protected void addBehaviourForSubscriptions() {
        for(Offer offer : _subscriptions) {
            BuyerAnnounceBehaviour behaviour = new BuyerAnnounceBehaviour(this, offer);
            _behaviours.add(behaviour);
            addBehaviour(behaviour);
            
            // auto bid
            autoBid(offer);
        }
    }
    
    protected void autoBid(Offer offer) {
        if(_mode == MODE_AUTO && offer.getPrice() <= _bidLimit && !offer.isClosed()) {
            offer.setBuyer(this.getLocalName());
            addBehaviour(new MessageBehaviour(this, Performatif.TO_BID, new String[]{fishmarket.FishMarket.MARCKET_NAME}, offer));
            changeStatus(offer, BuyerGUI.STEP_BID_STATUS);
        }
    }
    
    public int getMode() {
        return _mode;
    }
    
    /**********************************
     * GUI
     **********************************/
    
    @Override
    protected void onGuiEvent(GuiEvent event) {
        switch(event.getType()) {
            case BuyerGUI.EVENT_START:
                _started = true;
                _mode = (int) event.getParameter(0);
                if(_mode == MODE_AUTO) {
                    _bidLimit = (int) event.getParameter(1);
                }
                changeStatus(null, BuyerGUI.STEP_SUB_STATUS);
                updateGuiList();
                break;
            case BuyerGUI.EVENT_SUBSCRIPTION:
                Offer[] subscriptions = (Offer[]) event.getParameter(0);
                computeSubscriptions(subscriptions);
                addBehaviourForSubscriptions();
                if(_mode == MODE_MANUAL) {
                    changeStatus(null, BuyerGUI.STEP_SUB_DONE_MANUAL_STATUS);
                }
                else {
                    changeStatus(null, BuyerGUI.STEP_SUB_DONE_AUTO_STATUS);
                }
                updateGuiList();
                //System.out.println("montrer les abonnements uniquement : " + _gui.showOnlySubscriptions());
                break;
            case BuyerGUI.EVENT_BID:
                Offer offer = (Offer) event.getParameter(0);
                offer.setBuyer(this.getLocalName());
                addBehaviour(new MessageBehaviour(this, Performatif.TO_BID, new String[]{fishmarket.FishMarket.MARCKET_NAME}, offer));
                changeStatus(offer, BuyerGUI.STEP_BID_STATUS);
                //System.out.println("bid pour offer " + offer.toString());
                break;
        }
    }
    
    protected void updateGuiList() {
        if(_gui.showOnlySubscriptions() && _started) {
            _gui.updateTable(_subscriptions, _subscriptions);
        }
        else {
            _gui.updateTable(_offers, _subscriptions);
        }
    }
    
    public void changeStatus(Offer offer, String status) {
        _gui.changeStatus(offer, status);
    }
    
}
