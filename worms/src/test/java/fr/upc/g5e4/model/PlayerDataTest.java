package fr.upc.g5e4.model;

import fr.upc.g5e4.model.io.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;

/**
 * Classe simple de test pour PlayerData (new version)
 */
@ExtendWith(MockitoExtension.class)
public class PlayerDataTest {

    private PlayerData playerData;
    @Mock
    private CustomGameManager mockCustomGameManager;

    /**
     * Teste le constructeur par défaut (nouveau joueur)
     * Vérifie l'initialisation des valeurs de base (XP, Money, Skins par défaut)
     */
    @Test
    public void testConstructorNewPlayer() {
        playerData = new PlayerData("MEIIIIIMIEIIIII");

        assertEquals("MEIIIIIMIEIIIII", playerData.getName());
        assertEquals(0, playerData.getXp());
        assertEquals(0, playerData.getMoney());
        
        // verifie que les skins sont bien initialises
        assertNotNull(playerData.getTabSkinPlayer());
        assertEquals(6, playerData.getTabSkinPlayer().length);
        
        // verifie qu'un customGameManager a bien ete cree
        assertNotNull(playerData.getCustomTeamManager());
    }

    /**
     * Teste le deblocage d'un skin
     */
    @Test
    public void testUnlockSkin() {
        playerData = new PlayerData("Hugo");
        String skinToUnlock = "Worm Nerd Edition";

        // verifie qu'il est locked avant de le debloquer
        assertFalse(playerData.isSkinUnlocked(skinToUnlock), "Le skin devrait être verrouille au début");

        // on le debloque
        boolean result = playerData.unlockSkin(skinToUnlock);

        assertTrue(result, "La methode unlockSkin devrait retourner true");
        assertTrue(playerData.isSkinUnlocked(skinToUnlock), "Le skin devrait être debloque maintenant");
    }

    /**
     * Teste la methode unlockSkin dans le cas ou le nom n'est pas reconnu
     */
    @Test
    public void testUnlockUnknownSkin() {
        playerData = new PlayerData("Billy");
        
        boolean result = playerData.unlockSkin("BlaBlaBla");
        
        assertFalse(result, "UnlockSkin doit retourner false si le skin n'existe pas");
    }

    /**
     * Teste la recuperation des skins uniquement unlocked
     */
    @Test
    public void testOnlyUnlocked() {
        playerData = new PlayerData("Nassim");

        // seulement noel pinky et default sont censee etre unlocked pour un nouveau joueur
        ArrayList<String> unlockedSkins = playerData.onlyUnlocked();
        
        assertTrue(unlockedSkins.contains("Worm de base"));
        assertTrue(unlockedSkins.contains("Worm Noel Edition"));
        assertFalse(unlockedSkins.contains("Worm Nerd Edition")); // lui doit etre locked
        
        assertEquals(3, unlockedSkins.size(), "Il devrait y avoir 3 skins debloques par defaut");
    }

    /**
     * Teste la recherche de skin par nom
     */
    @Test
    void testFindSkinModelByName() {
        playerData = new PlayerData("Ian");

        // cas ou le skin existe
        SkinModel found = playerData.findSkinModelByName("Worm Noel Edition");
        assertEquals("Worm Noel Edition", found.getName());

        // si le skin existe pas, ca doit renvoyer celui de base
        SkinModel notFound = playerData.findSkinModelByName("Raph");
        assertEquals("Worm de base", notFound.getName());
    }
}