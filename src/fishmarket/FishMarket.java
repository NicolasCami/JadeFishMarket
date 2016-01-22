package fishmarket;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import util.MessageBehaviour;
import util.Offer;
import util.Performatif;

public class FishMarket extends GuiAgent {
    
    private List<String>                _sellers;
    private List<String>                _buyers;
    private List<Offer>                 _offers;
    transient protected FishMarketGUI   _gui;

    public static final String MARCKET_NAME = "market";
    
    /**********************************
     * AGENT
     **********************************/

    @Override
    protected void setup() {
        System.out.println("Agent "+getAID().getName()+ " is ready.");
        _sellers = new ArrayList<>();
        _buyers = new ArrayList<>();
        _offers = new ArrayList<>();
        
        if(!getLocalName().equals(MARCKET_NAME)) {
            System.out.println("Market must be named : " + MARCKET_NAME);
            doDelete();
            return;
        }
        
        addBehaviour(new FishMarketBehaviour(this));
        
        _gui = new FishMarketGUI(this);
        _gui.deploy();
    }
    
    @Override
    protected void takeDown() {
        _gui.dispose();
        System.out.println("Agent "+ getAID().getName()+ " terminating.");
    }
    
    /*************************************
     * UTILITY
     *************************************/
    
    protected void registerSeller(String name) {    
        _sellers.add(name);
        System.out.println("New seller registered : " + name);
    }
    
    protected void registerBuyer(String name) {
        _buyers.add(name);
        System.out.println("New buyer registered : " + name);
        
        if(_offers.size() > 0) {
            System.out.println("current offers sent to " + name);
            // send all existing offers to the new buyer
            for(Offer offer : _offers) {
                addBehaviour(new MessageBehaviour(this, Performatif.TO_ANNOUNCE, new String[]{name}, offer));
            }
        }
    }

    protected void newAnnounce(Serializable o) {
        Offer offer = (Offer) o;
        
        newAnnounce(offer);
    }
    protected void newAnnounce(Offer offer) {
        if(_offers.contains(offer)) {
            System.out.println("Update offer : " + offer.toString());
            int index = _offers.indexOf(offer);
            _offers.set(index, offer);
        }
        else {
            System.out.println("New offer : " + offer.toString());
            _offers.add(offer);
        }
        if(_buyers.size() > 0) {
            System.out.println("SEND MESSAGES TO : " + Arrays.toString(_buyers.toArray(new String[_buyers.size()])));
            addBehaviour(new MessageBehaviour(this, Performatif.TO_ANNOUNCE, (String[]) _buyers.toArray(new String[_buyers.size()]), offer));
        }
        
        updateGuiList();
    }
    
    protected void newBid(Serializable o) {
        Offer offer = (Offer) o;
        
        if(_offers.contains(offer)) {
            System.out.println("Bid recu pour l'offre : " + offer.toString());
            int index = _offers.indexOf(offer);
            Offer currentOffer = _offers.get(index);
            if(currentOffer.getPrice() == offer.getPrice()) {
                System.out.println("    Bid accepté pour acheteur : " + offer.getBuyer());
                currentOffer.setBuyer(offer.getBuyer());
                _offers.set(index, currentOffer);
                MessageBehaviour msg = new MessageBehaviour(this, Performatif.TO_BID, new String[]{currentOffer.getSeller()}, currentOffer);
                msg.setRealSender(offer.getName());
                addBehaviour(msg);
            }
        }
        else {
            System.out.println("Offre introuvable pour bid : " + offer.toString());
        }
    }
    
    protected void attribute(Serializable o) {
        Offer offer = (Offer) o;
        
        System.out.println("Attribute recu pour l'offre : " + offer.toString());
        if(_buyers.contains(offer.getBuyer())) {
            MessageBehaviour msg = new MessageBehaviour(this, Performatif.TO_ATTRIBUTE, new String[]{offer.getBuyer()}, offer);
            msg.setRealSender(offer.getSeller()+offer.getName());
            addBehaviour(msg);
        }
        else {
            System.out.println("    acheteur introuvable : " + offer.getBuyer());
        }
    }
    
    protected void give(Serializable o) {
        Offer offer = (Offer) o;
        
        System.out.println("Give recu pour l'offre : " + offer.toString());
        if(_buyers.contains(offer.getBuyer())) {
            MessageBehaviour msg = new MessageBehaviour(this, Performatif.TO_GIVE, new String[]{offer.getBuyer()}, offer);
            msg.setRealSender(offer.getSeller()+offer.getName());
            addBehaviour(msg);
        }
        else {
            System.out.println("    acheteur introuvable : " + offer.getBuyer());
        }
    }
    
    protected void pay(Serializable o) {
        Offer offer = (Offer) o;
        
        System.out.println("Pay recu pour l'offre : " + offer.toString());
        if(_sellers.contains(offer.getSeller())) {
            MessageBehaviour msg = new MessageBehaviour(this, Performatif.TO_PAY, new String[]{offer.getSeller()}, offer);
            msg.setRealSender(offer.getName());
            addBehaviour(msg);
            if(_offers.contains(offer)) {
                int index = _offers.indexOf(offer);
                Offer cleanOffer = _offers.get(index);
                cleanOffer.setClosed(true);
                newAnnounce(cleanOffer);
            }
        }
        else {
            System.out.println("    vendeur introuvable : " + offer.getSeller());
        }
    }
    
    /**********************************
     * GUI
     **********************************/
    
    @Override
    protected void onGuiEvent(GuiEvent event) {
        switch(event.getType()) {
            default:
                break;
        }
    }
    
    protected void updateGuiList() {
        _gui.updateTable(_offers);
    }
    
}
