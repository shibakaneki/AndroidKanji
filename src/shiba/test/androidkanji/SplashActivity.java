package shiba.test.androidkanji;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;

public class SplashActivity extends Activity {
	private KanjiDBHelper _KDBHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        // TODO : 	- Create my own Splashscreen (with low/middle/high resolutions)
        //			- Add an animated loading gif
        //			- Perform the DB creation during this time (using AsyncTask)
        //			- Display the AndroidKanjiActivity once the DB is created
        
        /*_KDBHelper = new KanjiDBHelper(this);

        try{
        	// If the database is not created, create it
        	_KDBHelper.createDatabase();
        }catch(IOException e){
        	throw new Error("Unable to create database!");
        }
        
        try{
        	_KDBHelper.openDatabase();
        }catch(java.sql.SQLException e){
        	throw new Error(e.getMessage());
        }
        
        _KDBHelper.close();
        */
	}
}
