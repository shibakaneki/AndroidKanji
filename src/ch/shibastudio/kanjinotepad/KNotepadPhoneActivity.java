package ch.shibastudio.kanjinotepad;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TabHost.TabSpec;

public class KNotepadPhoneActivity extends Activity{
	
	public static TabHost tabHost;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs);
        init();
    }
	
	private void init(){
		tabHost = (TabHost)findViewById(R.id.tabhost);
		tabHost.setup();
		TabSpec ts = tabHost.newTabSpec("list");
		ts.setContent(R.id.tablist);
		ts.setIndicator("List", getResources().getDrawable(R.drawable.ic_tab_list));
		tabHost.addTab(ts);
		
		ts = tabHost.newTabSpec("anime");
		ts.setContent(R.id.tabanime);
		ts.setIndicator("Animation", getResources().getDrawable(R.drawable.ic_tab_anime));
		tabHost.addTab(ts);
		
		ts = tabHost.newTabSpec("info");
		ts.setContent(R.id.tabdesc);
		ts.setIndicator("Infos", getResources().getDrawable(R.drawable.ic_tab_info));
		tabHost.addTab(ts);
		
		ts = tabHost.newTabSpec("canvas");
		ts.setContent(R.id.tabdrawing);
		ts.setIndicator("Drawing", getResources().getDrawable(R.drawable.ic_tab_drawing));
		tabHost.addTab(ts);
		
		TabWidget tw = tabHost.getTabWidget();
		for(int i=0; i<tw.getChildCount(); i++){
			View v = tw.getChildAt(i);
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_selector));
		}
	}
}
