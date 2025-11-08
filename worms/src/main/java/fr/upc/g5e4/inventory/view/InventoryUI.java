package fr.upc.g5e4.inventory.view;

import fr.upc.g5e4.inventory.model.*;
import fr.upc.g5e4.inventory.controller.InventoryLoader;

import javax.swing.*;
import java.awt.*;

public class InventoryUI extends JFrame {
    private TeamInventory inventory;
    private final DefaultListModel<ItemStack> listModel = new DefaultListModel<>();
    private final JList<ItemStack> list = new JList<>(listModel);
    private final JTextArea details = new JTextArea(6, 32);
    private String jsonPath = "projet-worms\\worms\\src\\main\\resources\\inventory.json";

    public InventoryUI() {
        super("Inventaire d'équipe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        details.setEditable(false);

        JPanel right = new JPanel(new BorderLayout(6, 6));
        right.add(new JScrollPane(details), BorderLayout.CENTER);

        JButton useBtn = new JButton("Utiliser");
        JButton reloadBtn = new JButton("Recharger");
        JButton saveBtn = new JButton("Sauvegarder");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(reloadBtn); actions.add(useBtn); actions.add(saveBtn);
        right.add(actions, BorderLayout.SOUTH);

        add(new JScrollPane(list), BorderLayout.WEST);
        add(right, BorderLayout.CENTER);

        list.addListSelectionListener(e -> showDetails(list.getSelectedValue()));
        useBtn.addActionListener(e -> onUse());
        reloadBtn.addActionListener(e -> onReload());
        saveBtn.addActionListener(e -> onSave());

        onReload();

        setSize(680, 420);
        setLocationRelativeTo(null);
    }

    private void bindInventory(TeamInventory inv) {
        this.inventory = inv;
        inventory.addObservateurs(this::refreshList);
        refreshList();
    }

    private void refreshList() {
        ItemStack sel = list.getSelectedValue();
        String selId = sel == null ? null : sel.getItem().id();

        listModel.clear();
        for (ItemStack s : inventory.all()) listModel.addElement(s);

        if (selId != null) {
            for (int i = 0; i < listModel.size(); i++) {
                if (listModel.get(i).getItem().id().equals(selId)) { list.setSelectedIndex(i); break; }
            }
        } else if (!listModel.isEmpty()) {
            list.setSelectedIndex(0);
        }
        showDetails(list.getSelectedValue());
    }

    private void showDetails(ItemStack s) {
        if (s == null) { details.setText(""); return; }
        Item it = s.getItem();
        String qty = s.isUnlimited() ? "∞ (illimité)" : String.valueOf(s.getQuantity());
        String dmg = it.type() == Item.Type.WEAPON ? String.valueOf(it.damage()) : "-";
        details.setText(
            "ID      : " + it.id() + "\n" +
            "Nom     : " + it.name() + "\n" +
            "Type    : " + it.type() + "\n" +
            "Dégâts  : " + dmg + "\n" +
            "Quantité: " + qty + "\n"
        );
    }

    private void onUse() {
        ItemStack s = list.getSelectedValue();
        if (s == null) return;
        if (!inventory.use(s.getItem().id())) {
            JOptionPane.showMessageDialog(this, "Plus de munitions.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onReload() {
        try {
            TeamInventory loaded = InventoryLoader.load(jsonPath);
            bindInventory(loaded);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSave() {
        try {
            InventoryLoader.save(inventory, jsonPath);
            JOptionPane.showMessageDialog(this, "Inventaire sauvegardé.", "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur de sauvegarde: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InventoryUI ui = new InventoryUI();
            if (args != null && args.length > 0) ui.jsonPath = args[0];
            ui.setVisible(true);
        });
    }
}
