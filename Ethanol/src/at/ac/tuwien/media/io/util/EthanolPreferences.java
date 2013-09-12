package at.ac.tuwien.media.io.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import at.ac.tuwien.media.IEthanol;
import at.ac.tuwien.media.R;
import at.ac.tuwien.media.util.Configuration;
import at.ac.tuwien.media.util.EthanolLogger;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.Value.EDirection;
import at.ac.tuwien.media.util.exception.EthanolException;

/**
 * {@link EthanolPreferences} represents the preferences for Ethanol.
 *  
 * @author jakob.frohnwieser@gmx.at
 */
public class EthanolPreferences extends PreferenceActivity {
	private static IEthanol ethanol;
	private static boolean needRestart;
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new EthanolPreferenceFragment()).commit();
        EthanolPreferenceFragment.setContext(this);
        
        needRestart = false;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		// display all debug messages
		// to show new settings
		EthanolLogger.displayDebugMessage();
		
		// restart Ethanol if needed
		if (needRestart) {
			restartEthanol();
			
		// else update views
		} else {
			ethanol.skipToThumbnail(EDirection.FORWARD, 0);
		}
	}
	
	public static void setParent(final IEthanol parent) {
		EthanolPreferences.ethanol = parent;
	}
	
	private static void restartEthanol() {
		// set reset to true to rewrite images
		try {
			Configuration.set(Value.CONFIG_RESET, true);
		} catch (EthanolException e) {
			e.printStackTrace();
		} finally {
			// restart Ethanol
			ethanol.restart();
		}
	}
	
	// subclass for preference fragment
	public static class EthanolPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
		private static PreferenceActivity preferenceActivity;
		
        @Override
        public void onCreate(final Bundle savedInstanceState)  {
            super.onCreate(savedInstanceState);
            
            // load preferences and set view
            loadPreferences();
            addPreferencesFromResource(R.xml.preferences);
        }
        
        @Override
        public void onResume() {
            super.onResume();
            // register listener
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
        	// unregister listener
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        // for boolean values
		@Override
		public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
			try {
				if (key.equals(Value.CONFIG_AUTOSAVE) ||
					key.equals(Value.CONFIG_CROP_IMAGES) ||
					key.equals(Value.CONFIG_DEBUG) ||
					key.equals(Value.CONFIG_JUMP_BACK) ||
					key.equals(Value.CONFIG_PREVIEW_BACK) ||
					key.equals(Value.CONFIG_ROTATE_IMAGES) ||
					key.equals(Value.CONFIG_SWIPE_SCROLL) ||
					key.equals(Value.CONFIG_SWIPE_SELECT) ||
					key.equals(Value.CONFIG_SWIPE_SLIDE) ||
					key.equals(Value.CONFIG_SWIPE_VERTICAL)) {
					Configuration.set(key, sharedPreferences.getBoolean(key, false));
					
					needRestart = key.equals(Value.CONFIG_ROTATE_IMAGES) || key.equals(Value.CONFIG_CROP_IMAGES);
				}
			} catch (EthanolException e) {
				e.printStackTrace();
			}
		}
		
		// for values that need to be confirmed
		@Override
		public boolean onPreferenceTreeClick(final PreferenceScreen preferenceScreen, final Preference preference) {
			// if reset was selected show confirm dialog
			if (preference.getKey().equals(Value.CONFIG_RESET)) {
				new AlertDialog.Builder(preferenceActivity)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.settings_reset_title)
					.setMessage(getResources().getString(R.string.settings_reset_summary) + "?")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								try {
									// reset configuration file and reload view
									needRestart = Configuration.resetConfigurationFile();
									
									// close this preference view
									preferenceActivity.onBackPressed();
									
									// open a new preference view with updated values
									ethanol.onOptionsItemSelected(R.id.menu_settings);
								} catch (EthanolException e) {
									e.printStackTrace();
								}
							}
						})
					.setNegativeButton(android.R.string.cancel, null)
					.show();
				
				return true;
				
			// if reload was selected show confirm dialog	
			} else if (preference.getKey().equals(Value.CONFIG_RELOAD)) {
				new AlertDialog.Builder(preferenceActivity)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.settings_reload_title)
					.setMessage(getResources().getString(R.string.settings_reload_summary) + "?")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// simply restart Ethanol
								// this also forces the program to re-create the thumbnail files
								restartEthanol();
							}
						})
					.setNegativeButton(android.R.string.cancel, null)
					.show();
				
				return true;
			
			// if delete was selected show confirm dialog	
			} else if (preference.getKey().equals(Value.CONFIG_DELETE)) {
				new AlertDialog.Builder(preferenceActivity)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.settings_delete_title)
					.setMessage(getResources().getString(R.string.settings_delete_summary) + "?")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// delete all files on the system and exits the app
								ethanol.deleteAllFiles();
								
								// close the preference activity
								preferenceActivity.finish();
							}
						})
					.setNegativeButton(android.R.string.cancel, null)
					.show();
				
				return true;
			}
			
			return false;
		}
		
		public static void setContext(final PreferenceActivity context) {
			EthanolPreferenceFragment.preferenceActivity = context;
		}
		
		// sets the values from the configuration file in the view
		private void loadPreferences() {
			final Editor editor = PreferenceManager.getDefaultSharedPreferences(preferenceActivity).edit();
			
			editor.putBoolean(Value.CONFIG_AUTOSAVE, Configuration.getAsBoolean(Value.CONFIG_AUTOSAVE));
			editor.putBoolean(Value.CONFIG_CROP_IMAGES, Configuration.getAsBoolean(Value.CONFIG_CROP_IMAGES));
			editor.putBoolean(Value.CONFIG_DEBUG, Configuration.getAsBoolean(Value.CONFIG_DEBUG));
			editor.putString(Value.CONFIG_IMAGE_FOLDER, Configuration.getAsString(Value.CONFIG_IMAGE_FOLDER));
			editor.putBoolean(Value.CONFIG_JUMP_BACK, Configuration.getAsBoolean(Value.CONFIG_JUMP_BACK));
			editor.putBoolean(Value.CONFIG_PREVIEW_BACK, Configuration.getAsBoolean(Value.CONFIG_PREVIEW_BACK));
			editor.putBoolean(Value.CONFIG_RESET, Configuration.getAsBoolean(Value.CONFIG_RESET));
			editor.putBoolean(Value.CONFIG_ROTATE_IMAGES, Configuration.getAsBoolean(Value.CONFIG_ROTATE_IMAGES));
			editor.putBoolean(Value.CONFIG_SWIPE_SCROLL, Configuration.getAsBoolean(Value.CONFIG_SWIPE_SCROLL));
			editor.putBoolean(Value.CONFIG_SWIPE_SELECT, Configuration.getAsBoolean(Value.CONFIG_SWIPE_SELECT));
			editor.putBoolean(Value.CONFIG_SWIPE_SLIDE, Configuration.getAsBoolean(Value.CONFIG_SWIPE_SLIDE));
			editor.putBoolean(Value.CONFIG_SWIPE_VERTICAL, Configuration.getAsBoolean(Value.CONFIG_SWIPE_VERTICAL));
			
			editor.commit();
		}
    }
}