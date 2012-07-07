package ch.shibastudio.kanjinotepad;

import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class KanjiListRowView extends LinearLayout{
	private Context mCtx;
	private KanjiDBHelper mKDBHelper;
	private TextView mCharacter;
	private ImageView mFavoriteStar;
	
	public KanjiListRowView(Context c){
		super(c);
		mCtx = c;
		init();
	}
	
	public KanjiListRowView(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
		LayoutInflater inflater = LayoutInflater.from(c);
		inflater.inflate(R.layout.kanji_list, this);
		init();
	}
	
	private void init(){
        mKDBHelper = new KanjiDBHelper(mCtx);
        mCharacter = (TextView)findViewById(R.id.kanjichar);
        mCharacter.setOnClickListener(onCharacterClicked);
        mFavoriteStar = (ImageView)findViewById(R.id.star);
	}
	
	private OnClickListener onCharacterClicked = new OnClickListener() {

		public void onClick(View v) {
			Toast.makeText(mCtx, "Character clicked!", Toast.LENGTH_SHORT).show();
/*			if(null != v){
				try {
					v.setBackgroundColor(R.color.fancy);
					mKDBHelper.openDatabase();
					KanjiInfo currentKanji = new KanjiInfo(((TextView)v).getText().toString(), false);
					int cp = TextTools.kanjiToCode(((TextView)v).getText().toString());
					Cursor c = mKDBHelper.getKanjiInfos(cp);
					
					// Get the JLPT level
					int jlpt = c.getInt(c.getColumnIndex(KanjiDBHelper.KEY_JLPT));
					// Get the kanjiVG data
					String kvg = "";
					byte[] paths = c.getBlob(c.getColumnIndex(KanjiDBHelper.KEY_PATH));
					if(null != paths){
						kvg = ZipTools.decompress(paths);
					}
					// Get the stroke count
					int strokeCount = c.getInt(c.getColumnIndex(KanjiDBHelper.KEY_STROKE_COUNT));
					// Get the frequency
					int frequency = c.getInt(c.getColumnIndex(KanjiDBHelper.KEY_FREQUENCY));				
					// Get the grade
					int grade = c.getInt(c.getColumnIndex(KanjiDBHelper.KEY_GRADE));
					
					Cursor cOn = mKDBHelper.getKanjiOnYomi(cp);
					cOn.moveToFirst();
					for(int i=0; i<cOn.getCount(); i++){
						String yomi = cOn.getString(cOn.getColumnIndex(KanjiDBHelper.KEY_YOMI));
						currentKanji.oNYomi.add(yomi);
						cOn.moveToNext();
					}
					
					Cursor cKun = mKDBHelper.getKanjiKunYomi(cp);
					cKun.moveToFirst();
					for(int i=0; i<cKun.getCount(); i++){
						String yomi = cKun.getString(cKun.getColumnIndex(KanjiDBHelper.KEY_YOMI));
						currentKanji.kUNYomi.add(yomi);
						cKun.moveToNext();
					}

					Cursor cMeaning = mKDBHelper.getKanjiMeaning(cp);
					cMeaning.moveToFirst();
					for(int i=0; i<cMeaning.getCount(); i++){
						String meaning = cMeaning.getString(cMeaning.getColumnIndex(KanjiDBHelper.KEY_MEANING));
						currentKanji.meaning.add(meaning);
						cMeaning.moveToNext();
					}
					
					currentKanji.frequency = frequency;
					currentKanji.grade = grade;
					currentKanji.kvg = kvg;
					currentKanji.strokeCount = strokeCount;
					currentKanji.jlpt = jlpt;
					KanjiManager.setCurrentKanji(currentKanji);
					
					mKDBHelper.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (java.sql.SQLException e) {
					e.printStackTrace();
				}
			}*/
		}
    };
}
