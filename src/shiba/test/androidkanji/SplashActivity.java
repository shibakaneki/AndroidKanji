package shiba.test.androidkanji;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;

public class SplashActivity extends Activity {
	public static KanjiDBHelper _KDBHelper;
	
	private class CreateKanjiDBTask extends AsyncTask<KanjiDBHelper, Void, Void>{

		@Override
		protected Void doInBackground(KanjiDBHelper... dbHelper) {
			
			if(dbHelper.length > 0){
		        try{
		        	dbHelper[0].initDB();
		        	dbHelper[0].openDatabase();
		        }catch(java.sql.SQLException e){
		        	throw new Error(e.getMessage());
		        }catch(IOException e){
		        	throw new Error(e.getMessage());
		        }
		        
		        dbHelper[0].close();

			}	        
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			// The DB has been (or was already) created. Now we show the main View!
			// TODO : Check the resolution of the device and load the right Activity
			//Intent intent = new Intent(SplashActivity.this, AndroidKanjiActivity.class);
			Intent intent;
			
			// For the moment, only the tablet is supported
			intent = new Intent(SplashActivity.this, KNotepadTabletActivity.class);
			finish();
			startActivity(intent);
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
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
