package shiba.test.androidkanji;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KanjiListAdapter extends ArrayAdapter<KanjiInfo>{
	private ArrayList<KanjiInfo> items;
	private LayoutInflater mInflater;
	private KanjiDBHelper mDbHelper;
	private Context mCtx;
	private final int QT_HEADER_SIZE = 4;
	private final int ZLIB_HEADER_SIZE = 2;
	private final int ZLIB_FOOTER_SIZE = 4;
	
	private OnClickListener onCharacterClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(null != v){
				// TODO : Get the kanji infos and update the other fragments
				try {
					mDbHelper.openDatabase();
					Cursor c = mDbHelper.getKanjiInfos(TextTools.kanjiToCode(((TextView)v).getText().toString()));
					
					int jlpt = c.getInt(c.getColumnIndex(KanjiDBHelper.KEY_JLPT));
					byte[] paths = c.getBlob(c.getColumnIndex(KanjiDBHelper.KEY_PATH));
					// 	The datas read from the DB have been compressed with QByteArray::qCompress, from the Qt API.
					//	This method appended 4 extra bytes at the beginning of the compressed data and thus make us
					//	impossible to read back the compressed data. We have to remove this Qt header as well as
					//	the ZLib header and footer in order to be able to read the GZip compressed data
					//	The packet structure is:
					//	| Qt | ZLib | Compressed data | ZLib |
					// 	source: http://www.qtforum.org/article/27065/how-to-use-zlib-data-created-by-qcompress-in-gzip-file.html
					
					// NOTE : This part doesn't work! Datas compressed with qCompress can only be retrieved by qDecompress!
					
					/*if(paths.length > (QT_HEADER_SIZE)){
						System.out.println("Removing unnecessary headers/footer from compressed data(" +paths.length +") .....");
						ByteArrayInputStream bais = new ByteArrayInputStream(paths);
						byte[] pathsGZip = new byte[paths.length];
						int remainingLength = bais.read(pathsGZip, QT_HEADER_SIZE, paths.length - QT_HEADER_SIZE);
						System.out.println("Packet ready for decompression (" +remainingLength +")");
						bais.close();
						
						// TODO : read the compressed datas
						// Decompress the bytes
						 Inflater decompresser = new Inflater(true);
						 decompresser.setInput(pathsGZip, 0, remainingLength);
						 byte[] result = new byte[remainingLength];
						 int resultLength = decompresser.inflate(result);
						 decompresser.end();

						 // Decode the bytes into a String
						 String outputString = new String(result, 0, resultLength, "UTF-8");
						 System.out.println(outputString);
					}
					*/
					
					mDbHelper.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
    };
    
    private OnClickListener onFavoriteClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(null != v){
				int position = Integer.parseInt(v.getTag().toString());
				KanjiInfo ki = items.get(position);
				ki.toggleFavorite();
				Drawable d;
				if(ki.favorite()){
					d = mCtx.getResources().getDrawable(R.drawable.favfull);
				}else{
					d = mCtx.getResources().getDrawable(R.drawable.favempty);
				}
				
				((ImageView)v).setImageDrawable(d);
				
				try {
					mDbHelper.openDatabase();
					mDbHelper.toggleFavorite(TextTools.kanjiToCode(ki.kanji()));
					mDbHelper.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
    };
	
	public KanjiListAdapter(Context context, int textViewResourceId, ArrayList<KanjiInfo> items) {
		super(context, textViewResourceId, items);
		mCtx = context;
		this.items = items;
		mInflater = LayoutInflater.from(context);
		mDbHelper = new KanjiDBHelper(context);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout row;
		if(null == convertView){
			row = (LinearLayout)mInflater.inflate(R.layout.kanji_row, parent, false);
		}else{
			row = (LinearLayout)convertView;
		}
			
		KanjiInfo ki = items.get(position);
		
		TextView character = (TextView)row.findViewById(R.id.text1);
		ImageView star = (ImageView)row.findViewById(R.id.favoriteStar);
		character.setOnClickListener(onCharacterClicked);
		star.setOnClickListener(onFavoriteClicked);
		
		character.setText(ki.kanji());
		
		Drawable d;
		if(ki.favorite()){
			d = mCtx.getResources().getDrawable(R.drawable.favfull);
		}else{
			d = mCtx.getResources().getDrawable(R.drawable.favempty);
		}
		star.setImageDrawable(d);
		star.setTag(position);
		
		return row;
    }
}
