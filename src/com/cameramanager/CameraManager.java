package com.cameramanager;

import java.awt.*;
import java.net.*;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.AWTException;
import java.awt.Image;
import javax.swing.ImageIcon;

public class CameraManager {

    private static DefaultListModel<Camera> cameraListModel;
    private static JPanel videoContainerPanel; // Main panel that holds the video grid
    private static JPanel videoGridPanel; // Panel that holds the actual video players in a grid
    private static JPanel detailedViewPanel; // Panel for detailed view of a single camera
    private static List<EmbeddedMediaPlayerComponent> activePlayers = new ArrayList<>();
    private static final String[] LOW_LATENCY_OPTIONS = {":network-caching=675", ":live-caching=675", ":rtsp-tcp"};
    private static JFrame mainFrame; // Declare JFrame as a static field
    private static TrayIcon trayIcon;

    public static void main(String[] args) {
        new NativeDiscovery().discover();
        SwingUtilities.invokeLater(CameraManager::createAndShowGui);
    }

    private static void createAndShowGui() {
        mainFrame = new JFrame("TSA-Security"); // Assign to static field
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle closing manually
        mainFrame.setSize(1280, 800);
        mainFrame.setLocationRelativeTo(null);

        // Handle window closing to minimize to tray
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setVisible(false);
            }
        });

        // Setup System Tray
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = new ImageIcon("image.ico").getImage(); // Load your icon

            PopupMenu popup = new PopupMenu();
            MenuItem showItem = new MenuItem("Покажи");
            MenuItem exitItem = new MenuItem("Изход");

            showItem.addActionListener(e -> SwingUtilities.invokeLater(() -> mainFrame.setVisible(true)));
            exitItem.addActionListener(e -> System.exit(0));

            popup.add(showItem);
            popup.add(exitItem);

            trayIcon = new TrayIcon(image, "TSA-Security", popup);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }
        }

        // Define colors based on preferences
        Color backgroundColor = new Color(0x1A1A1A); // #1A1A1A
        Color textColor = new Color(0xE0E0E0);      // #E0E0E0
        Color accentColor = new Color(0x00BCD4);     // #00BCD4
        Color secondaryColor = new Color(0xA0A0A0);  // #A0A0A0

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)); // Увеличени отстояния
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Увеличени отстояния

        // --- Панел за списъка с камери (вляво) ---
        cameraListModel = new DefaultListModel<>();
        JList<Camera> cameraJList = new JList<>(cameraListModel);
        cameraJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cameraJList.setBackground(backgroundColor);
        cameraJList.setForeground(textColor);
        cameraJList.setSelectionBackground(accentColor.darker()); // Darker accent for selection
        cameraJList.setSelectionForeground(textColor);
        cameraJList.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // По-голям шрифт

        JPanel cameraListPanel = new JPanel(new BorderLayout());
        cameraListPanel.setBackground(backgroundColor);
        cameraListPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(secondaryColor, 2), // По-дебела рамка
                "Добавени камери",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), // По-голям шрифт за заглавието
                textColor // Title text color
        ));
        cameraListPanel.setPreferredSize(new Dimension(280, 0)); // По-широк панел
        JScrollPane cameraListScrollPane = new JScrollPane(cameraJList);
        cameraListScrollPane.getViewport().setBackground(backgroundColor); // Set scroll pane background
        cameraListPanel.add(cameraListScrollPane, BorderLayout.CENTER);
        mainPanel.add(cameraListPanel, BorderLayout.WEST);

        // --- Панел за видеото (в центъра) с GridLayout ---
        videoGridPanel = new JPanel(new GridLayout(0, 1, 5, 5)); // 0 rows, 1 column initially
        videoGridPanel.setBackground(backgroundColor);

        detailedViewPanel = new JPanel(new BorderLayout());
        detailedViewPanel.setBackground(backgroundColor);
        JButton backToGridViewButton = new JButton("Обратно към мрежата");
        backToGridViewButton.setBackground(accentColor);
        backToGridViewButton.setForeground(textColor);
        backToGridViewButton.setFocusPainted(false); // Remove focus border
        backToGridViewButton.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Larger font
        backToGridViewButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding
        detailedViewPanel.add(backToGridViewButton, BorderLayout.NORTH);

        videoContainerPanel = new JPanel(new CardLayout());
        videoContainerPanel.setBackground(backgroundColor);
        videoContainerPanel.add(videoGridPanel, "GridView");
        videoContainerPanel.add(detailedViewPanel, "DetailedView");
        mainPanel.add(videoContainerPanel, BorderLayout.CENTER);

        // --- Панел за бутоните (отгоре) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)); // Увеличени отстояния
        buttonPanel.setBackground(backgroundColor);
        JButton addCameraButton = new JButton("Добави камера");
        JButton scanNetworkButton = new JButton("Сканирай мрежата");
        JButton removeCameraButton = new JButton("Премахни камера");
        JButton editCameraButton = new JButton("Редактирай камера");
        // Apply consistent button styling
        JButton[] buttons = {addCameraButton, scanNetworkButton, removeCameraButton, editCameraButton};
        for (JButton button : buttons) {
            button.setBackground(accentColor);
            button.setForeground(textColor);
            button.setFocusPainted(false); // Remove focus border
            button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // По-голям шрифт
            button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Увеличени отстояния
            buttonPanel.add(button);
        }

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        

        addCameraButton.addActionListener(e -> {
            AddCameraDialog dialog = new AddCameraDialog(mainFrame, null);
            dialog.setVisible(true);
            Camera newCamera = dialog.getCamera();
            if (newCamera != null) {
                // Преди да добавим, сканираме за отворени портове, ако не са зададени
                if (newCamera.getOpenPorts() == null || newCamera.getOpenPorts().isEmpty()) {
                    new Thread(() -> {
                        List<Integer> openPorts = PortScanner.findOpenPorts(newCamera.getIpAddress(), 200);
                        newCamera.setOpenPorts(openPorts);
                        SwingUtilities.invokeLater(() -> {
                            addCamera(newCamera);
                        });
                    }).start();
                } else {
                    addCamera(newCamera);
                }
            }
        });

        removeCameraButton.addActionListener(e -> {
            int selectedIndex = cameraJList.getSelectedIndex();
            if (selectedIndex != -1) {
                removeCamera(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Моля, изберете камера за премахване.", "Няма избрана камера", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        editCameraButton.addActionListener(e -> {
            int selectedIndex = cameraJList.getSelectedIndex();
            if (selectedIndex != -1) {
                Camera cameraToEdit = cameraListModel.getElementAt(selectedIndex);
                AddCameraDialog dialog = new AddCameraDialog(mainFrame, cameraToEdit);
                dialog.setVisible(true);
                Camera updatedCamera = dialog.getCamera();
                if (updatedCamera != null) {
                    // Актуализираме камерата в модела
                    cameraListModel.setElementAt(updatedCamera, selectedIndex);
                    // Пресъздаваме видео плейъра за обновената камера
                    if (selectedIndex < activePlayers.size()) {
                        EmbeddedMediaPlayerComponent oldPlayer = activePlayers.remove(selectedIndex);
                        videoGridPanel.remove(oldPlayer);
                        oldPlayer.mediaPlayer().controls().stop();
                        oldPlayer.release();
                    }
                    createNewVideoPlayer(updatedCamera);
                    saveCameras();
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Моля, изберете камера за редактиране.", "Няма избрана камера", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        scanNetworkButton.addActionListener(e -> {
            scanNetworkButton.setEnabled(false);
            scanNetworkButton.setText("Сканиране...");

            new Thread(() -> {
                String subnet = getLocalSubnet();
                List<String> activeHosts = (subnet != null) ? NetworkScanner.findActiveHosts(subnet) : new ArrayList<>();

                SwingUtilities.invokeLater(() -> {
                    scanNetworkButton.setEnabled(true);
                    scanNetworkButton.setText("Сканирай мрежата");

                    if (activeHosts.isEmpty()) {
                        JOptionPane.showMessageDialog(mainFrame, "Не бяха намерени активни устройства.", "Край", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    ScanResultsDialog resultsDialog = new ScanResultsDialog(mainFrame, activeHosts);
                    resultsDialog.setVisible(true);
                    List<String> selectedIps = resultsDialog.getSelectedIps();

                    if (selectedIps != null) {
                        for (String ip : selectedIps) {
                            // При сканиране, добавяме само IP, името ще е по подразбиране
                            Camera scannedCamera = new Camera(ip, "Camera @ " + ip, new ArrayList<>());
                            // Сканираме за портове след добавяне
                            new Thread(() -> {
                                List<Integer> openPorts = PortScanner.findOpenPorts(scannedCamera.getIpAddress(), 200);
                                scannedCamera.setOpenPorts(openPorts);
                                SwingUtilities.invokeLater(() -> {
                                    addCamera(scannedCamera);
                                });
                            }).start();
                        }
                    }
                });
            }).start();
        });

        mainFrame.getContentPane().add(mainPanel);
        mainFrame.setVisible(true);

        // Зареждане на запазените камери при старт
        loadCameras();
    }

    private static void showGridView() {
        CardLayout cl = (CardLayout)(videoContainerPanel.getLayout());
        cl.show(videoContainerPanel, "GridView");
    }

    

    private static void addCamera(Camera camera) {
        // Проверка дали камерата вече съществува по IP адрес
        for (int i = 0; i < cameraListModel.size(); i++) {
            if (cameraListModel.get(i).getIpAddress().equals(camera.getIpAddress())) {
                // Ако камерата вече съществува, просто я актуализираме
                cameraListModel.set(i, camera);
                // Актуализираме и видео плейъра, ако е необходимо
                // За простота, тук може да се наложи да пресъздадем плейъра или да обновим MRL
                // Засега просто ще пресъздадем, ако е необходимо
                if (i < activePlayers.size()) {
                    EmbeddedMediaPlayerComponent oldPlayer = activePlayers.remove(i);
                    videoGridPanel.remove(oldPlayer);
                    oldPlayer.mediaPlayer().controls().stop();
                    oldPlayer.release();
                }
                createNewVideoPlayer(camera);
                saveCameras();
                return;
            }
        }

        if (cameraListModel.size() >= 4) {
            JOptionPane.showMessageDialog(null, "Можете да добавите максимум 4 камери.", "Лимит на камерите", JOptionPane.WARNING_MESSAGE);
            return;
        }

        cameraListModel.addElement(camera);
        createNewVideoPlayer(camera);
        saveCameras(); // Запазваме новия списък
    }

    private static void createNewVideoPlayer(Camera camera) {
        String mrl = buildMrl(camera);
        if (mrl == null) {
            System.out.println("Не може да се генерира MRL за камера " + camera.getIpAddress());
            return;
        }

        EmbeddedMediaPlayerComponent player = new EmbeddedMediaPlayerComponent();
        videoGridPanel.add(player);
        activePlayers.add(player);
        updateVideoLayout();

        System.out.println("Пускане на видео от: " + mrl);
        player.mediaPlayer().media().play(mrl, LOW_LATENCY_OPTIONS);
    }

    private static String buildMrl(Camera camera) {
        if (camera.getOpenPorts().contains(554)) {
            return "rtsp://" + camera.getIpAddress() + ":554/";
        }
        return null;
    }

    private static void updateVideoLayout() {
        int numPlayers = activePlayers.size();
        int rows = 0;
        int cols = 0;

        if (numPlayers == 1) {
            rows = 1;
            cols = 1;
        } else if (numPlayers == 2) {
            rows = 1;
            cols = 2;
        } else if (numPlayers == 3) {
            rows = 1;
            cols = 3;
        } else if (numPlayers == 4) {
            rows = 2;
            cols = 2;
        } else if (numPlayers > 4) {
            // For more than 4, we can default to 2x2 and just show the first 4, or implement more complex logic.
            // For now, let's stick to max 4 as per requirement.
            rows = 2;
            cols = 2;
            // Remove extra players if more than 4 are added
            while (activePlayers.size() > 4) {
                EmbeddedMediaPlayerComponent playerToRemove = activePlayers.remove(activePlayers.size() - 1);
                videoGridPanel.remove(playerToRemove);
                playerToRemove.release();
            }
        }

        ((GridLayout) videoGridPanel.getLayout()).setRows(rows);
        ((GridLayout) videoGridPanel.getLayout()).setColumns(cols);

        videoGridPanel.revalidate();
        videoGridPanel.repaint();
        videoContainerPanel.revalidate();
        videoContainerPanel.repaint();
    }

    private static void saveCameras() {
        List<Camera> cameras = new ArrayList<>();
        for (int i = 0; i < cameraListModel.size(); i++) {
            cameras.add(cameraListModel.get(i));
        }
        ConfigurationManager.saveCameras(cameras);
    }

    private static void loadCameras() {
        List<Camera> savedCameras = ConfigurationManager.loadCameras();
        for (Camera camera : savedCameras) {
            addCamera(camera);
        }
    }

    private static String getLocalSubnet() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String ip = socket.getLocalAddress().getHostAddress();
            return ip.substring(0, ip.lastIndexOf('.'));
        } catch (Exception e) {
            return "192.168.1";
        }
    }

    private static void removeCamera(int index) {
        if (index >= 0 && index < cameraListModel.size()) {
            Camera cameraToRemove = cameraListModel.remove(index);
            // Stop and release the media player associated with the removed camera
            if (index < activePlayers.size()) {
                EmbeddedMediaPlayerComponent playerToRemove = activePlayers.remove(index);
                videoGridPanel.remove(playerToRemove);
                playerToRemove.mediaPlayer().controls().stop();
                playerToRemove.release();
            }
            updateVideoLayout();
            saveCameras();
        }
    }
}
