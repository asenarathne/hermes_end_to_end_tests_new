package com.wiley.hermes.helpers;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.*;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FtpHelper {

    // Matches the method signature called in the Karate feature
    public static void uploadZippedResource(String host, int port, String user, String pass, String resourcePath, String remotePath) {
        FTPClient ftpClient = new FTPClient();
        try {
            System.out.println("Connecting to FTP: " + host + ":" + port);
            ftpClient.connect(host, port);
            
            if (!ftpClient.login(user, pass)) {
                throw new RuntimeException("FTP login failed for user: " + user);
            }
            
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Read the XML from src/test/resources
            InputStream is = FtpHelper.class.getResourceAsStream(resourcePath);
            if (is == null) throw new RuntimeException("Resource not found: " + resourcePath);

            // Zip it in memory (Mimicking the logic from legacy SpecificFtpStepdefs.java)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                zos.putNextEntry(new ZipEntry(UUID.randomUUID().toString() + ".xml"));
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            }

            // Upload the zipped bytes to the FTP path
            try (InputStream uploadStream = new ByteArrayInputStream(baos.toByteArray())) {
                boolean success = ftpClient.storeFile(remotePath, uploadStream);
                if (!success) {
                    throw new RuntimeException("FTP upload failed. Server reply: " + ftpClient.getReplyString());
                }
                System.out.println("✅ Uploaded zipped file to FTP: " + remotePath);
            }

        } catch (Exception e) {
            throw new RuntimeException("FTP operation failed", e);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                System.err.println("Failed to cleanly disconnect FTP: " + ex.getMessage());
            }
        }
    }

    // Uploads the raw file exactly as it is (No Zipping)
    public static void uploadRawResource(String host, int port, String user, String pass, String resourcePath, String remotePath) {
        FTPClient ftpClient = new FTPClient();
        try {
            System.out.println("Connecting to FTP: " + host + ":" + port);
            ftpClient.connect(host, port);
            
            if (!ftpClient.login(user, pass)) {
                throw new RuntimeException("FTP login failed for user: " + user);
            }
            
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Read the XML from src/test/resources
            InputStream is = FtpHelper.class.getResourceAsStream(resourcePath);
            if (is == null) throw new RuntimeException("Resource not found: " + resourcePath);

            // Create directories if they don't exist
            String[] folders = remotePath.split("/");
            String currentDir = "";
            for (int i = 0; i < folders.length - 1; i++) {
                if (!folders[i].isEmpty()) {
                    currentDir += "/" + folders[i];
                    ftpClient.makeDirectory(currentDir);
                }
            }

            // Upload the raw XML bytes to the FTP path
            boolean success = ftpClient.storeFile(remotePath, is);
            if (!success) {
                throw new RuntimeException("FTP upload failed. Server reply: " + ftpClient.getReplyString());
            }
            System.out.println("✅ Uploaded RAW file to FTP: " + remotePath);

        } catch (Exception e) {
            throw new RuntimeException("FTP operation failed", e);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                System.err.println("Failed to cleanly disconnect FTP: " + ex.getMessage());
            }
        }
    }

    // Deletes the remote file so the FTP Watcher sees a fresh "CREATE" event
    public static void deleteRemoteFile(String host, int port, String user, String pass, String remotePath) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(host, port);
            if (ftpClient.login(user, pass)) {
                ftpClient.enterLocalPassiveMode();
                boolean deleted = ftpClient.deleteFile(remotePath);
                if (deleted) System.out.println("🗑️ Cleaned up existing FTP file: " + remotePath);
            }
        } catch (Exception e) {
            System.err.println("FTP delete warning: " + e.getMessage());
        } finally {
            try { if (ftpClient.isConnected()) { ftpClient.logout(); ftpClient.disconnect(); } } catch (Exception ex) {}
        }
    }
}