package com.cameramanager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ScanResultsDialog extends JDialog {

    private JList<String> ipList;
    private List<String> selectedIps;

    public ScanResultsDialog(Frame owner, List<String> foundIps) {
        super(owner, "Намерени устройства в мрежата", true);
        // Define colors based on preferences
        Color backgroundColor = new Color(0x1A1A1A); // #1A1A1A
        Color textColor = new Color(0xE0E0E0);      // #E0E0E0
        Color accentColor = new Color(0x00BCD4);     // #00BCD4

        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout());

        // Списък с намерените IP адреси
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String ip : foundIps) {
            listModel.addElement(ip);
        }
        ipList = new JList<>(listModel);
        ipList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ipList.setBackground(backgroundColor);
        ipList.setForeground(textColor);
        ipList.setSelectionBackground(accentColor.darker()); // Darker accent for selection
        ipList.setSelectionForeground(textColor);

        JScrollPane scrollPane = new JScrollPane(ipList);
        scrollPane.getViewport().setBackground(backgroundColor); // Set scroll pane background
        add(scrollPane, BorderLayout.CENTER);

        // Панел за бутоните
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        JButton addButton = new JButton("Добави избраните");
        JButton cancelButton = new JButton("Отказ");

        // Apply consistent button styling
        JButton[] buttons = {addButton, cancelButton};
        for (JButton button : buttons) {
            button.setBackground(accentColor);
            button.setForeground(textColor);
            button.setFocusPainted(false); // Remove focus border
            buttonPanel.add(button);
        }

        addButton.addActionListener(e -> onAdd());
        cancelButton.addActionListener(e -> onCancel());

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(300, 400);
        setLocationRelativeTo(owner);
    }

    private void onAdd() {
        selectedIps = ipList.getSelectedValuesList();
        setVisible(false);
    }

    private void onCancel() {
        selectedIps = null;
        setVisible(false);
    }

    public List<String> getSelectedIps() {
        return selectedIps;
    }
}
