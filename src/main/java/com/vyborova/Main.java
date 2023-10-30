package com.vyborova;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    private static String iPsRange;
    private static int streamsQuantity;
    private static volatile List<String> hostnames = new ArrayList<>();
    private static List<IPThread> threads = new ArrayList<>();

    public static void main(String[] args) {
        startApplication();
    }

    private static void startApplication() {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/ip_handler", Location.CLASSPATH);
        }).start(7070);

        app.post("/sort-ip", ctx -> {
            ctx.req().setCharacterEncoding("UTF-8");
            ctx.res().setCharacterEncoding("UTF-8");
            ctx.contentType("text/html; charset=UTF8");
            iPsRange = ctx.formParam("ips");
            streamsQuantity = Integer.parseInt(Objects.requireNonNull(ctx.formParam("streams")));
            ctx.html(sortIPs());
        });
    }

    protected static void addHostname(String hostname) {
        Main.hostnames.add(hostname);
        System.out.println("Added: " + hostname);
    }

    private static void scanIPs(IP startIP, IP endIP) {
        threads.add(new IPThread(startIP, endIP));
        threads.get(threads.size() - 1).start();
    }

    public static String intToIp(int ip) {
        return String.format("%d.%d.%d.%d", (ip >> 24) & 0xFF, (ip >> 16) & 0xFF, (ip >> 8) & 0xFF, ip & 0xFF);
    }

    protected static String sortIPs() throws Exception {
        String[] parts = iPsRange.replaceAll("([./])", " ").split(" ");
        int network = 0;
        int subnetLength;

        if (iPsRange.matches("(\\d{1,3}\\.){3}\\d{0,3}/\\d{1,2}")) {
            subnetLength = Integer.parseInt(parts[4]);
            for (int i = 0; i < 4; i++) {
                int octet = Integer.parseInt(parts[i]);
                if (octet < 0 || octet > 255) {
                    System.out.println("Неверный IP-адрес");
                    break;
                }
                network += octet << (24 - 8 * i);
            }

            System.out.println("Переведенное значение network: " + network);
        } else {
            throw new Exception("Неверный формат IP-адреса");
        }

        int mask = 0xffffffff << (32 - subnetLength);
        int startIp = (network & mask) + 1;
        int endIp = (network | ~mask) - 1;

        IP startIP = new IP(intToIp(startIp));
        IP endIP = new IP(intToIp(endIp));
        sortIPForStream(startIP, endIP);

        for (IPThread thread : threads) {
            thread.join();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new BufferedWriter(new FileWriter("rsl.txt", true)))) {
            for (String hostname : hostnames) {
                bufferedWriter.write(hostname);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Данные сохранены успешно";
    }

    private static void sortIPForStream(IP startIP, IP endIP) {
        long ipRangeSize = endIP.toLong() - startIP.toLong() + 1;
        long perStream = ipRangeSize / streamsQuantity;
        long remainder = ipRangeSize % streamsQuantity;
        IP currentStartIP = startIP;
        IP currentEndIP;

        for (int i = 0; i < streamsQuantity; i++) {
            long rangeSize = perStream;
            if (i < remainder) {
                rangeSize++;
            }
            currentEndIP = currentStartIP.add(rangeSize - 1);
            scanIPs(currentStartIP, currentEndIP);
            currentStartIP = currentEndIP.add(1);
        }
    }
}

