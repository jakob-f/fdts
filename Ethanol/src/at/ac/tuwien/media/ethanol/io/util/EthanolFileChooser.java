package at.ac.tuwien.media.ethanol.io.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.ac.tuwien.media.ethanol.IEthanol;
import at.ac.tuwien.media.ethanol.R;
import at.ac.tuwien.media.ethanol.util.Configuration;
import at.ac.tuwien.media.ethanol.util.EthanolLogger;
import at.ac.tuwien.media.ethanol.util.Util;
import at.ac.tuwien.media.ethanol.util.Value;
import at.ac.tuwien.media.ethanol.util.exception.EthanolException;

/**
 * The {@link EthanolFileChooser} class displays a file chooser. It will only select files of type <code>JPEG</code>.
 * 
 * @author jakob.frohnwieser@gmx.at
 */
public class EthanolFileChooser implements OnItemClickListener, OnClickListener {
	private final List<File> directoryFiles;
	private File currentDirectory;
	private final ListView listView;
	private final Context parent;

	public EthanolFileChooser(final Context parent, String root) {
		this.parent = parent;
		directoryFiles = new ArrayList<File>();
		
		// set current directory to start with
		if (root == null || root.isEmpty()) {
			root = Value.SDCARD;
		}
		currentDirectory = new File(root);

		getSubdirectoriesAndImages();
		
		// show a dialog
		final AlertDialog alertDialog = new AlertDialog.Builder(parent)
			.setTitle(R.string.choose_folder)
			.setIcon(R.drawable.ic_search)
			.setAdapter(new EthanolDirectoryAdapter(parent, R.layout.file_chooser_item, directoryFiles), this)
			.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						try {
							// reset image folder path only if it changed
							// and at least one image file is present
							if (!currentDirectory.getAbsolutePath().equals(Configuration.getAsString(Value.CONFIG_IMAGE_FOLDER))) {
								
								// set new image folder
								Configuration.set(Value.CONFIG_IMAGE_FOLDER, currentDirectory.getAbsolutePath());
								EthanolLogger.addDebugMessage("Set new configuration folder "
										+ Configuration.getAsString(Value.CONFIG_IMAGE_FOLDER));
								
								// re-create the preview images
								// only reset if preview images are not already there
								if(!Util.getPreviewFolderForPath(currentDirectory.getAbsolutePath()).exists()) {
									Configuration.set(Value.CONFIG_RESET, true);
									EthanolLogger.addDebugMessage("Will write new preview images");
								}
							
								// finaly restart Ethanol
								((IEthanol) parent).restart();
							}
						} catch (EthanolException e) {
							// we cannot do anything against it
							e.printStackTrace();
						}
					}
					})
			.setNegativeButton(android.R.string.cancel, null)
			.create();
		
		listView = alertDialog.getListView();
		listView.setOnItemClickListener(this);	
		alertDialog.show();
	}
	
	@Override
	public void onItemClick(final AdapterView<?> arg0, final View arg1, final int position, final long arg3) {
		// check if current position is in boundaries
		if (position >= 0 && position < directoryFiles.size()) {
			final File selectedDirectory = directoryFiles.get(position);
			// must be directory or parent
			if (selectedDirectory.isDirectory() || selectedDirectory.getName().equals(parent.getResources().getString(R.string.parent))) {
				// get selected folder
				currentDirectory = selectedDirectory.getName().equals(parent.getResources().getString(R.string.parent)) ?
						currentDirectory.getParentFile()
						: directoryFiles.get(position);

				// reload file list view
				getSubdirectoriesAndImages();
				listView.setAdapter(new EthanolDirectoryAdapter(parent, R.layout.file_chooser_item, directoryFiles));
			}
		}
	}

	private void getSubdirectoriesAndImages() {
		// clean old file list
		directoryFiles.clear();

		// list all files in the current directory
		final File[] files = currentDirectory.listFiles();
		// add them to the directory list
		if (files != null) {
			for (File file : files) {
				// filter only folders and images (of type JPEG)
				if (file.isDirectory()
						|| file.getName().matches(Value.REGEX_IMAGE)) {
					directoryFiles.add(file);
				}
			}
			
			// sort them
			Collections.sort(directoryFiles);
		}

		// add parent directory to the begin of the list
		if (currentDirectory.getParent() != null) {
			directoryFiles.add(0, new File(parent.getResources().getString(R.string.parent)));
		}
	}
	
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// this method intentionally left blank
	}
	
	// Ethanol Directory Adapter Class
	public class EthanolDirectoryAdapter extends ArrayAdapter<File> {
		
		public EthanolDirectoryAdapter(final Context context, final int resourceId, final List<File> objects) {
			super(context, resourceId, objects);
		}
		
		@Override
        public View getView(final int position, final View convertView, final ViewGroup viewGroup) {
            final TextView textView = (TextView) super.getView(position, convertView, viewGroup);
            
            // get values for view
            final File directoryAtPosition = directoryFiles.get(position);
            final String fileName = directoryAtPosition == null ?
            		parent.getResources().getString(R.string.parent)
					: directoryAtPosition.getName();
            final int iconId = fileName.equals(parent.getResources().getString(R.string.parent)) ?
				R.drawable.ic_back
				: fileName.matches(Value.REGEX_IMAGE) ?
						R.drawable.ic_image
						: R.drawable.ic_folder;
            
            // set values
            textView.setText(" " + fileName);
            textView.setCompoundDrawablesWithIntrinsicBounds(parent.getResources().getDrawable(iconId), null, null, null );
 
            return textView;
        }
	}
}