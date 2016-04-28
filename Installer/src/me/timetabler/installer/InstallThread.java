package me.timetabler.installer;

import javafx.concurrent.Task;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A background thread which installs the system to the given path and sets the root password to be the given password.
 */
public class InstallThread extends Task<Void> {
    /**
     * The path to install the timetabler to, which must be directory, exist and have write permissions.
     */
    private File installPath;

    /**
     * The password for the root user to be. It is a char array not a string, to reduce overhead and increase security,
     * as no string operations will be run on the password.
     */
    private char[] password;

    /**
     * A class-wide flag to specify if the install is not failing.
     */
    private boolean success = true;

    /**
     * Initialises the class. The installPath must exist, be a directory and have write permissions.
     * @param installPath The path to install the system to.
     * @param password The password for the root user.
     * @throws FileNotFoundException Thrown if the install path does not exist.
     * @throws AccessDeniedException Thrown if the install path does not have write permissions for the current user.
     * @throws NotDirectoryException Thrown if the install path is not a directory.
     * @throws IOException Thrown IO error occurs.
     */
    public InstallThread(File installPath, char[] password) throws IOException {
        //Ensure the installPath is correct
        if (!installPath.exists()) {
            throw new FileNotFoundException(installPath.getPath());
        } else if (!installPath.isDirectory()) {
            throw new NotDirectoryException(installPath.getPath());
        } else if (!installPath.canWrite()) {
            throw new AccessDeniedException(installPath.getPath());
        }
        this.installPath = installPath;
        this.password = password;
    }

    /**
     * Installs the timetabler to the installPath member with a root password for the password member.
     * @return Nothing.
     */
    @Override
    protected Void call() {
        updateMessage("Extracting system.");
        updateProgress(1, 5);
        try (TarArchiveInputStream in = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream("assets/Timetabler.tar.gz"))))) {
            extract(in, installPath);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            updateMessage("Failed to extract Timetabler! Ensure the user [" + System.getProperty("user.name")
                    + "] has read permissions for [" + new File("assets/Timetabler.tar.gz") + ']');
            updateProgress(-1, 5);
        }

        //Stop if the system could not be extracted.
        if (!success) return null;

        //Check if the task has been cancelled, and stop if it has.
        if (isCancelled()) {
            updateMessage("Cancelled");
            updateProgress(-1, 5);
            return null;
        }

        updateProgress(2, 5);
        updateMessage("Extracting Database System");
        OperatingSystem os = OperatingSystem.getCurrentOs();
        System.out.println("Current OS [" + os + ']');
        if (os == OperatingSystem.UNSUPPORTED) {
            updateMessage("Unsupported operating system/architecture! Please use a supported system. Consult the user manual for the supported system list.");
            updateProgress(-1, 5);
            return null;
        }

        File mariaZip = new File("assets/mariadb-" + os.getName() + ".tar.gz");

        try (TarArchiveInputStream in = new TarArchiveInputStream(
                new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(mariaZip))))) {
            extract(in, installPath);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            updateMessage("Could not extract database! Ensure the user [" + System.getProperty("user.name")
                    + "] has read permissions for [" + mariaZip + ']');
            updateProgress(-1, 5);
            success = false;
        }

        //Stop if the system could not be extracted.
        if (!success) return null;

        //Check if the task has been cancelled, and stop if it has.
        if (isCancelled()) {
            updateMessage("Cancelled");
            updateProgress(-1, 5);
            return null;
        }
        updateProgress(3, 5);
        updateMessage("Setting Up Database");
        File mariaDataDir = new File(installPath, "db");
        File mariaBaseDir = new File(installPath, "mariadb");
        installDb(mariaDataDir, mariaBaseDir);

        //Stop if the system could not be extracted.
        if (!success) return null;
        //Check if the task has been cancelled, and stop if it has.
        if (isCancelled()) {
            updateMessage("Cancelled");
            updateProgress(-1, 5);
            return null;
        }

        updateMessage("Done.");
        updateProgress(5, 5);
        //Return nothing.
        return null;
    }

    /**
     * Extracts the given archive stream into the given folder. The method is fail-fast, therefore if a single entry
     * could not be extracted, the method throws an IOException.
     * @param in The archive stream to be extracted.
     * @param outFolder The folder to be extracted into.
     * @throws IOException Thrown if an entry in the archive could not be extracted.
     */
    private void extract(ArchiveInputStream in, File outFolder) throws IOException {
        assert outFolder.isDirectory();
        ArchiveEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                File file = new File(outFolder, entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                    System.out.println("Created directory [" + entry.getName() + ']');
                } else {
                    Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Extracted file [" + entry.getName() + ']');
                }
            }
    }

    /**
     * Installs, secures and sets up the database. The data dir is the directory where the database will install the
     * tables. The base dir is the root directory of the database install directory, containing bin and lib.
     * @param dataDir The data dir for the database.
     * @param baseDir The base dir for the database.
     */
    private void installDb(File dataDir, File baseDir) {
        //Setup command to install system tables
        OperatingSystem os = OperatingSystem.getCurrentOs();
        List<String> cmd = new ArrayList<>();
        if (os == OperatingSystem.WINDOWS_X64 || os == OperatingSystem.WINDOWS_X86) {
            cmd.add(baseDir.getPath() + "/bin/mysql_install_db.exe");
        } else {
            cmd.add(baseDir.getPath() + "/scripts/mysql_install_db");
            cmd.add("--no-defaults");
            cmd.add("--basedir=" + baseDir);
            cmd.add("--datadir=" + dataDir);
        }

        try {
            Process process = new ProcessBuilder().inheritIO().command(cmd).start();
            //Make sure the tables where installed successfully
            if (process.waitFor() != 0) {
                success = false;
                return;
            }
        } catch (IOException | InterruptedException e) {
            displayException(e);
            success = false;
        }

        cmd.clear();
        if (os == OperatingSystem.WINDOWS_X64 || os == OperatingSystem.WINDOWS_X86) {
            cmd.add(baseDir.getPath() + "/bin/mysqld.exe");
        } else {
            cmd.add(baseDir.getPath() + "/bin/mysqld");
            cmd.add("--no-defaults");
            cmd.add("--basedir=" + baseDir);
            cmd.add("--datadir=" + dataDir);
            cmd.add("--socket=./mysqld.sock");
        }

        Process serverProcess = null;
        try {
            serverProcess = new ProcessBuilder().command(cmd).start();
            Scanner scanner = new Scanner(serverProcess.getErrorStream());
            String line;
            while ((line = scanner.nextLine()) != null) {
                //The server outputs 'ready for connections' upon finishing initialisation, and 'shutdown' when closing.
                if (line.contains("ready for connections")) {
                    break;
                } else if (line.contains("shutdown")) {
                    try {
                        serverProcess.destroyForcibly().waitFor();
                        updateProgress(-1, 5);
                        updateMessage("Failed to start database process!");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            displayException(e);
            success = false;
        }


    }

    /**
     * Prints the stackTrace of the given exception, sets the process to indefinite and displays the error to the user.
     * @param e The exception to display.
     */
    private void displayException(Exception e) {
        e.printStackTrace();
        updateProgress(-1, 5);
        updateMessage("ERROR [" + e.getMessage() + ']');
    }
}
