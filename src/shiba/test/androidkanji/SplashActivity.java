package shiba.test.androidkanji;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

public class SplashActivity extends Activity {
	public static KanjiDBHelper _KDBHelper;
	// TODO : Create a FavoriteDBHelper class 
	
	private class CreateKanjiDBTask extends AsyncTask<KanjiDBHelper, Void, Void>{

		@Override
		protected Void doInBackground(KanjiDBHelper... dbHelper) {
			
			if(dbHelper.length > 0){
		        try{
		        	// If the database is not created, create it
		        	dbHelper[0].createDatabase(KanjiDBHelper.DB_NAME);
		        	dbHelper[0].createDatabase(KanjiDBHelper.FAVDB_NAME);
		        }catch(IOException e){
		        	throw new Error("Unable to create database!");
		        }
		        
		        try{
		        	dbHelper[0].openDatabase();
		        }catch(java.sql.SQLException e){
		        	throw new Error(e.getMessage());
		        }
		        
		        dbHelper[0].close();

			}	        
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			// The DB has been (or was already) created. Now we show the main View!
			Intent intent = new Intent(SplashActivity.this, AndroidKanjiActivity.class);
			startActivity(intent);
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        // TODO : 	- Create my own Splashscreen (Design with low/middle/high resolutions)
        //			- Add an animated loading gif
        //			- Check the presence of the favorites database and create it if it doesn't exist
        
        // Create the data directory in the sdcard
        setupSDCardDirectory();
        
        _KDBHelper = new KanjiDBHelper(this);
        new CreateKanjiDBTask().execute(_KDBHelper);
	}
	
	private void setupSDCardDirectory(){
    	String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.app_name);
    	
    	File directory = new File(path);
	    // Create the folder if it doesn't exist:
	    if (!directory.exists()) 
	    {
	        directory.mkdirs();
	    }
	}
}
