package com.example.cataloguemoviefinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.cataloguemoviefinal.fragment.AlarmPreferenceFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {
	
	@BindView(R.id.settings_toolbar)
	Toolbar settingsToolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
		
		ButterKnife.bind(this); // Bind view into ButterKnife 3rd party
		
		setSupportActionBar(settingsToolbar); // Set support action bar into Toolbar
		
		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle(getString(R.string.settings)); // Set title in action bar
			getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Set home button (to navigate to main activity)
		}
		
		getSupportFragmentManager().beginTransaction().add(R.id.alarm_settings, new AlarmPreferenceFragment()).commit(); // Attach AlarmPreferenceFragment to root layout in activity_settings.xml {@link LinearLayout}
	}
	
	
	
}
