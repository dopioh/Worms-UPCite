package fr.upc.g5e4.model;


import fr.upc.g5e4.controller.ImputController;
import fr.upc.g5e4.view.*;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Classe qui sert pour tous les graphismes du monde
 */
public class GamePanel extends JPanel {
    private final GroundModel model;
    private final PersonnageModel personnageModel;
    private final PersonnageView personnageView;
    private final ImputController imputController;
    
    private static final float PPM = 30f;

    /**
     * {@code setPreferredSize()} est essentiel sinon rien ne s'affiche 
     * La fenetre casse
     */
    public GamePanel(GroundModel model,PersonnageModel personnageModel, PersonnageView personnageView, ImputController imputController) {
        this.model = model;
        this.personnageModel = personnageModel;
        this.personnageView = personnageView;
        this.imputController = imputController;

        setPreferredSize(new Dimension(model.getWidth(), model.getHeight()));
        setLayout(null); 

        imputController.setBounds(0,0,0,0);
        add(imputController);

        imputController.setFocusable(true);
        imputController.requestFocusInWindow();
    }

    public static float getPPM(){
        return PPM;
    }

    /**
     * L'odre d'ajout pour les elements graphique impacte la superposition des dessins
     * Exemple : 
     * Ciel doit etre placé avant Ground psq il doit pas cacher Ground
     * Il apparait donc derriere et Ground est au premier plan par rapport a Ciel
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Ciel
        g2.setColor(new Color(135, 206, 235));
        g2.fillRect(0, 0, model.getWidth(), model.getHeight());
        
        // Ground
        g2.setColor(new Color(34, 139, 34));
        Path2D path = new Path2D.Double();
        path.moveTo(0, model.getHeight());
        double step = (double) model.getWidth() / (model.getGroundY().length - 1);

        for (int i = 0; i < model.getGroundY().length; i++) {
            path.lineTo(i * step, model.getGroundY()[i]);
        }
        path.lineTo(model.getWidth(), model.getHeight());
        path.closePath();
        g2.fill(path);

        // Worms
        personnageView.draw(g2, personnageModel);

        // Eau
        g2.setColor(new Color(75, 147, 240));
        g2.fillRect(0, model.getWaterLevel(), model.getWidth(), model.getHeight() - model.getWaterLevel());


    }


}
