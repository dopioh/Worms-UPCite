package fr.upc.g5e4;

import javax.swing.*;

public class Frame extends JFrame{
    // classe pour lancer le jeu -> elle va instancier le GamePanel
    public Frame(){
        int sizeX = 800;
        int sizeY = 600;
        JFrame jeu = new JFrame();
        jeu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jeu.setTitle("Prototype");
        jeu.setSize(sizeX,sizeY);         

        int[] groundY = {200,230,250,280,300,340,370,390,400,410,420,430,450,470};

        GroundModel gM = new GroundModel(groundY,sizeX,sizeY, 500);
        jeu.add(new GamePanel(gM));
        jeu.pack();
        jeu.setLocationRelativeTo(null);
        jeu.setResizable(false);

        // PhysicsWorld world = new PhysicsWorld();
        // world.createGroundFromModel(gM);




        jeu.setVisible(true);
    }

    public static void main(String[] args){
        new Frame();
        
    }
}
