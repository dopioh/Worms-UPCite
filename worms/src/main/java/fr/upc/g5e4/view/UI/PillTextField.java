package fr.upc.g5e4.view.UI;

import javax.swing.*;
import java.awt.*;

public class PillTextField extends JTextField {
    private Color backgroundColor = new Color(66, 139, 82); // vert 
    //private int radius = 20; //bord arrondis

    public PillTextField(String text){
        super(text);
        setUpStyle();
    }

    public PillTextField(int collums){
        super(collums);
        setUpStyle();
    }

    public void setUpStyle(){
        this.setOpaque(false);
        this.setForeground(Color.WHITE);

        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // border interne

        setFont(new Font("googleSansFont",Font.BOLD,18));
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // eviter les gresillement sur les bords
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight()); // pill pill

        super.paintComponent(g);
        
        g2.dispose();

    }
}
