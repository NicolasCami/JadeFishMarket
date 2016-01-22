/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package buyer;

import jade.gui.GuiEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import util.Offer;

public class BuyerGUI extends JFrame implements ActionListener {
    
    private final int               COL_SUBSCRIPTION = 3;
    private final int               COL_DATA = 4;
    private static final String     ACTION_SUBSCRIPTION = "ACTION_SUBSCRIPTION";
    public static final int         EVENT_SUBSCRIPTION = 0;
    private static final String     ACTION_BID = "ACTION_BID";
    public static final int         EVENT_BID = 1;
    private static final String     ACTION_AUTO = "ACTION_AUTO";
    public static final int         EVENT_START = 2;
    private static final String     ACTION_MANUAL = "ACTION_MANUAL";
    public static final String      STEP_START_STATUS = "Choisissez votre mode de fonctionnement (manuel ou automatique).";
    public static final String      STEP_SUB_STATUS = "Sélectionnez les enchères que vous voulez suivre.";
    public static final String      STEP_SUB_DONE_MANUAL_STATUS = "Abonnements effectués. Vous pouvez maintenant faire des propositions en sélectionnant une offre.";
    public static final String      STEP_SUB_DONE_AUTO_STATUS = "Abonnements effectués. L'agent va maintenant faire des propositions automatiquement.";
    public static final String      STEP_BID_STATUS = "Proposition faite.";
    public static final String      STEP_ATTRIBUTE_STATUS = "Enchère gagnée ! Produit attribué.";
    public static final String      STEP_GIVE_STATUS = "Lot reçue.";
    public static final String      STEP_PAY_STATUS = "Paiement envoyé.";

    private final Buyer             _agent;
    private JButton                 _btnPublish;
    private JButton                 _btnQuit;
    private JButton                 _btnSubscription;
    private JButton                 _btnManual;
    private JButton                 _btnAuto;
    private JTextField              _inLimit;
    private JTable                  _table;
    private JScrollPane             _scrollPane;
    private JLabel                  _lbStatus;
    private final String[]          _tableHeader = new String[] {"Vendeur", "Nom du lot", "Prix", "Abonnement", "Data"};

    public BuyerGUI(Buyer a) {
        super();
        _agent = a;
    }
    
    public void addComponentsToPane(final Container pane) {
        setTitle(_agent.getName());
        
        _btnQuit = new JButton("Quitter");
        _btnQuit.addActionListener(this);

        _btnPublish = new JButton("Proposer");
        _btnPublish.setActionCommand(ACTION_BID);
        _btnPublish.addActionListener(this);
        _btnPublish.setEnabled(false);
        
        _btnSubscription = new JButton("Passer aux enchères !");
        _btnSubscription.setActionCommand(ACTION_SUBSCRIPTION);
        _btnSubscription.addActionListener(this);
        _btnSubscription.setEnabled(false);
        
        _btnManual = new JButton("Démarrer en manuel");
        _btnManual.setActionCommand(ACTION_MANUAL);
        _btnManual.addActionListener(this);
        _btnManual.setEnabled(true);
        
        _btnAuto = new JButton("Démarrer en automatique");
        _btnAuto.setActionCommand(ACTION_AUTO);
        _btnAuto.addActionListener(this);
        _btnAuto.setEnabled(true);
        
        _inLimit = new JTextField("1000");
        
        _lbStatus = new JLabel(STEP_START_STATUS);

        DefaultTableModel model = new DefaultTableModel(new Object[0][_tableHeader.length], _tableHeader);
        _table = new JTable(model) {

            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    case COL_DATA:
                        return Offer.class;
                    default:
                        return Boolean.class;
                }
            }
            
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                DefaultTableModel dtm = (DefaultTableModel) getModel();
                Offer offer = (Offer) dtm.getValueAt(row, COL_DATA);
                if(offer.isClosed() && offer.getBuyer().equals(_agent.getLocalName())) {
                    c.setBackground(Color.GREEN);
                    c.setForeground(Color.BLACK);
                }
                else if(offer.isClosed()) {
                    c.setBackground(Color.RED);
                    c.setForeground(Color.BLACK);
                }
                else {
                    c.setBackground(super.getBackground());
                    c.setForeground(super.getForeground());
                }
                return c;
            }
        };
        _table.removeColumn(_table.getColumnModel().getColumn(COL_DATA));
        _table.removeColumn(_table.getColumnModel().getColumn(COL_SUBSCRIPTION));
        _table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _scrollPane = new JScrollPane();
        _scrollPane.setViewportView(_table);
        _scrollPane.setMinimumSize(new Dimension(200, 200));
        
        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(1, 2, 10, 5));
        JPanel footer = new JPanel();
        footer.setLayout(new GridLayout(2, 1, 10, 5));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel form = new JPanel();
        form.setLayout(new GridLayout(1, 4, 10, 5));
        form.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        controls.add(_btnSubscription);
        controls.add(_btnPublish);
        
        footer.add(controls);
        footer.add(_lbStatus);
        
        form.add(_btnManual);
        form.add(new JLabel("Somme limite (euros) :"));
        form.add(_inLimit);
        form.add(_btnAuto);
        
        pane.add(form, BorderLayout.NORTH);
        pane.add(_scrollPane, BorderLayout.CENTER);
        pane.add(footer, BorderLayout.SOUTH);
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
        GuiEvent event;
        switch(e.getActionCommand()) {
            case ACTION_MANUAL:
                _btnManual.setEnabled(false);
                _btnAuto.setEnabled(false);
                _inLimit.setEnabled(false);
                _btnSubscription.setEnabled(true);
                event = new GuiEvent((Object)this, EVENT_START);
                event.addParameter((Object) Buyer.MODE_MANUAL);
                _agent.postGuiEvent(event);
                break;
            case ACTION_AUTO:
                _btnManual.setEnabled(false);
                _btnAuto.setEnabled(false);
                _inLimit.setEnabled(false);
                _btnSubscription.setEnabled(true);
                event = new GuiEvent((Object)this, EVENT_START);
                event.addParameter((Object) Buyer.MODE_AUTO);
                event.addParameter((Object) new Integer(_inLimit.getText()));
                _agent.postGuiEvent(event);
                break;
            case ACTION_SUBSCRIPTION:
                _btnSubscription.setEnabled(false);
                if(_agent.getMode() == Buyer.MODE_MANUAL) {
                    _btnPublish.setEnabled(true);
                }
                event = new GuiEvent((Object)this, EVENT_SUBSCRIPTION);
                event.addParameter((Object) getSubscriptions());
                _agent.postGuiEvent(event);
                break;
            case ACTION_BID:
                if(_table.getSelectedRow() < 0) {
                    message("Sélectionnez une annonce en cliquant dessus dans le tableau avant d'enchérir.");
                }
                else {
                    event = new GuiEvent((Object)this, EVENT_BID);
                    DefaultTableModel dtm = (DefaultTableModel) _table.getModel();
                    event.addParameter((Object) dtm.getValueAt(_table.getSelectedRow(), COL_DATA));
                    _agent.postGuiEvent(event);
                }
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    public void updateTable(List<Offer> offers, List<Offer> subscriptions) {
        List<Offer> save = new ArrayList<>(Arrays.asList(getSubscriptions()));
        Object[][] model = new Object[offers.size()][_tableHeader.length];
        for(int i=0; i<offers.size(); i++) {
            model[i][0] = offers.get(i).getSeller();
            model[i][1] = offers.get(i).getName();
            model[i][2] = offers.get(i).getPrice();
            model[i][COL_SUBSCRIPTION] = subscriptions.contains(offers.get(i)) || save.contains(offers.get(i));
            model[i][COL_DATA] = offers.get(i);
        }

        _table.setModel(new DefaultTableModel(model, _tableHeader));
        _table.removeColumn(_table.getColumnModel().getColumn(COL_DATA));
        if(showOnlySubscriptions()) {
            _table.removeColumn(_table.getColumnModel().getColumn(COL_SUBSCRIPTION));
        }
    }
    
    public Offer[] getSubscriptions() {
        DefaultTableModel dtm = (DefaultTableModel) _table.getModel();
        int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
        int nRowFinal = 0;
        for(int i = 0 ; i < nRow ; i++) {
            if((boolean) dtm.getValueAt(i,COL_SUBSCRIPTION) == true) {
                nRowFinal++;
            }
        }
        Offer[] tableData = new Offer[nRowFinal];
        int index = 0;
        for(int i = 0 ; i < nRow ; i++) {
            if((boolean) dtm.getValueAt(i,COL_SUBSCRIPTION) == true) {
                tableData[index] = (Offer) dtm.getValueAt(i,COL_DATA);
                index++;
            }
        }
       
        return tableData;
    }

    public boolean showOnlySubscriptions() {
        return !_btnSubscription.isEnabled();
    }
    
    public void changeStatus(Offer offer, String s) {
        if(offer != null) {
            _lbStatus.setText(offer.getName() + " : " + s);
        }
        else {
            _lbStatus.setText(s);
        }
    }
}