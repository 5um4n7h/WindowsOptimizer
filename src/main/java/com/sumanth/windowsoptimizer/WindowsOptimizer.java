package com.sumanth.windowsoptimizer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

            // Get temp directory
            String tempDir = System.getProperty("java.io.tmpdir");

            // Loop through files in temp directory and attempt to delete
            File tempFolder = new File(tempDir);
            for (File file : tempFolder.listFiles()) {
                if (!file.isDirectory()) {
                    System.out.println("Checking file: " + file.getAbsolutePath());
                    // Run handle.exe to find processes using the file
                    Process handleProcess = new ProcessBuilder(tempHandle.getAbsolutePath(), file.getAbsolutePath()).start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(handleProcess.getInputStream()));
                    String line;
                    List<String> pids = new ArrayList<>();

                    // Parse handle.exe output and find PIDs
                    while ((line = reader.readLine()) != null) {
                        if (line.contains(file.getAbsolutePath())) {
                            String[] parts = line.split(" ");
                            String pid = parts[1].split(":")[0];
                            pids.add(pid);  // Store the PID of the process using the file
                            System.out.println("Process holding the file: PID = " + pid);
                        }
                    }

                    handleProcess.waitFor();

                    // If processes are using the file, kill them
                    for (String pid : pids) {
                        try {
                            System.out.println("Killing process with PID: " + pid);
                            // Kill the process using taskkill
                            Process killProcess = Runtime.getRuntime().exec("taskkill /F /PID " + pid);
                            killProcess.waitFor();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // Attempt to delete the file after killing the process
                    if (file.delete()) {
                        System.out.println("Deleted file: " + file.getAbsolutePath());
                    } else {
                        System.err.println("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}