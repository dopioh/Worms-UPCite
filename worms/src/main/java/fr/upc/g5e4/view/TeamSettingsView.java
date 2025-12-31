package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;

import fr.upc.g5e4.view.UI.*;;

public class TeamSettingsView extends JPanel{

    // Panel 
    private JPanel contentPanel;

    private JPanel zoneChoice;
    private JPanel zoneCustom;

    // Button
    private PillButton saveNextGame;
    private PillButton saveCustom;

    private boolean fullScreen = false;

    public TeamSettingsView(){
        setOpaque(false); // pour que ca chache pas ce qu'il y'a en dessous (transparence)
        setLayout(new GridBagLayout()); // pour que ca soit au dessus ???? idk
        

        initComponents();
        
        /* 
        // Bouton test
        JButton testButton = new JButton("Test");
        testButton.addActionListener(e -> System.out.println("Test appuié"));
        this.add(testButton);
        */
        
    }

    public void initComponents(){
        // CoontentPanel (le truc vert clair en fond la)
        this.contentPanel = new RoundedPanel(60);
        this.contentPanel.setLayout(new GridBagLayout());
        //this.contentPanel.setOpaque(false);
        this.contentPanel.setBackground(new Color(165, 242, 190));
        this.add(contentPanel);

        // ZoneChoice

        this.zoneChoice = new RoundedPanel(50);
        zoneChoice.setLayout(new GridBagLayout());
        this.zoneChoice.setBackground(new Color(117, 161, 131));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        // position dans la grille
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        // le poids ?? pour lui dire de prendre tout l'espace en largeur et hauteur
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;

        // l'ancre ?? pour dire la zone
        gridBagConstraints.anchor = GridBagConstraints.WEST;

        // la marge pour pas que ca colle au bords
        //gridBagConstraints.insets = new Insets(20, 35, 20, 0);

        contentPanel.add(zoneChoice,gridBagConstraints);

        this.zoneCustom = new RoundedPanel(50);
        this.zoneCustom.setLayout(new GridBagLayout());
        this.zoneCustom.setBackground(new Color(117, 161, 131));

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();

        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 0;

        gridBagConstraints2.weightx = 1;
        gridBagConstraints2.weighty = 1;

        gridBagConstraints2.anchor = GridBagConstraints.EAST;

        //gridBagConstraints2.insets = new Insets(20, 0, 20, 35);

        contentPanel.add(zoneCustom,gridBagConstraints2);

        // Button 
        this.saveCustom = new PillButton("SAUVEGARDER"); 
        saveCustom.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();

        gridBagConstraints3.gridx = 0;
        gridBagConstraints3.gridy = 0;

        gridBagConstraints3.weightx = 1;
        gridBagConstraints3.weighty = 1;

        gridBagConstraints3.anchor = GridBagConstraints.SOUTHEAST;

        gridBagConstraints3.insets = new Insets(0, 0, 20, 20);

        zoneCustom.add(saveCustom, gridBagConstraints3);

        this.saveNextGame = new PillButton("SAUVEGARDER"); 
        saveCustom.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();

        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 0;

        gridBagConstraints4.weightx = 1;
        gridBagConstraints4.weighty = 1;

        gridBagConstraints4.anchor = GridBagConstraints.SOUTHEAST;

        gridBagConstraints4.insets = new Insets(0, 0, 20, 20);

        zoneChoice.add(saveNextGame, gridBagConstraints3);
        updateAll(fullScreen);
    }


    /**
     * Fonction update qui est appeler dans l'update de SettingsView pour faciliter
     * @param fullScreen boolean vrai si plein ecran
     */
    public void update(boolean fullScreen){
        setFullScreen(fullScreen);
        updateAll(fullScreen);

    }

    private void updateAll(boolean fullScreen){
        GridBagLayout layout = (GridBagLayout) this.contentPanel.getLayout();

        GridBagConstraints gbcChoice = layout.getConstraints(this.zoneChoice);
        GridBagConstraints gbcCustom = layout.getConstraints(this.zoneCustom);

        if (fullScreen){
            this.contentPanel.setPreferredSize(new Dimension(2200, 1150));
            this.zoneChoice.setPreferredSize(new Dimension(1000, 1050)); 
            this.zoneCustom.setPreferredSize(new Dimension(1000, 1050)); 

            this.saveCustom.setPreferredSize(new Dimension(400, 100));
            this.saveCustom.setFont(new Font("googleSansFont", Font.BOLD, 30));

            this.saveNextGame.setPreferredSize(new Dimension(400, 100));
            this.saveNextGame.setFont(new Font("googleSansFont", Font.BOLD, 30));

            gbcChoice.insets = new Insets(20, 70, 20, 0);
            gbcCustom.insets = new Insets(20, 0, 20, 70);
            
        }else{  
            this.contentPanel.setPreferredSize(new Dimension(1100,550));
            this.zoneChoice.setPreferredSize(new Dimension(500, 500));
            this.zoneCustom.setPreferredSize(new Dimension(500, 500));

            this.saveCustom.setPreferredSize(new Dimension(200, 50));
            this.saveCustom.setFont(new Font("googleSansFont", Font.BOLD, 18));

            this.saveNextGame.setPreferredSize(new Dimension(200, 50));
            this.saveNextGame.setFont(new Font("googleSansFont", Font.BOLD, 18));

            gbcChoice.insets = new Insets(20, 35, 20, 0);
            gbcCustom.insets = new Insets(20, 0, 20, 35);
        }

        layout.setConstraints(this.zoneChoice, gbcChoice);
        layout.setConstraints(this.zoneCustom, gbcCustom);

        this.revalidate(); // pour recalculer le layout
        this.repaint();    
    }

    public void setFullScreen(boolean fullScreen){
        this.fullScreen = fullScreen;
    }
    
}
