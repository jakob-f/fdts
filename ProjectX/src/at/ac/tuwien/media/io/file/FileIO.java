package at.ac.tuwien.media.io.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import at.ac.tuwien.media.util.exception.XException;

/**
 * The {@link FileIO} class handles the whole reading and writing files of type
 * {@link File}.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class FileIO {
	
	
	/**
	 * Writes a {@link File} with the given data to the filesystem.
	 * 
	 * @param file the {@link File} to write
	 * @param data the <code>byte[]</code> content of the file to write
	 * @throws XException thrown if the file cannot be written
	 * or the {@link FileOutputStream} cannot be closed
	 */
	public static void write(final File file, final byte[] data) throws XException {
		if (file != null && data != null && data.length > 0) {
			// try to save a file the file system
			FileOutputStream fos = null;
			try {
				file.setWritable(true);
				file.createNewFile();
				
				fos = new FileOutputStream(file);
				fos.write(data);
			} catch (IOException ioe) {
				// something went wrong
				throw new XException("cannot write file to filesystem", ioe);
			} finally {
				try {
					// close output stream
					if (fos != null) {
						fos.flush();
						fos.close();
					}
				} catch (IOException ioe) {
					throw new XException("Cannot close output stream" , ioe);
				}
			}
		}
	}

	/**
	 * Deletes all subdirectories of a given folder
	 * 
	 * @param folder the folder for the subdirectories to delete
	 */
	public static void deleteSubdirectories(final File folder) {
		if (folder.isDirectory()) {
	        for (File childFolder : folder.listFiles()) {
	        	if (childFolder.isDirectory()) { 
	        		delete(childFolder);
	        	}
	        }
	    }
	}

	/**
	 * Recursively deletes the the given file or folder of type {@link File}.
	 * 
	 * @param fileOrFolder the file or folder to delete
	 */
	public static void delete(final File fileOrFolder) {
		if (fileOrFolder.isDirectory()) {
			for (File childFolder : fileOrFolder.listFiles()) {
				delete(childFolder);
			}
		}

		fileOrFolder.delete();
	}
}