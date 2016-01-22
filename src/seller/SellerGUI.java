/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seller;

import jade.gui.GuiEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import util.Offer;

public class SellerGUI extends JFrame implements ActionListener {

    private static final String     ACTION_PUBLISH = "ACTION_PUBLISH";
    public static final int         EVENT_PUBLISH = 0;
    public static final String      STEP_PUBLISH_STATUS = "Créez votre annonce.";
    public static final String      STEP_BID_STATUS = "En attente de propositions.";
    public static final String      STEP_REANNOUNCE_STATUS = "Ré-annonce. En attente de propositions.";
    public static final String      STEP_ONE_BID_STATUS = "Une seule proposition pour le moment.";
    public static final String      STEP_SEVERAL_BID_STATUS = "Plusieurs porpositions reçues, ré-annonce à venir.";
    public static final String      STEP_ATTRIBUTE_STATUS = "Produit attribué.";
    public static final String      STEP_GIVE_STATUS = "Produit délivré. En attente de paiment.";
    public static final String      STEP_PAY_STATUS = "Paiement reçue. Enchères terminées !";

    private final Seller    _agent;
    private JTextField      _inPrice;
    private JTextField      _inDelay;
    private JTextField      _inStep;
    private JTextField      _inName;
    private JButton         _btnPublish;
    private JButton         _btnQuit;
    private JTable          _table;
    private JScrollPane     _scrollPane;
    private JLabel          _lbStatus;
    private final String[]  _tableHeader = new String[] {"Annonce", "Prix", "Agent preneur"};

    public SellerGUI(Seller a) {
        super();
        _agent = a;
    }
    
    public void addComponentsToPane(final Container pane) {
        setTitle(_agent.getName());
        
        _inPrice = new JTextField("500");
        _inDelay = new JTextField("10");
        _inStep = new JTextField("50");
        _inName = new JTextField("Poisson");

        _btnQuit = new JButton("Quitter");
        _btnQuit.addActionListener(this);

        _btnPublish = new JButton("Créer annonce");
        _btnPublish.setActionCommand(ACTION_PUBLISH);
        _btnPublish.addActionListener(this);
        
        _lbStatus = new JLabel(STEP_PUBLISH_STATUS);

        _table = new JTable(new Object[0][2], _tableHeader);
        _scrollPane = new JScrollPane();
        _scrollPane.setViewportView(_table);
        _scrollPane.setMinimumSize(new Dimension(200, 200));
        
        JPanel form = new JPanel();
        form.setLayout(new GridLayout(2, 4, 10, 5));
        form.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel status = new JPanel();
        status.setLayout(new GridLayout(1, 1, 10, 5));
        status.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // form
        form.add(new JLabel("Nom"));
        form.add(new JLabel("Prix initial (euros)"));
        form.add(new JLabel("Temps d'attente (secondes)"));
        form.add(new JLabel("Pas de variation (euros)"));
        form.add(new JLabel(""));
        form.add(_inName);
        form.add(_inPrice);
        form.add(_inDelay);
        form.add(_inStep);
        form.add(_btnPublish);
        
        //status
        status.add(_lbStatus);
        
        pane.add(form, BorderLayout.NORTH);
        pane.add(_scrollPane, BorderLayout.CENTER);
        pane.add(status, BorderLayout.SOUTH);
    }
     
    /**
     * Create the GUI and show it.  For thread safety,
     * this method is invoked from the
     * event dispatch thread.
     */
    private void createAndShowGUI() {
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponentsToPane(getContentPane());
        pack();
        setVisible(true);
    }
     
    public void deploy() {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    public void message(String mess) {
            JOptionPane.showMessageDialog(this, mess, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            case ACTION_PUBLISH:
                GuiEvent event = new GuiEvent((Object)this, EVENT_PUBLISH);
                Integer delay = new Integer(_inDelay.getText());
                delay *= 1000;
                event.addParameter((Object) new Integer(_inPrice.getText()));
                event.addParameter((Object) delay);
                event.addParameter((Object) new Integer(_inStep.getText()));
                event.addParameter((Object) _inName.getText());
                _agent.postGuiEvent(event);
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    public void updateTable(List<String[]> offers) {
        Object[][] model = new Object[offers.size()][3];
        int i = 0;
	for(String[] entry : offers) {
            model[i][0] = entry[0];
            model[i][1] = entry[1];
            model[i][2] = entry[2];
            i++;
	}

        _table.setModel(new DefaultTableModel(model, _tableHeader));
    }
    
    public void changeStatus(Offer offer, String s) {
        _lbStatus.setText(offer.getName() + " : " + s);
    }

}