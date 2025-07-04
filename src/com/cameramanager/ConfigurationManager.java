package com.cameramanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationManager {

    private static final String CONFIG_FILE = "cameras.txt";
    private static final String DISK_LIMIT_FILE = "disk_limit.txt";

    // Зарежда камерите от файла
    public static List<Camera> loadCameras() {
        List<Camera> cameras = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(";");
                    if (parts.length >= 2) {
                        String ipAddress = parts[0];
                        String name = parts[1];
                        List<Integer> openPorts = new ArrayList<>();
                        if (parts.length > 2 && !parts[2].isEmpty()) {
                            String[] portStrings = parts[2].split(",");
                            for (String portStr : portStrings) {
                                try {
                                    openPorts.add(Integer.parseInt(portStr.trim()));
                                } catch (NumberFormatException e) {
                                    System.err.println("Невалиден формат на порт: " + portStr);
                                }
                            }
                        }
                        cameras.add(new Camera(ipAddress, name, openPorts));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Файлът с конфигурация '" + CONFIG_FILE + "' не е намерен. Ще бъде създаден автоматично.");
        }
        return cameras;
    }

    // Запазва списъка с камери във файла
    public static void saveCameras(List<Camera> cameras) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            for (Camera camera : cameras) {
                StringBuilder line = new StringBuilder();
                line.append(camera.getIpAddress()).append(";");
                line.append(camera.getName()).append(";");
                List<Integer> ports = camera.getOpenPorts();
                if (ports != null && !ports.isEmpty()) {
                    for (int i = 0; i < ports.size(); i++) {
                        line.append(ports.get(i));
                        if (i < ports.size() - 1) {
                            line.append(",");
                        }
                    }
                }
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Зарежда лимита на диска от файла
    public static long loadDiskLimit() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DISK_LIMIT_FILE))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return Long.parseLong(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Файлът с лимит на диска '" + DISK_LIMIT_FILE + "' не е намерен или е невалиден. Ще бъде използван лимит по подразбиране.");
        }
        return 1024L; // Лимит по подразбиране: 1GB
    }

    // Запазва лимита на диска във файла
    public static void saveDiskLimit(long limit) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DISK_LIMIT_FILE))) {
            writer.write(String.valueOf(limit));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
