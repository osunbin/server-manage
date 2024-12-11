package com.bin.sm.common;

import java.util.Objects;

public class Address {
    private String ip;
    private int port;

    private int weight;

    public Address(String address) {
        String[] split = address.split(":");
        this.ip = split[0];
        this.port = Integer.parseInt(split[1]);
    }

    public Address(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return port == address.port && Objects.equals(ip, address.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
