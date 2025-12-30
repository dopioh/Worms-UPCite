package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;

public class TeamSettingsView extends JPanel{

    private JPanel contentPanel;

    private JPanel zoneChoice;
    private JPanel zoneCustom;

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
        this.contentPanel = new JPanel();
        this.contentPanel.setLayout(new BorderLayout());
        this.contentPanel.setOpaque(true);
        if (fullScreen){
            this.contentPanel.setPreferredSize(new Dimension(1900, getHeight()));
        }else{  
            this.contentPanel.setPreferredSize(new Dimension(300, 200));
        }
        this.contentPanel.setBackground(new Color(0,0,255));
        this.add(contentPanel);

        this.revalidate(); // Recalcule le layout
        this.repaint();    // Redessine les pixels
    }


    /**
     * Fonction update qui est appeler dans l'update de SettingsView pour faciliter
     * @param fullScreen boolean vrai si plein ecran
     */
    public void update(boolean fullScreen){
        setFullScreen(fullScreen);
        setSizeComponent(fullScreen);

    }

    private void setSizeComponent(boolean fullScreen){
        if (fullScreen){
            this.contentPanel.setPreferredSize(new Dimension(1900, 800));
        }else{  
            this.contentPanel.setPreferredSize(new Dimension(300, 200));
        }
    }

    public void setFullScreen(boolean fullScreen){
        this.fullScreen = fullScreen;
    }
    
}
