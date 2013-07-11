package at.ac.tuwien.media;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import at.ac.tuwien.media.io.file.Configuration;
import at.ac.tuwien.media.util.EthanolLogger;
import at.ac.tuwien.media.util.Value;
import at.ac.tuwien.media.util.exception.EthanolException;

/**
 * {@link EthanolPreferences} represents the preferences for Ethanol
 *  
 * @author jakob.frohnwieser@gmx.at
 */
public class EthanolPreferences extends PreferenceActivity {
	private static IEthanol parent;
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
		}
	}
	
	public static void setParent(IEthanol parent) {
		EthanolPreferences.parent = parent;
	}
	
	private static void restartEthanol() {
		// set reset to true to rewrite images
		try {
			Configuration.set(Value.CONFIG_RESET, true);
		} catch (EthanolException e) {
			e.printStackTrace();
		} finally {
			// restart Ethanol
			parent.restart();
		}
	}
	
	// subclass for preference fragment
	public static class EthanolPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
		private static Context context;
		
        @Override
        public void onCreate(final Bundle savedInstanceState)  {
            super.onCreate(savedInstanceState);
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

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			try {
				if (key.equals(Value.CONFIG_ROTATE_IMAGES) ||
					key.equals(Value.CONFIG_WARP_IMAGES) ||
					key.equals(Value.CONFIG_DEBUG)) {
					Configuration.set(key, sharedPreferences.getBoolean(key, Value.CONFIG_DEFAULT_VALUE_ROTATE_IMAGES));
					
					needRestart = key.equals(Value.CONFIG_ROTATE_IMAGES) || key.equals(Value.CONFIG_WARP_IMAGES);
				}
			} catch (EthanolException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
			// if reload was selected show confirm dialog
			if (preference.getKey().equals(Value.CONFIG_RESET)) {
				new AlertDialog.Builder(context)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.settings_reset_title)
					.setMessage(getResources().getString(R.string.settings_reset_summary) + "?")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								try {
									// reset configuration file and reload view
									Configuration.resetConfigurationFile();
									loadPreferences();
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
				new AlertDialog.Builder(context)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.settings_reload_title)
					.setMessage(getResources().getString(R.string.settings_reload_summary) + "?")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								restartEthanol();
							}
						})
					.setNegativeButton(android.R.string.cancel, null)
					.show();
				
				return true;
			}
			
			return false;
		}
		
		public static void setContext(final Context context) {
			EthanolPreferenceFragment.context = context;
		}
		
		private void loadPreferences() {
			//TODO implement me!
		}
    }
}