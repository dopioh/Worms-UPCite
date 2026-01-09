package fr.upc.g5e4.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import fr.upc.g5e4.model.TutorialModel;
import fr.upc.g5e4.view.TutorialView;

/**
 * Controller pour le tutoriel (bouton comment jouer)
 * @author Nassim
 */
public class TutorialController {
    
    private TutorialModel model;
    private TutorialView view;
    
    public TutorialController(TutorialModel model, TutorialView view) {
        this.model = model;
        this.view = view;
        
        setupListeners();
    }
    
    private void setupListeners() {
        view.getContinueButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.nextPage();
            }
        });
        
        view.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.previousPage();
            }
        });
        
        view.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeTutorial();
            }
        });
    }
    
    // Afficer le tutoriel
    public void showTutorial() {
        model.reset();
        view.setVisible(true);
        view.repaint();
    }
    
    // Fermer le tutoriel
    private void closeTutorial() {
        model.reset();
        view.setVisible(false);
    }
    
    public TutorialView getView() {
        return view;
    }
}
