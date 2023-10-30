package com.vyborova;

import java.util.Objects;

class IP {
    private int octet1;
    private int octet2;
    private int octet3;
    private int octet4;

    public IP(int octet1, int octet2, int octet3, int octet4) {
        this.octet1 = octet1;
        this.octet2 = octet2;
        this.octet3 = octet3;
        this.octet4 = octet4;
    }

    public IP(String octet1, String octet2, String octet3, String octet4) {
        this.octet1 = Integer.parseInt(octet1);
        this.octet2 = Integer.parseInt(octet2);
        this.octet3 = Integer.parseInt(octet3);
        this.octet4 = Integer.parseInt(octet4);
    }

    public IP(IP ip) {
        this.octet1 = ip.getOctet1();
        this.octet2 = ip.getOctet2();
        this.octet3 = ip.getOctet3();
        this.octet4 = ip.getOctet4();
    }

    public IP(String ip) {
        if (ip.matches("(\\d{1,3}\\.){3}\\d{0,3}")) {
            String[] ipArr = ip.split("\\.");
            this.octet1 = Integer.parseInt(ipArr[0]);
            this.octet2 = Integer.parseInt(ipArr[1]);
            this.octet3 = Integer.parseInt(ipArr[2]);
            this.octet4 = Integer.parseInt(ipArr[3]);
        } else {
            System.out.println("Error with creating IP from string");
        }
    }

    public int getOctet1() {
        return octet1;
    }

    public void setOctet1(int octet1) {
        this.octet1 = octet1;
    }

    public int getOctet2() {
        return octet2;
    }

    public void setOctet2(int octet2) {
        this.octet2 = octet2;
    }

    public int getOctet3() {
        return octet3;
    }

    public void setOctet3(int octet3) {
        this.octet3 = octet3;
    }

    public int getOctet4() {
        return octet4;
    }

    public void setOctet4(int octet4) {
        this.octet4 = octet4;
    }

    public long toLong() {
        return (long) octet1 << 24 | (long) octet2 << 16 | (long) octet3 << 8 | octet4;
    }

    public IP add(long value) {
        long result = toLong() + value;
        return new IP((int) (result >> 24), (int) (result >> 16) & 0xFF, (int) (result >> 8) & 0xFF, (int) result & 0xFF);
    }

    public IP increment() {
        int newOctet4 = octet4 + 1;
        int newOctet3 = octet3;
        int newOctet2 = octet2;
        int newOctet1 = octet1;

        if (newOctet4 > 255) {
            newOctet4 = 0;
            newOctet3++;
        }
        if (newOctet3 > 255) {
            newOctet3 = 0;
            newOctet2++;
        }
        if (newOctet2 > 255) {
            newOctet2 = 0;
            newOctet1++;
        }

        return new IP(newOctet1, newOctet2, newOctet3, newOctet4);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IP ip = (IP) o;
        return octet1 == ip.octet1 && octet2 == ip.octet2 && octet3 == ip.octet3 && octet4 == ip.octet4;
    }

    @Override
    public int hashCode() {
        return Objects.hash(octet1, octet2, octet3, octet4);
    }

    @Override
    public String toString() {
        return octet1 +
                "." + octet2 +
                "." + octet3 +
                "." + octet4;
    }
}
