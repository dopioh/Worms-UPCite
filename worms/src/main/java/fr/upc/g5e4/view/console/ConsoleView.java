package fr.upc.g5e4.view.console;

import java.io.IOException;
import java.util.Random;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import fr.upc.g5e4.model.CharacterModel;

import java.awt.Font;

import javax.swing.JFrame;

/**
 * Gere l'ecran du terminal externe
 * Pas possible d'avoir ca dans le terminal machine 
 * A cause de windows qui veut qu'on utilise javaw
 * Possibiliter de le regler au SPRINT 3 si necessaire
 * @see https://github.com/mabe02/lanterna/issues/335
 */
public class ConsoleView {
    private Terminal terminal;
    private Screen screen;
    private TextGraphics text;
    private static final int XCase = 100;
    private static final int YCase = 28;

    /**
     * Initialise la fenetre et tout ce qui est overlay
     *  -> le menu, les stats etc..
     */
    public ConsoleView(){
        try {
            Font maPolice = new Font("Monospaced", Font.BOLD, 20);

            DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();   // pour regler prbnlm windows
            defaultTerminalFactory.setForceTextTerminal(false);          // force lanterna a cree sa propre fenetre du terminal
            defaultTerminalFactory.setPreferTerminalEmulator(true);

            defaultTerminalFactory.setTerminalEmulatorTitle("Terminal");
            defaultTerminalFactory.setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.newInstance(maPolice));
            defaultTerminalFactory.setInitialTerminalSize(new TerminalSize(XCase, YCase));

            this.terminal = defaultTerminalFactory.createTerminal();                        // on initialise notre terminal avec notre factory modifie
            this.screen = new TerminalScreen(terminal);
            this.screen.startScreen();

            SwingTerminalFrame terminalFrame = (SwingTerminalFrame)this.terminal;    // On part du principe qu'on fonctionnera toujours dans une fenetre de terminal externe (a cause de windows)
            terminalFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            this.text = screen.newTextGraphics();

            //menu();

        } catch (IOException e) {
            System.out.println("IOException : Erreur d'initialisation du terminal. Message complet : " + e.getMessage());
        }
    }

    public Terminal getTerminal(){
        return this.terminal;
    }

    public Screen getScreen(){
        return this.screen;
    }

    public static int getXCase(){
        return XCase;
    }

    public static int getYCase(){
        return YCase;
    }

    public TextGraphics getTextGraphics(){
        return this.text;
    }

    /**
     * Print le menu, ne fait rien a part print
     */
    public void menu(){
        try {
            screen.clear();
            text.setForegroundColor(TextColor.ANSI.GREEN);
            text.putString(32,5, "███╗   ███╗███████╗███╗   ██╗██╗   ██╗");
            text.putString(32,6, "████╗ ████║██╔════╝████╗  ██║██║   ██║");
            text.putString(32,7, "██╔████╔██║█████╗  ██╔██╗ ██║██║   ██║");
            text.putString(32,8, "██║╚██╔╝██║██╔══╝  ██║╚██╗██║██║   ██");
            text.putString(32,9, "██║ ╚═╝ ██║███████╗██║ ╚████║╚██████╔╝"); 
            text.putString(32,10, "╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝ ╚═════╝"); 


            text.putString(32, 15,"> Lancer le jeu");
            text.putString(35, 16,"Ecriver 'launch game'");
            screen.refresh(); 
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }

    public static int positionLettre = 0;
    /**
     * Affiche les lettres taper par l'utilisateur pour lancer une partie
     * Pour simuler un terminal
     */
    public void printLetterMenu(char r) throws IOException{
        text.setCharacter(positionLettre, screen.getTerminalSize().getRows() -1, r);
        positionLettre++;
        screen.refresh();
    }

    /**
     * Print les stats de la partie
     * @param round -> Le numero de round
     * @param team -> la team active
     * @param indexWActif -> le worm actif de la team
     * @param characterModel -> Pour afficher les PV du worms actif
     */
    public void printStatsInGame(int round, String team, int indexWActif, CharacterModel characterModel){
        text.setForegroundColor(TextColor.ANSI.WHITE);
        text.putString(1,1, "Round " + round + " - Equipe active : " + team);
        text.putString(1,2,"Worms actif numero : " + indexWActif);  // pas d'utiliter dans le terminal mais on pourrait print le nom du characterModel a la place
        text.putString(1,3, "PV = " + characterModel.getHealth());
        //screen.refresh();
    }

    /**
     * Pour effacer et raffraichir la page plus simplement
     */
    public void clearRefresh(){
        try {
            screen.clear();
            screen.refresh();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Pour raffraichir la page plus simplement
     */
    public void refresh(){
        try {
            screen.refresh();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    
}
