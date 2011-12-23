package shiba.test.androidkanji;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SplashActivity extends Activity {
	public static KanjiDBHelper _KDBHelper;
	
	private class CreateKanjiDBTask extends AsyncTask<KanjiDBHelper, Void, Void>{

		@Override
		protected Void doInBackground(KanjiDBHelper... dbHelper) {
			
			if(dbHelper.length > 0){
		        try{
		        	// If the database is not created, create it
		        	dbHelper[0].createDatabase();
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
        _KDBHelper = new KanjiDBHelper(this);
        new CreateKanjiDBTask().execute(_KDBHelper);
	}
}
