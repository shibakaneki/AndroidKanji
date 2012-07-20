package ch.shibastudio.kanjinotepad;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class KNotepadPhoneActivity extends Activity{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);
        init();
    }
	
	private void init(){
		TabHost th = (TabHost)findViewById(R.id.tabhost);
		th.setup();
		TabSpec ts = th.newTabSpec("list");
		ts.setContent(R.id.tablist);
		ts.setIndicator("List", getResources().getDrawable(R.drawable.ic_tab_list));
		th.addTab(ts);
		
		ts = th.newTabSpec("anime");
		ts.setContent(R.id.tabanime);
		ts.setIndicator("Animation", getResources().getDrawable(R.drawable.ic_tab_anime));
		th.addTab(ts);
		
		ts = th.newTabSpec("info");
		ts.setContent(R.id.tabdesc);
		ts.setIndicator("Infos", getResources().getDrawable(R.drawable.ic_tab_info));
		th.addTab(ts);
		
		ts = th.newTabSpec("canvas");
		ts.setContent(R.id.tabdrawing);
		ts.setIndicator("Drawing", getResources().getDrawable(R.drawable.ic_tab_drawing));
		th.addTab(ts);
	}
}
