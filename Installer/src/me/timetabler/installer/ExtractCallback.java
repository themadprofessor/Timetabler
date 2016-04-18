package me.timetabler.installer;

import net.sf.sevenzipjbinding.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The callback for the sevenzip extraction. This class assumes the outFolder has write permissions.
 */
public class ExtractCallback implements IArchiveExtractCallback {
    /**
     * The archive being extracted.
     */
    private IInArchive in;

    /**
     * The folder the archive will be extracted to.
     */
    private File outFolder;

    /**
     * The outputStream which writes the file to the outFolder.
     */
    private BufferedOutputStream outStream;

    /**
     * Initialises the callback. The outFolder must have write permissions.
     * @param inArchive The archive being extracted.
     * @param outFolder The folder the archive will be extracted to.
     */
    public ExtractCallback(IInArchive inArchive, File outFolder) {
        this.in = inArchive;
        this.outFolder = outFolder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {
        closeStream();
        if (extractAskMode != ExtractAskMode.EXTRACT) {
            //We only do extraction here.
            return null;
        }

        if ((Boolean) in.getProperty(index, PropID.IS_FOLDER)) {
            return null;
        }

        File file = new File(outFolder, in.getStringProperty(index, PropID.PATH));
        file.getParentFile().mkdirs();
        BufferedOutputStream outputStream;
        try {
            file.createNewFile();
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (IOException e) {
            //Print stackTrace, then let sevenZip handle it.
            e.printStackTrace();
            throw new SevenZipException("Error opening [" + file.getPath() + ']', e);
        }

        //Return a stream which will write the data to the BufferedOutputStream outputStream.
        return data -> {
            int length;

            try {
                outputStream.write(data);
                length = data.length;
            } catch (IOException e) {
                //Print stackTrace, then let sevenZip handle it
                e.printStackTrace();
                throw new SevenZipException("Error writing to file [" + file.getPath() + ']', e);
            }

            return length;
        };
    }

    /**
     * {@inheritDoc}
     * Does not need implementing as there is nothing to prepare for.
     */
    @Override
    public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {

    }

    /**
     * {@inheritDoc}
     * Closes the backing stream, and logs an error if the extraction failed.
     */
    @Override
    public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
        closeStream();
        if (extractOperationResult != ExtractOperationResult.OK) {
            System.err.println("Extraction Error!");
        }
    }

    /**
     * {@inheritDoc}
     * Does not need implementing as there is no need to keep track of total.
     */
    @Override
    public void setTotal(long total) throws SevenZipException {

    }

    /**
     * {@inheritDoc}
     * Does not need implementing as there is no need to keep track of currently completed count.
     */
    @Override
    public void setCompleted(long complete) throws SevenZipException {

    }

    /**
     * Closes and nullifies outStream.
     */
    private void closeStream() throws SevenZipException {
        if (outStream != null) {
            try {
                outStream.close();
                outStream = null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new SevenZipException("Error closing output stream", e);
            }
        }
    }
}
