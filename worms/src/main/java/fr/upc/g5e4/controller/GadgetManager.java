package fr.upc.g5e4.controller;

import fr.upc.g5e4.model.CharacterModel;
import fr.upc.g5e4.model.InventoryModel.InventoryJson;
import fr.upc.g5e4.model.SoundPlayer;
import fr.upc.g5e4.view.CharacterView;

/**
 * Classe destiné à l'utilisation des gadgets
 */
public class GadgetManager {
    
    /**
     * Utilise un gadget sur un worm
     * @param itemName Le nom du gadget à utiliser
     * @param currentWorm Le worm sur lequel appliquer l'effet
     * @param charView La vue du worm pour afficher les effets visuels (peut être null)
     * @param gadgetFromInventory Le gadget dans l'inventaire pour enregistrer l'utilisation
     * @return true si le gadget a été utilisé avec succès, false sinon
     */
    public static boolean useGadget(String itemName, CharacterModel currentWorm, CharacterView charView, InventoryJson gadgetFromInventory) {
        if (currentWorm == null) {
            return false;
        }
        
        // afficher le sprite du gadget
        if (charView != null) {
            charView.setCurrentGadgetSprite(itemName);
        }
        
        boolean success = false;
        
        switch (itemName) {
            case "PotionHeal":
                currentWorm.setHealth(Math.min(100, currentWorm.getHealth() + 50));
                currentWorm.healEffect();
                System.out.println("Potion Heal : +50 HP");
                SoundPlayer.play("/sfx/potionheal-sound.wav");
                success = true;
                break;
                
            case "PotionShield":
                currentWorm.setShield(Math.min(100, currentWorm.getShield() + 25));
                currentWorm.shieldEffect();
                System.out.println("Potion Shield : +25 Shield");
                SoundPlayer.play("/sfx/potionshield-sound.wav");
                success = true;
                break;
                
            case "PotionSpeed":
                currentWorm.applySpeedBuff(20000);
                if (charView != null) {
                    charView.getNameTagView().addItemFromResource("/buffs/speed_buff.png", "speed_buff_icon", 20000);
                }
                System.out.println("Potion Speed : +50% vitesse");
                SoundPlayer.play("/sfx/potionspeed-sound.wav");
                success = true;
                break;
                
            case "PotionJump":
                currentWorm.applyJumpBuff(20000);
                if (charView != null) {
                    charView.getNameTagView().addItemFromResource("/buffs/jump_buff.png", "jump_buff_icon", 20000);
                }
                System.out.println("Potion Jump : +50% saut");
                SoundPlayer.play("/sfx/potionjump-sound.wav");
                success = true;
                break;
                
            case "Drill":
                System.out.println("Drill équipée; Clic gauche pour casser 1 bloc, clic droit pour en poser");
                SoundPlayer.play("/sfx/drill.wav");
                success = true;
                break;
                
            case "Teleporter":
                System.out.println("Teleporter équipé; Clic gauche pour se téléporter");
                SoundPlayer.play("/sfx/teleporter-sound.wav");
                success = true;
                break;
                
            default:
                System.out.println("[Gadget] Gadget inconnu : " + itemName);
                return false;
        }
        
        // enregistrer l'utilisation du gadget si succès
        if (success && gadgetFromInventory != null && gadgetFromInventory.canUseThisTurn()) {
            gadgetFromInventory.registerUseThisTurn();
        }
        
        return success;
    }
    
    /**
     * Applique un buff à un worm (utilisé lors du chargement d'une sauvegarde)
     * @param buffType Type de buff à appliquer
     * @param remainingTime Temps restant du buff en millisecondes
     * @param worm Le worm sur lequel appliquer le buff
     * @param charView La vue du worm pour afficher l'icône du buff
     */
    public static void applyBuffFromSave(fr.upc.g5e4.model.BuffModel.BuffType buffType, long remainingTime, CharacterModel worm, CharacterView charView) {
        if (worm == null) {
            return;
        }
        
        switch (buffType) {
            case SPEED_BOOST:
                worm.applySpeedBuff((int) remainingTime);
                if (charView != null) {
                    charView.getNameTagView().addItemFromResource("/buffs/speed_buff.png", "speed_buff_icon", remainingTime);
                }
                break;
                
            case JUMP_BOOST:
                worm.applyJumpBuff((int) remainingTime);
                if (charView != null) {
                    charView.getNameTagView().addItemFromResource("/buffs/jump_buff.png", "jump_buff_icon", remainingTime);
                }
                break;
                
            default:
                System.out.println("[GadgetManager] Buff inconnu : " + buffType);
                break;
        }
    }
}
