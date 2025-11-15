package fr.upc.g5e4.view;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;

/* Inventory User Interface se présentera de cette manière
 * 1 partie overlay affichant l'inventaire
 * 1 autre invisible superposée a la première */
public class InventoryUI extends JDialog {
    private final DefaultListModel<InventoryJson> listModel = new DefaultListModel<>();
    private final JList<InventoryJson> list = new JList<>(listModel);
    private final JTextArea details = new JTextArea();


    public InventoryUI(Frame gameFrame, List<InventoryJson> inventory){
        super(gameFrame, "Inventaire", false);  //Mettre a true si besoin de fermer l'inventaire pour jouer
        //Fenetre de jeu + Nom de la fenetre + Jeu en arriere plan

        setLayout(new BorderLayout());  //Choix du layout de la fenetre

        //Remplis
        if(inventory != null){
            for(InventoryJson item : inventory){
                listModel.addElement(item);
            }
        }

        //Liste pour le "centre" de la fenetre
        JScrollPane scroll = new JScrollPane(list);
        add(scroll, BorderLayout.CENTER);           

        //Informations des armes
        details.setEditable(false);
        JScrollPane scrollDetails = new JScrollPane(details);
        add(scrollDetails, BorderLayout.EAST);
        scrollDetails.setPreferredSize(new Dimension(200,0));

        //Interaction (ex: use)
        JPanel bottom = new JPanel();
        add(bottom, BorderLayout.SOUTH);
        JButton use = new JButton("Utiliser");
        JButton close = new JButton("Fermer");
        bottom.setPreferredSize(new Dimension(0,50));
        bottom.add(use); bottom.add(close);

        use.addActionListener(ev ->{             //Si on utilise le bouton use faire ...
            InventoryJson selected = list.getSelectedValue();
            if(selected !=null){
                boolean ok = selected.registerUseThisTurn();
                if(!ok){ //Temporairement plus tard ca ne devrait jamais arriver
                    JOptionPane.showMessageDialog(this,"Action impossible");
                    return;
                }

                showDetails(selected);

                if(!selected.canUseThisTurn()){
                    System.exit(0);    //Changer system exit en setVisible(false) plus tard
                }
            }
        }); 

        close.addActionListener(ev -> System.exit(0));  //Changer system exit en setVisible(false) plus tard

        //Details après clic     recherche des "event" 
        list.addListSelectionListener(ev -> {
            if(!ev.getValueIsAdjusting()){
                InventoryJson selected = list.getSelectedValue();
                if(selected != null){
                    showDetails(selected);
                }
            }
        });

        //Taille
        pack();
        setLocationRelativeTo(gameFrame);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //Ferme le "Dialog" pour ne pas le laisser en mémoire
    }

    private void showDetails(InventoryJson e){      //montre les details 
        StringBuilder sb = new StringBuilder();
        sb.append("Nom : ").append(e.name).append("\n");
        sb.append("Type : ").append(e.objectType).append("\n");
        sb.append("Max usages/Tour : ").append(e.maxUsages).append("\n");
        sb.append("Restant pour ce tour : ").append(e.getRemainingUseThisTurn()).append("\n");
        sb.append("Illimité : ").append(e.isInfinite? "true" : "false").append("\n");    
        if(e.remainingUses != null){
            sb.append("Utilisations restantes : ").append(e.remainingUses).append("\n");
        }
        details.setText(sb.toString());
    }
    
}
