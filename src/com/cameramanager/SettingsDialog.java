package com.cameramanager;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {

    private JTextField diskLimitField;
    private long diskLimit;

    public SettingsDialog(Frame owner) {
        super(owner, "Настройки", true);

        // Define colors based on preferences
        Color backgroundColor = new Color(0x1A1A1A); // #1A1A1A
        Color textColor = new Color(0xE0E0E0);      // #E0E0E0
        Color accentColor = new Color(0x00BCD4);     // #00BCD4

        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout(15, 15)); // Увеличени отстояния
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Увеличени отстояния

        // Панел за въвеждане на лимит на диска
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15)); // Увеличени отстояния
        inputPanel.setBackground(backgroundColor);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel diskLimitLabel = new JLabel("Лимит на диска (MB):");
        diskLimitLabel.setForeground(textColor);
        diskLimitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // По-голям шрифт
        inputPanel.add(diskLimitLabel);

        diskLimitField = new JTextField(10);
        diskLimitField.setBackground(backgroundColor.brighter());
        diskLimitField.setForeground(textColor);
        diskLimitField.setCaretColor(textColor);
        diskLimitField.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // По-голям шрифт
        inputPanel.add(diskLimitField);

        // Зареждане на текущия лимит
        diskLimitField.setText(String.valueOf(ConfigurationManager.loadDiskLimit()));

        add(inputPanel, BorderLayout.CENTER);

        // Панел за бутоните
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10)); // Увеличени отстояния
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Add padding
        JButton okButton = new JButton("Запази");
        JButton cancelButton = new JButton("Отказ");

        // Apply consistent button styling
        JButton[] buttons = {okButton, cancelButton};
        for (JButton button : buttons) {
            button.setBackground(accentColor);
            button.setForeground(textColor);
            button.setFocusPainted(false); // Remove focus border
            button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // По-голям шрифт
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Увеличени отстояния
            buttonPanel.add(button);
        }

        okButton.addActionListener(e -> onOK());
        cancelButton.addActionListener(e -> onCancel());

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(350, 150);
        setLocationRelativeTo(owner);
    }

    private void onOK() {
        try {
            diskLimit = Long.parseLong(diskLimitField.getText().trim());
            if (diskLimit <= 0) {
                JOptionPane.showMessageDialog(this, "Лимитът на диска трябва да е положително число.", "Грешка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ConfigurationManager.saveDiskLimit(diskLimit);
            setVisible(false);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Моля, въведете валидно число за лимит на диска.", "Грешка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        setVisible(false);
    }
}
