package com.sumanth.windowsoptimizer;

import java.io.*;

public class WindowsOptimizer {
    public static void main(String[] args) {
        try {
            // Extract handle.exe from resources to a temporary file
            InputStream inputStream = WindowsOptimizer.class.getClassLoader().getResourceAsStream("handle.exe");
            if (inputStream == null) {
                System.err.println("handle.exe not found in resources");
                return;
            }
            File tempHandle = File.createTempFile("handle", ".exe");
            tempHandle.deleteOnExit();
            try (FileOutputStream outputStream = new FileOutputStream(tempHandle)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Run handle.exe on the temp directory
            String tempDir = System.getProperty("java.io.tmpdir");
            Process handleProcess = new ProcessBuilder(tempHandle.getAbsolutePath(), tempDir).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(handleProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(tempDir)) {
                    String pid = line.split(" ")[1].split(":")[0];
                    String processName = line.split(" ")[0];  // Get the process name
                    System.out.println("Killing process: " + processName + " with PID: " + pid);
                    // Run taskkill with elevated permissions using runas
                    Process killProcess = Runtime.getRuntime().exec("runas /user:Administrator \"taskkill /F /PID " + pid + "\"");
                    killProcess.waitFor();
                }
            }
            handleProcess.waitFor();

            // Delete all files in the Temp directory
            File tempFolder = new File(tempDir);
            for (File file : tempFolder.listFiles()) {
                if (!file.delete()) {
                    System.err.println("Failed to delete: " + file.getAbsolutePath());
                    // Log reason for failure
                    try {
                        System.out.println("File in use or permission issue for: " + file.getAbsolutePath());
                        // Attempt to unlock or give more time before retry
                        Thread.sleep(1000);
                        if (!file.delete()) {
                            System.err.println("Retry failed to delete: " + file.getAbsolutePath());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}