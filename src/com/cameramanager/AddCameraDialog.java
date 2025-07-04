package com.cameramanager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddCameraDialog extends JDialog {

    private JTextField ipAddressField;
    private JTextField nameField;
    private JTextField portsField;
    private Camera camera;

    public AddCameraDialog(Frame owner, Camera cameraToEdit) {
        super(owner, cameraToEdit == null ? "Добавяне на нова камера" : "Редактиране на камера", true);
        this.camera = cameraToEdit;

        // Define colors based on preferences
        Color backgroundColor = new Color(0x1A1A1A); // #1A1A1A
        Color textColor = new Color(0xE0E0E0);      // #E0E0E0
        Color accentColor = new Color(0x00BCD4);     // #00BCD4

        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout(15, 15)); // Увеличени отстояния
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Увеличени отстояния

        // Панел за въвеждане на данни
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 15, 15)); // Увеличени отстояния
        inputPanel.setBackground(backgroundColor);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel ipLabel = new JLabel("IP адрес:");
        ipLabel.setForeground(textColor);
        ipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // По-голям шрифт
        ipAddressField = new JTextField(15);
        ipAddressField.setBackground(backgroundColor.brighter());
        ipAddressField.setForeground(textColor);
        ipAddressField.setCaretColor(textColor);
        ipAddressField.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // По-голям шрифт

        JLabel nameLabel = new JLabel("Име на камерата:");
        nameLabel.setForeground(textColor);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // По-голям шрифт
        nameField = new JTextField(15);
        nameField.setBackground(backgroundColor.brighter());
        nameField.setForeground(textColor);
        nameField.setCaretColor(textColor);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // По-голям шрифт

        JLabel portsLabel = new JLabel("Отворени портове (пр. 80,443):");
        portsLabel.setForeground(textColor);
        portsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // По-голям шрифт
        portsField = new JTextField(15);
        portsField.setBackground(backgroundColor.brighter());
        portsField.setForeground(textColor);
        portsField.setCaretColor(textColor);
        portsField.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // По-голям шрифт

        inputPanel.add(ipLabel);
        inputPanel.add(ipAddressField);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(portsLabel);
        inputPanel.add(portsField);

        add(inputPanel, BorderLayout.CENTER);

        // Попълване на полетата при редактиране
        if (cameraToEdit != null) {
            ipAddressField.setText(cameraToEdit.getIpAddress());
            nameField.setText(cameraToEdit.getName());
            if (cameraToEdit.getOpenPorts() != null && !cameraToEdit.getOpenPorts().isEmpty()) {
                String portsText = cameraToEdit.getOpenPorts().stream()
                                            .map(String::valueOf)
                                            .collect(Collectors.joining(","));
                portsField.setText(portsText);
            }
            ipAddressField.setEditable(false); // IP адресът не може да се променя при редактиране
        }

        // Панел за бутоните
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10)); // Увеличени отстояния
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Add padding
        JButton okButton = new JButton(cameraToEdit == null ? "Добави" : "Запази");
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

        setSize(600, 300);
        setLocationRelativeTo(owner);
    }

    private void onOK() {
        String ipAddress = ipAddressField.getText().trim();
        String name = nameField.getText().trim();
        String portsText = portsField.getText().trim();

        if (ipAddress.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Моля, попълнете IP адрес и име на камерата.", "Грешка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Integer> openPorts = new ArrayList<>();
        if (!portsText.isEmpty()) {
            try {
                openPorts = Arrays.stream(portsText.split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Невалиден формат на портовете. Моля, въведете числа, разделени със запетаи.", "Грешка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (this.camera == null) {
            // Добавяне на нова камера
            this.camera = new Camera(ipAddress, name, openPorts);
        } else {
            // Редактиране на съществуваща камера
            this.camera.setName(name);
            this.camera.setOpenPorts(openPorts);
            // IP адресът не се променя, тъй като полето е нередактируемо
        }
        setVisible(false);
    }

    private void onCancel() {
        this.camera = null; // Отказва операцията
        setVisible(false);
    }

    public Camera getCamera() {
        return camera;
    }
}