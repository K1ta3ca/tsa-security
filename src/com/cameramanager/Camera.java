package com.cameramanager;

import java.util.List;

public class Camera {
    private String ipAddress;
    private String name;
    private List<Integer> openPorts;

    public Camera(String ipAddress, List<Integer> openPorts) {
        this.ipAddress = ipAddress;
        this.openPorts = openPorts;
        this.name = "Camera @ " + ipAddress; // Временно име
    }

    public Camera(String ipAddress, String name, List<Integer> openPorts) {
        this.ipAddress = ipAddress;
        this.name = name;
        this.openPorts = openPorts;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public List<Integer> getOpenPorts() {
        return openPorts;
    }

    public void setOpenPorts(List<Integer> openPorts) {
        this.openPorts = openPorts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        // Това ще се показва в списъка с камери
        return name;
    }
}
