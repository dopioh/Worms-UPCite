package fr.upc.g5e4.view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import fr.upc.g5e4.view.UI.*;
import fr.upc.g5e4.controller.PlayerDataController;
//import fr.upc.g5e4.model.SkinModel;
import fr.upc.g5e4.model.io.CustomTeamData;

public class TeamSettingsView extends JPanel{

    // Panel 
    private JPanel contentPanel;

    private JPanel zoneChoice;
    private JPanel zoneCustom;

    // Panel conteneur des teams dynamique dans Zone Choice
    private JPanel dynamicTeamContainer;

    // Button
    private PillButton saveNextGame;
    private PillButton saveCustom;

    // Label
    private ArrayList<JLabel> allLabels;
    private ArrayList<JLabel> allLabelsW;

    // liste deroulante
    private ArrayList<PillComboBox> allPillComboBox; // utile seulement pour resize fullScreen

    // Donnees a transferer 
    private PillComboBox<Integer> nbrEquipe;
    private PillComboBox<Integer> nbrWormsGame;
    private ArrayList<PillComboBox> nomTeams;


    // Zone Custom 
    private PillTextField nomEquipeCustom;
    private ArrayList<String> skinChoose; // faut que ca affiche que les skinModel unlocked
    private PillComboBox skinChooseUnlocked;

    private JLabel skinPreviewImage;
    private HashMap<String,Image> imagesLoad; 

    // noms des 6 worms a custom
    private ArrayList<PillTextField> allWormsName;

    // Pour resize les noms de worms et noms d'equipes

    private ArrayList<PillTextField> allChooseResizeText;
    private ArrayList<PillComboBox> allChooseResizeBOX;



    private boolean fullScreen = false;
    private PlayerDataController playerDataController;

    public TeamSettingsView(PlayerDataController playerDataController){
        this.playerDataController = playerDataController;
        setOpaque(false); // pour que ca chache pas ce qu'il y'a en dessous (transparence)
        setLayout(new GridBagLayout()); // pour que ca soit au dessus ???? idk

        this.allLabels = new ArrayList<>();
        this.allLabelsW = new ArrayList<>();
        this.allWormsName = new ArrayList<>();
        this.allPillComboBox = new ArrayList<>();
        this.allChooseResizeText = new ArrayList<>();
        this.allChooseResizeBOX = new ArrayList<>();
        this.nomTeams = new ArrayList<>();

        this.skinChoose = playerDataController.getPlayerData().onlyUnlocked();
        this.imagesLoad = new HashMap<>();
        loadSkins(); // on charge toutes les images d'un coup, comme ca on peut y acceder direct
        
        

        initComponents();
        
        /* 
        // Bouton test
        JButton testButton = new JButton("Test");
        testButton.addActionListener(e -> System.out.println("Test appuié"));
        this.add(testButton);
        */
        
    }

    public void initComponents(){
    // ContentPanel (le truc vert clair en fond)
    this.contentPanel = new RoundedPanel(60);
    this.contentPanel. setLayout(new GridBagLayout());
    this.contentPanel. setBackground(new Color(165, 242, 190));
    this.add(contentPanel);

    // ZONE CHOICE 
    this.zoneChoice = new RoundedPanel(50);
    zoneChoice.setLayout(new GridBagLayout());
    this.zoneChoice.setBackground(new Color(117, 161, 131));

    GridBagConstraints gbcZoneChoice = new GridBagConstraints();
    gbcZoneChoice.gridx = 0;
    gbcZoneChoice.gridy = 0;
    gbcZoneChoice. weightx = 1;
    gbcZoneChoice.weighty = 1;
    gbcZoneChoice.anchor = GridBagConstraints.WEST;
    contentPanel.add(zoneChoice, gbcZoneChoice);

    //ZONE CUSTOM
    this.zoneCustom = new RoundedPanel(50);
    this.zoneCustom.setLayout(new GridBagLayout());
    this.zoneCustom.setBackground(new Color(117, 161, 131));

    GridBagConstraints gbcZoneCustom = new GridBagConstraints();
    gbcZoneCustom. gridx = 1;
    gbcZoneCustom.gridy = 0;
    gbcZoneCustom.weightx = 1;
    gbcZoneCustom.weighty = 1;
    gbcZoneCustom.anchor = GridBagConstraints.EAST;
    contentPanel.add(zoneCustom, gbcZoneCustom);

    // PANEL CONTENEUR DYNAMIQUE
    this.dynamicTeamContainer = new JPanel();
    this.dynamicTeamContainer.setOpaque(false);
    this.dynamicTeamContainer.setLayout(new GridBagLayout()); 

    GridBagConstraints gbcContainer = new GridBagConstraints();
    gbcContainer.gridx = 0;
    gbcContainer.gridy = 2; 
    gbcContainer.gridwidth = 2; 
    gbcContainer.weightx = 1;
    gbcContainer.weighty = 1;
    gbcContainer.fill = GridBagConstraints.HORIZONTAL;
    gbcContainer.insets = new Insets(70, 0,0, 10);
    zoneChoice.add(dynamicTeamContainer, gbcContainer);

    // ZONE CHOICE : CONTENU
    GridBagConstraints gbc1 = new GridBagConstraints();
    
    // titre : prend 2 colonnes
    gbc1.gridx = 0;
    gbc1.gridy = 0;
    gbc1.gridwidth = 2;  // Prend colonnes 0 et 1
    gbc1.anchor = GridBagConstraints. NORTHWEST;
    gbc1.insets = new Insets(20, 18, 20, 10);
    zoneChoice.add(createLabel("Equipes pour la prochaine partie :"), gbc1);

    // label "Nombre d'equipes"
    gbc1.gridx = 0;
    gbc1.gridy = 1;
    gbc1.gridwidth = 1;  // Remet à 1 colonne
    zoneChoice.add(createLabel("Nombre d'equipes :"), gbc1);

    // ComboBox nombre equipes
    Integer[] nbrEquipeMax = {2, 3, 4, 5, 6};
    this.nbrEquipe = new PillComboBox<>(nbrEquipeMax);
    gbc1.gridx = 1;
    gbc1.gridy = 1;
    gbc1.anchor = GridBagConstraints. NORTHEAST;
    gbc1.insets = new Insets(10, 10, 10, 30);
    this.allPillComboBox. add(nbrEquipe);
    zoneChoice.add(nbrEquipe, gbc1);

    // label "Nombre de Worm"
    gbc1.gridx = 0;
    gbc1.gridy = 2;
    gbc1.anchor = GridBagConstraints. NORTHWEST;
    gbc1.insets = new Insets(20, 18, 20, 10);
    zoneChoice.add(createLabel("Nombre de Worm Par Equipe :"), gbc1);

    // ComboBox nombre worms
    Integer[] nbrWormsMax = {1, 2, 3, 4, 5, 6};
    this.nbrWormsGame = new PillComboBox<>(nbrWormsMax);
    gbc1.gridx = 1;
    gbc1.gridy = 2;
    gbc1.anchor = GridBagConstraints.NORTHEAST;
    gbc1.insets = new Insets(10, 10, 10, 30);
    this.allPillComboBox.add(nbrWormsGame);
    zoneChoice.add(nbrWormsGame, gbc1);

    // Bouton SAUVEGARDER zoneChoice
    this.saveNextGame = new PillButton("SAUVEGARDER");
    //this.saveNextGame.setPreferredSize(new Dimension(170, 45));
    gbc1.gridx = 0;
    gbc1.gridy = 3;
    gbc1.weightx = 1;
    gbc1.weighty = 0;
    gbc1.gridwidth = 2;
    gbc1.anchor = GridBagConstraints.SOUTHEAST;
    //gbc1.fill = GridBagConstraints.HORIZONTAL;
    gbc1.insets = new Insets(0, 0, 20, 20);
    zoneChoice.add(saveNextGame, gbc1);

    // ZONE CUSTOM : CONTENU 
    GridBagConstraints gbc2 = new GridBagConstraints();

    // titre : PREND 3 COLONNES
    gbc2.gridx = 0;
    gbc2.gridy = 0;
    gbc1.weightx = 1;
    gbc2.gridwidth = 3;  // Prend colonnes 0, 1 et 2
    gbc2.anchor = GridBagConstraints.NORTHWEST;
    gbc2.insets = new Insets(20, 18, 20, 10);
    zoneCustom.add(createLabel("Creer une equipe personnalisee : "), gbc2);

    // label "Nom de l'Equipe"
    gbc2.gridx = 0;
    gbc2.gridy = 1;
    gbc2.gridwidth = 1;  // Remet à 1 colonne
    zoneCustom.add(createLabel("Nom de l'Equipe :"), gbc2);

    // textField nom equipe
    this.nomEquipeCustom = new PillTextField(10);
    gbc2.gridx = 1;
    gbc2.gridy = 1;
    gbc2.gridwidth = 2;  // Prend 2 colonnes pour plus d'espace
    gbc2.weightx = 0;  // IMPORTANT : prend l'espace disponible
    gbc2.fill = GridBagConstraints. HORIZONTAL;
    gbc2.anchor = GridBagConstraints.NORTHEAST;
    gbc2.insets = new Insets(15, 10, 10, 25);
    zoneCustom.add(nomEquipeCustom, gbc2);

    // label "Skin"
    gbc2.gridx = 0;
    gbc2.gridy = 2;
    gbc2.gridwidth = 1;
    gbc2.anchor = GridBagConstraints.NORTHWEST;
    gbc2.fill = GridBagConstraints.NONE;
    gbc2.insets = new Insets(20, 18, 20, 10);
    zoneCustom.add(createLabel("Skin : "), gbc2);

    // preview Skin (image)
    this.skinPreviewImage = new JLabel();
    gbc2.gridx = 1;
    gbc2.gridy = 2;
    gbc2.gridwidth = 1;
    gbc2.anchor = GridBagConstraints.CENTER;
    gbc2.insets = new Insets(10, 10, 10, 20);
    zoneCustom.add(skinPreviewImage, gbc2);

    // comboBox choix skin
    String[] skinModelUnlockedTab = skinChoose.toArray(new String[0]);
    this.skinChooseUnlocked = new PillComboBox<>(skinModelUnlockedTab);
    //this.skinChooseUnlocked.setPreferredSize(new Dimension(180, 40)); // Taille minimum
    gbc2.gridx = 2;
    gbc2.gridy = 2;
    gbc2.gridwidth = 1;
    gbc2.weightx = 0.7;  // Plus de poids pour la combobox
    gbc2.fill = GridBagConstraints.NONE;  // S'étire horizontalement
    gbc2.anchor = GridBagConstraints.WEST;
    gbc2.insets = new Insets(10, 0, 10, 25);
    zoneCustom.add(skinChooseUnlocked, gbc2);

    // bouton SAUVEGARDER zoneCustom
    this.saveCustom = new PillButton("SAUVEGARDER");
    gbc2.gridx = 2;
    gbc2.gridy = 9;
    gbc2.weightx = 1;
    gbc2.weighty = 1;
    gbc2.anchor = GridBagConstraints.SOUTHEAST;
    gbc2.fill = GridBagConstraints.NONE;
    gbc2.insets = new Insets(0, 0, 20, 20);
    zoneCustom.add(saveCustom, gbc2);

    // noms worms numero 

    GridBagConstraints gbc3 = new GridBagConstraints();

    // titre : PREND 3 COLONNES
    gbc3.gridx = 0;
    gbc3.gridy = 3;
    gbc3.weightx = 1;
    gbc3.gridwidth = 3;  // Prend colonnes 0, 1 et 2
    gbc3.anchor = GridBagConstraints.NORTHWEST;
    gbc3.insets = new Insets(7, 18, 7, 10);
    zoneCustom.add(createLabelW("Worms 1 : "), gbc3);
    gbc3.gridy = 4;
    zoneCustom.add(createLabelW("Worms 2 : "), gbc3);    
    gbc3.gridy = 5;
    zoneCustom.add(createLabelW("Worms 3 : "), gbc3);
    gbc3.gridy = 6;
    zoneCustom.add(createLabelW("Worms 4 : "), gbc3);
    gbc3.gridy = 7;
    zoneCustom.add(createLabelW("Worms 5 : "), gbc3);
    gbc3.gridy = 8;
    zoneCustom.add(createLabelW("Worms 6 : "), gbc3);

    // NOMS WORMS : -> on va creer une fonction pour les creer plus rapidement
    for (int i = 3 ; i < 9 ; i++){
        setUpWormsNameTextField(i);
    }

    updateAll(fullScreen);

    setUpTeamCustomChoice(getNbrEquipe());
}


    /**
     * Fonction update qui est appeler dans l'update de SettingsView pour faciliter
     * @param fullScreen boolean vrai si plein ecran
     */
    public void update(boolean fullScreen){
        setFullScreen(fullScreen);
        this.dynamicTeamContainer.removeAll();
        setUpTeamCustomChoice(getNbrEquipe());
        updateAll(fullScreen);
    }

    private void updateAll(boolean fullScreen){
        GridBagLayout layout = (GridBagLayout) this.contentPanel.getLayout();

        GridBagConstraints gbcChoice = layout.getConstraints(this.zoneChoice);
        GridBagConstraints gbcCustom = layout.getConstraints(this.zoneCustom);

        String currentSkin = (String) skinChooseUnlocked.getSelectedItem();

        if (fullScreen){
            // on fait un scale pour que le fullscreen s'adapte a chaque ecran
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int screenW = screenSize.width;
            int screenH = screenSize.height;

            float scaleW = screenW/2560f;
            float scaleH = screenH/1440f; // mon ecran actuel

            float globalScale = Math.min(scaleW, scaleH);

            //  (int)(1000 * globalScale), (int)(1050 * globalScale)

            this.contentPanel.setPreferredSize(new Dimension((int)(2200 * globalScale), (int)(1150 * globalScale)));
            this.zoneChoice.setPreferredSize(new Dimension((int)(1000 * globalScale), (int)(1050 * globalScale))); 
            this.zoneCustom.setPreferredSize(new Dimension((int)(1000 * globalScale), (int)(1050 * globalScale))); 
            this.dynamicTeamContainer.setPreferredSize(new Dimension((int)(500 * globalScale), (int)(500 * globalScale)));

            Dimension bigBtnSize = new Dimension((int)(400 * globalScale), (int)(100 * globalScale));
            this.saveCustom.setPreferredSize(bigBtnSize); // on met tout comme ca on est sure que il se redimensionne bien
            this.saveCustom.setMinimumSize(bigBtnSize);
            this.saveCustom.setMaximumSize(bigBtnSize); 
            this.saveCustom.setFont(new Font("googleSansFont", Font.BOLD, (int)(30*globalScale)));

            this.saveNextGame.setPreferredSize(new Dimension(400, 100));
            this.saveNextGame.setFont(new Font("googleSansFont", Font.BOLD,(int)(30*globalScale)));

            gbcChoice.insets = new Insets(20, 70, 20, 0);
            gbcCustom.insets = new Insets(20, 0, 20, 70);

            this.nomEquipeCustom.setPreferredSize(new Dimension(500, 80));
            this.nomEquipeCustom.setFont(new Font("googleSansFont", Font.BOLD, (int)(40*globalScale)));

            Font labelFullScreen = new Font("googleSansFont", Font.BOLD, (int)(40*globalScale));

            this.skinChooseUnlocked.setPreferredSize( new Dimension((int)(600 * globalScale), (int)(100 * globalScale)));
            skinChooseUnlocked.setFont(labelFullScreen);

            int imgSize = (int)(100*globalScale); 
            this.skinPreviewImage.setPreferredSize(new Dimension(imgSize, imgSize));
            updateSkin(currentSkin, imgSize);

            for (JLabel label : this.allLabels){
                label.setFont(labelFullScreen);
            }

            for (JLabel label : this.allLabelsW){
                label.setFont(labelFullScreen);
            }

            for (PillComboBox<Object> pill : allPillComboBox){
                pill.setPreferredSize(new Dimension((int)(200 * globalScale), (int)(80 * globalScale)));
                pill.setFont(labelFullScreen);
            }

            Dimension d = new Dimension((int)(40 * globalScale), (int)(90 * globalScale));
            for (PillTextField ptf : this.allChooseResizeText){
                ptf.setPreferredSize(d);
                ptf.setMaximumSize(d);
                ptf.setMinimumSize(d);
                ptf.setFont(labelFullScreen);
            }

            for (PillComboBox pcb : allChooseResizeBOX){
                pcb.setPreferredSize(d);
                pcb.setMaximumSize(d);
                pcb.setMinimumSize(d);
                pcb.setFont(labelFullScreen);
            }

            //Dimension bigImage = new Dimension(500, 500); -> casse tout
            //this.skinPreviewImage.setPreferredSize(bigImage);
            //this.skinPreviewImage.setMinimumSize(bigImage);
            //this.skinPreviewImage.setMaximumSize(bigImage);
            //this.skinPreviewImage.setPreferredSize(new Dimension(500, 500)); // ne marche pas
            
            
        }else{  

            this.contentPanel.setMaximumSize((new Dimension(1100,550))); 
            this.zoneChoice.setMaximumSize((new Dimension(500, 500)));
            this.zoneCustom.setMaximumSize((new Dimension(500, 500)));
            this.dynamicTeamContainer.setMaximumSize((new Dimension(250,500)));

            this.contentPanel.setMinimumSize(new Dimension(1100,550)); 
            this.zoneChoice.setMinimumSize(new Dimension(500, 500));
            this.zoneCustom.setMinimumSize(new Dimension(500, 500));
            this.dynamicTeamContainer.setMinimumSize(new Dimension(250,500));

            this.contentPanel.setPreferredSize(new Dimension(1100,550));
            this.zoneChoice.setPreferredSize(new Dimension(500, 500));
            this.zoneCustom.setPreferredSize(new Dimension(500, 500));
            this.dynamicTeamContainer.setPreferredSize(new Dimension(250,500));

            Dimension smallBtnSize = new Dimension(200, 50);
            this.saveCustom.setPreferredSize(smallBtnSize);
            this.saveCustom.setMinimumSize(smallBtnSize);
            this.saveCustom.setMaximumSize(smallBtnSize);
            this.saveCustom.setFont(new Font("googleSansFont", Font.BOLD, 18));

            this.saveNextGame.setPreferredSize(new Dimension(200, 50));
            this.saveNextGame.setFont(new Font("googleSansFont", Font.BOLD, 18));

            gbcChoice.insets = new Insets(20, 35, 20, 0);
            gbcCustom.insets = new Insets(20, 0, 20, 35);

            this.nomEquipeCustom.setPreferredSize(new Dimension(70, 50));
            this.nomEquipeCustom.setFont(new Font("googleSansFont", Font.BOLD, 18));

            Font labelFullScreen = new Font("googleSansFont", Font.BOLD, 20);
            Font labelFullScreenW = new Font("googleSansFont", Font.BOLD, 15);

            this.skinChooseUnlocked.setPreferredSize( new Dimension(220, 50));
            skinChooseUnlocked.setFont(new Font("googleSansFont", Font.BOLD, 17));

            int imgSize = 50; 
            this.skinPreviewImage.setPreferredSize(new Dimension(imgSize, imgSize));
            updateSkin(currentSkin, imgSize);

            for (JLabel label : this.allLabels){
                label.setFont(labelFullScreen);
            }

            for (JLabel label : this.allLabelsW){
                label.setFont(labelFullScreenW);
            }

            Font pillFullScreen = new Font("googleSansFont", Font.BOLD, 17);

            for (PillComboBox<Object> pill : allPillComboBox){
                pill.setPreferredSize(new Dimension(120, 50));
                pill.setFont(pillFullScreen);
            }

            //Dimension bigImage = new Dimension(50, 50); 
            //this.skinPreviewImage.setPreferredSize(bigImage);
            //this.skinPreviewImage.setMinimumSize(bigImage);
            //this.skinPreviewImage.setMaximumSize(bigImage);

            Dimension d = new Dimension(40,35);
            for (PillTextField ptf : this.allChooseResizeText){
                ptf.setPreferredSize(d);
                ptf.setMaximumSize(d);
                ptf.setMinimumSize(d);
                ptf.setFont(pillFullScreen);
            }

            for (PillComboBox pcb : allChooseResizeBOX){
                pcb.setPreferredSize(d);
                pcb.setMaximumSize(d);
                pcb.setMinimumSize(d);
                pcb.setFont(labelFullScreen);
            }
        }

        layout.setConstraints(this.zoneChoice, gbcChoice);
        layout.setConstraints(this.zoneCustom, gbcCustom);


        this.zoneChoice.revalidate();
        this.zoneCustom.revalidate();
        this.contentPanel.revalidate();

        this.revalidate(); // pour recalculer le layout
        this.repaint();    

    }

    public JLabel createLabel(String text){
        JLabel label = new JLabel(text);
        //label.setFont(new Font("googleSansFont",Font.BOLD, 20));
        label.setForeground(Color.WHITE);

        allLabels.add(label);

        return label;
    }

    public JLabel createLabelW(String text){
        JLabel label = new JLabel(text);
        //label.setFont(new Font("googleSansFont",Font.BOLD, 20));
        label.setForeground(Color.WHITE);

        allLabelsW.add(label);

        return label;
    }
 
    public PillComboBox<Object> createPillComboBox(Object[] items){
        PillComboBox<Object> pillpill = new PillComboBox<>(items);
        this.allPillComboBox.add(pillpill);
        return pillpill;

    }

    private void loadSkins(){
        for (String nom : skinChoose){
            imagesLoad.put(nom,laodIcon(playerDataController.getPlayerData().findSkinModelByName(nom).getFilePath()));
            System.out.println(nom);
        }
    }

    private Image laodIcon(String filePath){
        try {
            URL imgUrl = getClass().getResource(filePath);
            //ImageIcon ingIcon = new ImageIcon(imgUrl);
            //Image img = ingIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            ImageIcon tempIcon = new ImageIcon(imgUrl);
            return tempIcon.getImage();
        } catch (Exception e) {
            System.out.println("Le skin n'a pas pu etre load, le skin de base va s'afficher a la place..");
            URL imgUrl = getClass().getResource("/wormPixel.png");
            ImageIcon tempIcon = new ImageIcon(imgUrl);
            return tempIcon.getImage();
        }
    }

    public void updateSkin(String skinName, int size){
        //skinPreviewImage.setIcon(imagesLoad.get(skinName));
        Image rawImg = imagesLoad.get(skinName);
        if (rawImg != null) {
            Image scaledImg = rawImg.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            skinPreviewImage.setIcon(new ImageIcon(scaledImg));
        }
    }

    private void setUpWormsNameTextField(int gridY){
        GridBagConstraints gbcWorms = new GridBagConstraints();
        PillTextField worm = new PillTextField(10);
        gbcWorms.gridx = 2;
        gbcWorms.gridy = gridY;
        gbcWorms.weightx = 0;
        gbcWorms.gridwidth = 2;
        gbcWorms.anchor = GridBagConstraints.EAST;
        gbcWorms.fill = GridBagConstraints.HORIZONTAL;
        gbcWorms.insets = new Insets(0, 0, 2, 20);

        allChooseResizeText.add(worm);
        allWormsName.add(worm);
        zoneCustom.add(worm, gbcWorms);
    }

    private void setUpTeamCustomChoice(int nbrEquipe){
        this.dynamicTeamContainer.removeAll();
        nomTeams.clear();

        ArrayList<CustomTeamData> allCustomTeams = playerDataController.getPlayerData().getCustomTeamManager().getListCustomTeamData();
        CustomTeamData[] tabCustomTeamData = (CustomTeamData[]) allCustomTeams.toArray(new CustomTeamData[0]);
        String[] nomCustomTeam = new String[tabCustomTeamData.length];

        GridBagConstraints gbcTeam = new GridBagConstraints();
        gbcTeam.insets = new Insets(10, 10, 10, 20);
        for (int i = 0 ; i < tabCustomTeamData.length ; i++){
            nomCustomTeam[i] = tabCustomTeamData[i].getTeamName();
        }
        for (int i = 0 ; i < nbrEquipe ; i++){

        // Partie Droite (PillComboBox);
        PillComboBox teamCustom = new PillComboBox<>(nomCustomTeam);
        gbcTeam.gridx = 1;
        gbcTeam.gridy = i ;
        gbcTeam.weightx = 1;
        //gbcTeam.gridwidth = 3;
        gbcTeam.anchor = GridBagConstraints.EAST;
        gbcTeam.fill = GridBagConstraints.HORIZONTAL;
        gbcTeam.insets = new Insets(2, 0, 2, 10);

        if (fullScreen){
            teamCustom.setPreferredSize(new Dimension(250, 50));
            teamCustom.setFont(new Font("googleSansFont", Font.BOLD, 20));
            gbcTeam.insets = new Insets(15, 0, 15, 10);
        } else {
            teamCustom.setPreferredSize(new Dimension(150, 30));
            teamCustom.setFont(new Font("googleSansFont", Font.BOLD, 12));
            gbcTeam.insets = new Insets(0, 0, 2, 10);
        }

        //allChooseResizeBOX.add(teamCustom);
        nomTeams.add(teamCustom);
        dynamicTeamContainer.add(teamCustom,gbcTeam);
        //this.allPillComboBox.add(teamCustom);

        // Partie Gauche (JLabel Text)
        int temp = i + 1;
        JLabel customTeam = new JLabel("Custom team pour l'equipe " + temp + " :     "); // on va mettre des espaces au lieu de separer gridContraint pour tricher un peu..
        customTeam.setForeground(Color.WHITE);
        gbcTeam.gridx = 0;
        gbcTeam.weightx = 0;
        //gbcTeam.gridwidth = 2;
        gbcTeam.anchor = GridBagConstraints.WEST;
        gbcTeam.insets = new Insets(0, 20, 0, 0);
        //gbcTeam.fill = GridBagConstraints.HORIZONTAL;

        if (fullScreen){
            customTeam.setFont(new Font("googleSansFont", Font.BOLD, 40));
            //gbcTeam.insets = new Insets(20, 20, 20, 0);
        } 

        this.dynamicTeamContainer.revalidate();
        this.dynamicTeamContainer.repaint();

        allLabelsW.add(customTeam);
        dynamicTeamContainer.add(customTeam, gbcTeam);

        zoneChoice.revalidate();
        zoneChoice.repaint();

        this.revalidate();
        this.repaint();

        // sert a rien de mettre recalculate/repaint ici
        }
    }

    public void reloadCustomTeamChoose(){
        int currentNbEquipes = this.getNbrEquipe();

        setUpTeamCustomChoice(currentNbEquipes);
    }

    public void setFullScreen(boolean fullScreen){
        this.fullScreen = fullScreen;
    }

    // Getter Data
    public int getNbrEquipe(){
        return (int) this.nbrEquipe.getSelectedItem();
    }

    public int getWormByTeam(){
        return (int) this.nbrWormsGame.getSelectedItem(); 
    }

    // Getter Button pour action listener
    public JButton getSaveNextGameButton(){
        return this.saveNextGame;
    }

    public PillComboBox getSkinPillButton(){
        return this.skinChooseUnlocked;
    }

    public boolean getFullScreen(){
        return this.fullScreen;
    }

    public PillComboBox<Integer> getChangeNumberTeam(){
        return this.nbrEquipe;
    }

    // DATA CUSTOM
    public String getTeamName(){
        return this.nomEquipeCustom.getText();
    }


    public String getSkinChooseSTR(){
        return (String) this.skinChooseUnlocked.getSelectedItem();
    }

    public ArrayList<String> getAllWormsName(){
        ArrayList<String> result = new ArrayList<>();
        for (PillTextField wormName : allWormsName){
            String temp = wormName.getText();
            System.out.println(temp);
            if (temp == null || temp.equals("") || temp.equals(" ")){
                System.out.println("TEMP EST NULL");
                result.add("bot");
            } else {
                result.add(temp);
            }
        }
        return result;
    }

    public JButton getSaveCustom(){
        return this.saveCustom;
    }

    public ArrayList<String> getAllCustomByTeam(){
        ArrayList<String> res = new ArrayList<>();
        for (int i = 0 ; i < nomTeams.size() ; i ++){
            String s = (String) nomTeams.get(i).getSelectedItem();
            res.add(s);
        }
        return res;
    }

    
}
