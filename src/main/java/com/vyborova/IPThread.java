package com.vyborova;

public class IPThread extends Thread {
    private IP startIP;
    private IP endIP;

    public IPThread(IP startIP, IP endIP) {
        this.startIP = startIP;
        this.endIP = endIP;
    }

    @Override
    public void run() {
        while (!startIP.equals(endIP)) {
            String hostname = SSLCertificateChecker.sslCertificate(startIP.toString());
            if (hostname != null)
                Main.addHostname(hostname);
            startIP = startIP.increment();
        }
        String hostname = SSLCertificateChecker.sslCertificate(endIP.toString());
        if (hostname != null)
            Main.addHostname(hostname);
    }
}
