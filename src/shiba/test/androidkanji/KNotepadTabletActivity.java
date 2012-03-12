package shiba.test.androidkanji;

import android.app.Activity;
import android.os.Bundle;

public class KNotepadTabletActivity extends Activity {
	KanjiListView _kanjiListView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }
	
	private void init(){
		_kanjiListView = (KanjiListView)findViewById(R.id.kanjiListView1);
	}
}
