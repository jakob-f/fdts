package at.ac.tuwien.media.methanol.io.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import at.ac.tuwien.media.methanol.util.exception.MethanolException;

/**
 * The {@link FileIO} class handles the whole reading and writing files of type {@link File}.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class FileIO {
	
	/**
	 * 
	 * Reads a the given {@link File} from the filesystem.
	 * 
	 * @param file the {@link File} to read
	 * @return a String with the content of the given file
	 * @throws MethanolException thrown if the file cannot be read
	 * or the {@link BufferedReader} cannot be closed
	 */
	public static String read(final File file) throws MethanolException {
		BufferedReader br = null;
		final StringBuilder sb = new StringBuilder();
		 
		try {
			br = new BufferedReader(new FileReader(file));
			
			String nextLine;
 
			while ((nextLine = br.readLine()) != null) {
				sb.append(nextLine).append("\n");
			}
 
		} catch (IOException ioe) {
			// something went wrong
			throw new MethanolException("cannot read file from filesystem", ioe);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ioe) {
				throw new MethanolException("Cannot close buffered reader" , ioe);
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Writes a {@link File} with the given data to the filesystem.
	 * 
	 * @param file the {@link File} to write
	 * @param data the <code>byte[]</code> content of the file to write
	 * @throws MethanolException thrown if the file cannot be written
	 * or the {@link FileOutputStream} cannot be closed
	 */
	public static void write(final File file, final byte[] data) throws MethanolException {
		if (file != null && data != null && data.length > 0) {
			// try to save a file the file system
			FileOutputStream fos = null;
			try {
				file.setWritable(true);
				if (file.createNewFile()) {
					fos = new FileOutputStream(file);
					fos.write(data);
				}				
			} catch (IOException ioe) {
				// something went wrong
				throw new MethanolException("cannot write file to filesystem", ioe);
			} finally {
				try {
					// close output stream
					if (fos != null) {
						fos.flush();
						fos.close();
					}
				} catch (IOException ioe) {
					throw new MethanolException("Cannot close output stream" , ioe);
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
	public static boolean delete(final File fileOrFolder) {
	    if (fileOrFolder.isDirectory()) {
	        for (File childFolder : fileOrFolder.listFiles()) {
	        	return delete(childFolder);
	        }
	    }
	    
	    return fileOrFolder.delete();
	}
}