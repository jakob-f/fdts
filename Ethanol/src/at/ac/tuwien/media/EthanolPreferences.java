package at.ac.tuwien.media;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * {@link EthanolPreferences} represents the preferences for Ethanol
 *  
 * @author jakob.frohnwieser@gmx.at
 */
public class EthanolPreferences extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}