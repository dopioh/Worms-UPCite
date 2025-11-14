package fr.upc.g5e4.model;

import org.jbox2d.dynamics.*;

/**
 * Classe abstraite pour representer les entites du jeu (worms, projectiles, etc.)
 */
public abstract class Entity {
    protected final GroundModel model;
    protected final World world;

    public Entity(GroundModel model, World world){
        this.model = model;
        this.world = world;
        
    }
    
    /**
     * Vous devez creer son Body dans sa propre class comme avec PersonnageModel.java
     * 
     * 3 étapes pour construire un Body :
     *      1. Faire un BodyDef
     *      2. Faire une FixtureDef
     *      3. Assembler tout ca dans un 
     *      {@code Body b = new Body(BodyDef bd)}
     *      {@code b.createFixture(Fixturedef fd)}
     * 
     * @see CharacterModel#createBody 
     */
    public abstract Body createBody();
}
