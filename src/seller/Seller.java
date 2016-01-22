/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seller;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import util.Offer;
import util.RegisterBehaviour;

public class Seller extends GuiAgent {
    
    private List<String[]>          _propositions;
    private List<Offer>             _offers;
    transient protected SellerGUI   _gui;
    
    /**********************************
     * AGENT
     **********************************/

    @Override
    protected void setup() {
        System.out.println("Agent "+getAID().getName()+ " is ready.");
        
        _propositions = new ArrayList<>();
        _offers = new ArrayList<>();
        
        addBehaviour(new RegisterBehaviour(this));
        
        _gui = new SellerGUI(this);
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
    
    public void newBid(Serializable o) {
        Offer bid = (Offer) o;

        _propositions.add(new String[]{bid.getName(), Integer.toString(bid.getPrice()), bid.getBuyer()});
        if(_offers.contains(bid)) {
            int index = _offers.indexOf(bid);
            Offer offer = _offers.get(index);
            offer.setBuyer(bid.getBuyer());
            _offers.set(index, offer);
            System.out.println("Update offer : " + offer.toString());
        }
        updateGuiList();
    }
    
    /**********************************
     * GUI
     **********************************/
    
    @Override
    protected void onGuiEvent(GuiEvent event) {
        switch(event.getType()) {
            case SellerGUI.EVENT_PUBLISH:
                Integer price = (Integer) event.getParameter(0);
                Integer delay = (Integer) event.getParameter(1);
                Integer step = (Integer) event.getParameter(2);
                String name = (String) event.getParameter(3);
                Offer offer = new Offer(name, price, delay, step, getLocalName());
                if(_offers.contains(offer)) {
                    _gui.message("Une enchère avec le même nom existe déjà, veuillez changer de nom pour votre nouvelle enchère.");
                }
                else {
                    _offers.add(offer);
                    addBehaviour(new SellerAnnounceBehaviour(this, offer));
                    System.out.println("nouvelle enchere : " + name + " / " + price + " / " + delay + " / " + step);
                }
                
                changeStatus(offer, SellerGUI.STEP_BID_STATUS);
                break;
        }
    }
    
    protected void updateGuiList() {
        _gui.updateTable(_propositions);
    }
    
    public void changeStatus(Offer offer, String status) {
        _gui.changeStatus(offer, status);
    }
    
}
