package com.cameramanager;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PortScanner {

    // Най-често срещани портове за IP камери
    private static final int[] COMMON_PORTS = {80, 8080, 554, 88, 8000, 8081};

    public static List<Integer> findOpenPorts(String ipAddress, int timeout) {
        ExecutorService executor = Executors.newFixedThreadPool(COMMON_PORTS.length);
        List<Future<Integer>> futures = new ArrayList<>();
        List<Integer> openPorts = new ArrayList<>();

        for (int port : COMMON_PORTS) {
            futures.add(executor.submit(() -> {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ipAddress, port), timeout);
                    socket.close();
                    return port;
                } catch (Exception ex) {
                    return -1; // Портът е затворен или недостъпен
                }
            }));
        }

        for (Future<Integer> future : futures) {
            try {
                int port = future.get();
                if (port != -1) {
                    openPorts.add(port);
                }
            } catch (Exception ex) {
                // Грешка при изпълнението
            }
        }

        executor.shutdown();
        return openPorts;
    }
}
