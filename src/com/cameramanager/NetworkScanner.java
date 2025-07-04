package com.cameramanager;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NetworkScanner {

    public static List<String> findActiveHosts(String subnet) {
        ExecutorService executor = Executors.newFixedThreadPool(254);
        List<Future<String>> futures = new ArrayList<>();
        List<String> activeHosts = new ArrayList<>();

        for (int i = 1; i < 255; i++) {
            String host = subnet + "." + i;
            futures.add(executor.submit(() -> {
                try {
                    InetAddress address = InetAddress.getByName(host);
                    if (address.isReachable(1000)) {
                        return host;
                    }
                } catch (Exception e) {
                    // Грешка
                }
                return null;
            }));
        }

        for (Future<String> future : futures) {
            try {
                String host = future.get();
                if (host != null) {
                    activeHosts.add(host);
                }
            } catch (Exception e) {
                // Грешка
            }
        }

        executor.shutdown();
        return activeHosts;
    }
}
