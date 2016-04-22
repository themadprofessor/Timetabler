package me.timetabler.installer;

import javafx.concurrent.Task;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.AccessDeniedException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.List;

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
     * Initaliises the class. The installPath must exist, be a directory and have write permissions.
     * @param installPath The path to install the system to.
     * @param password The password for the root user.
     */
    public InstallThread(File installPath, char[] password) {
        this.installPath = installPath;
        this.password = password;
    }

    /**
     * Installs the timetabler to the installPath member with a root password for the password member.
     * @return Nothing.
     * @throws FileNotFoundException Thrown if the install path does not exist.
     * @throws AccessDeniedException Thrown if the install path does not have write permissions for the current user.
     * @throws NotDirectoryException Thrown if the install path is not a directory.
     * @throws IOException Thrown IO error occurs.
     */
    @Override
    protected Void call() throws IOException {
        //Ensure the installPath is correct
        if (!installPath.exists()) {
            throw new FileNotFoundException(installPath.getPath());
        } else if (!installPath.isDirectory()) {
            throw new NotDirectoryException(installPath.getPath());
        } else if (!installPath.canWrite()) {
            throw new AccessDeniedException(installPath.getPath());
        }

        updateMessage("Extracting system.");
        updateProgress(1, 5);
        RandomAccessFile sevenZip = null;
        try {
            SevenZip.initSevenZipFromPlatformJAR();
            sevenZip = new RandomAccessFile("assets/Timetabler.7z", "r");
            System.out.println("Extracting Timetabler.7z");

            //Check if the task has been cancelled, and stop if it has.
            if (isCancelled()) {
                updateMessage("Cancelled");
                updateProgress(-1, 5);
                return null;
            }
            extract(sevenZip, installPath);
        } catch (SevenZipNativeInitializationException e) {
            e.printStackTrace();
            updateProgress(-1, 5);
            updateMessage("ERROR [" + e.getMessage() + ']');
            success = false;
        } finally {
            try {
                if (sevenZip != null) sevenZip.close();
            } catch (IOException e) {
                displayException(e);
                success = false;
            }
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

        RandomAccessFile mariaZip;
        if (os == OperatingSystem.WINDOWS_X64 || os == OperatingSystem.WINDOWS_X86) {
            mariaZip = new RandomAccessFile("assets/mariadb-" + os.getName() + ".7z", "r");
        } else {
            mariaZip = new RandomAccessFile("assets/mariadb-" + os.getName() + ".tar.gz", "r");
        }
        //The archives contain the folder 'mariadb' as the top entry.
        extract(mariaZip, installPath);
        mariaZip.close();
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
     * Extracts the given sevenZip file to the given outFolder. The given outFolder must not be null, exist, be a
     * directory and have write permissions.
     * @param sevenZip The archive to extract.
     * @param outFolder The folder to extract the archive to.
     */
    private void extract(RandomAccessFile sevenZip, File outFolder) {
        assert outFolder.exists() && outFolder.isDirectory() && outFolder.canWrite() : "outFolder must exist, be a directory and have write permissions!";

        IInArchive inArchive = null;
        try {
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(sevenZip));
            inArchive.extract(null, false, new ExtractCallback(inArchive, outFolder));
        } catch (SevenZipException e) {
            e.printStackTrace();
            updateProgress(-1, 5);
            updateMessage("ERROR [" + e.getMessage() + ']');
            success = false;
        } finally {
            if (inArchive != null) try {
                inArchive.close();
            } catch (SevenZipException e) {
                e.printStackTrace();
                updateProgress(-1, 5);
                updateMessage("ERROR [" + e.getMessage() + ']');
                success = false;
            }
        }
    }

    private void installDb(File dataDir, File baseDir) {
        //Setup command to install system tables
        List<String> cmd = new ArrayList<>();
        if (OperatingSystem.getCurrentOs() == OperatingSystem.WINDOWS_X64 || OperatingSystem.getCurrentOs() == OperatingSystem.WINDOWS_X86) {
            cmd.add(baseDir.getPath() + "/bin/mysql_install_db.exe");
        } else {
            cmd.add(baseDir.getPath() + "bin/mysqld");
            cmd.add("--no-defaults");
            cmd.add("--console");
            cmd.add("--skip-grant-tables");
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
