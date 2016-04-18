package me.timetabler.installer;

import javafx.concurrent.Task;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.AccessDeniedException;
import java.nio.file.NotDirectoryException;

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
     * A class-wide flag to determine if the install was a success.
     */
    boolean success = true;

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
            extract7z(sevenZip, installPath);
        } catch (SevenZipNativeInitializationException e) {
            e.printStackTrace();
            updateProgress(-1, 5);
            updateMessage("ERROR [" + e.getMessage() + ']');
            success = false;
        } finally {
            if (sevenZip != null) sevenZip.close();
        }

        //Check if the task has been cancelled, and stop if it has.
        if (isCancelled()) {
            updateMessage("Cancelled");
            updateProgress(-1, 5);
            return null;
        }
        try {
            OperatingSystem os = OperatingSystem.getCurrentOs();
            System.out.println("Current OS [" + os + ']');
            RandomAccessFile mariaZip = null;
            switch (os) {
                case WINDOWS_X64:
                    mariaZip = new RandomAccessFile("assets/mariadb-windows-x86_64.7z", "r");
                    break;
                case WINDOWS_X86:
                    mariaZip = new RandomAccessFile("assets/mariadb-windows-i686.7z", "r");
                    break;
                case LINUX_X64:
                    mariaZip = new RandomAccessFile("assets/mariadb-linux-x86_64.7z", "r");
                    break;
                case LINUX_X86:
                    mariaZip = new RandomAccessFile("assets/mariadb-linux-i686.7z", "r");
                    break;
            }

            //The archives contain the folder 'mariadb' as the top entry.
            extract7z(sevenZip, installPath);
        } catch (UnsupportedOperationException e) {
            //No need to change message as it shows the currently supported operating systems and the exception info.
            updateMessage("[ERROR] " + e.getMessage());
            updateProgress(-1, 5);
            e.printStackTrace();
        }

        //Check if the task has been cancelled, and stop if it has.
        if (isCancelled()) {
            updateMessage("Cancelled");
            updateProgress(-1, 5);
            return null;
        }

        if (success) {
            updateMessage("Done.");
            updateProgress(5, 5);
        } else  {
            System.out.println("Completed with problems.");
        }
        //Return nothing.
        return null;
    }

    /**
     * Extracts the given sevenZip file to the given outFolder. The given outFolder must not be null, exist, be a
     * directory and have write permissions.
     * @param sevenZip The archive to extract.
     * @param outFolder The folder to extract the archive to.
     */
    private void extract7z(RandomAccessFile sevenZip, File outFolder) {
        assert outFolder.exists() && outFolder.isDirectory() && outFolder.canWrite() : "outFolder must exist, be a directory and have write permissions!";

        IInArchive inArchive = null;
        try {
            inArchive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, new RandomAccessFileInStream(sevenZip));
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
            }
        }
    }
}
