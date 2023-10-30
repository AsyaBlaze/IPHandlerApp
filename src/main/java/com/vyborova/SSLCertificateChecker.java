package com.vyborova;

import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.security.cert.Certificate;

public class SSLCertificateChecker {
    public static String sslCertificate(String ipAddress) {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            SSLSocketFactory factory = context.getSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket(ipAddress, 443);
            socket.startHandshake();

            SSLSession session = socket.getSession();
            Certificate[] serverCerts = session.getPeerCertificates();

            if (serverCerts.length > 0) {
                X509Certificate cert = (X509Certificate) serverCerts[0];
                return cert.getSubjectDN().getName().substring(3);
            }

            socket.close();
        } catch (Exception e) {
            return null;
        }
        return null;
    }

}





