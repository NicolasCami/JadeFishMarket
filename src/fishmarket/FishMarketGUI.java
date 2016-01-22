/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fishmarket;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import util.Offer;

public class FishMarketGUI extends JFrame implements ActionListener {
    
    private final int               COL_DATA = 3;

    private final FishMarket        _agent;
    private JTextField              _inPrice;
    private JButton                 _btnPublish;
    private JButton                 _btnQuit;
    private JButton                 _btnSubscription;
    private JTable                  _table;
    private JScrollPane             _scrollPane;
    private JLabel                  _labelDefaultAmount;
    private final String[]          _tableHeader = new String[] {"Vendeur", "Nom du lot", "Montant courant de l'enchère", "Data"};

    public FishMarketGUI(FishMarket a) {
        super();
        _agent = a;
    }
    
    public void addComponentsToPane(final Container pane) {
        setTitle(_agent.getName());

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
                    default:
                        return Offer.class;
                }
            }
            
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                DefaultTableModel dtm = (DefaultTableModel) getModel();
                Offer offer = (Offer) dtm.getValueAt(row, COL_DATA);
                if(offer.isClosed()) {
                    c.setBackground(Color.BLACK);
                    c.setForeground(Color.WHITE);
                }
                else {
                    c.setBackground(super.getBackground());
                    c.setForeground(super.getForeground());
                }
                return c;
            }
        };
        _scrollPane = new JScrollPane();
        _scrollPane.setViewportView(_table);
        _scrollPane.setMinimumSize(new Dimension(200, 200));
        
        pane.add(_scrollPane, BorderLayout.CENTER);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()) {
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    public void updateTable(List<Offer> offers) {
        Object[][] model = new Object[offers.size()][_tableHeader.length];
        for(int i=0; i<offers.size(); i++) {
            model[i][0] = offers.get(i).getSeller();
            model[i][1] = offers.get(i).getName();
            model[i][2] = offers.get(i).getPrice();
            model[i][COL_DATA] = offers.get(i);
        }

        _table.setModel(new DefaultTableModel(model, _tableHeader));
        _table.removeColumn(_table.getColumnModel().getColumn(COL_DATA));
    }
}