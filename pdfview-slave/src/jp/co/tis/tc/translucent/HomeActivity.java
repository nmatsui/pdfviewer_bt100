package jp.co.tis.tc.translucent;

import java.io.File;
import java.io.IOException;

import android.app.WallpaperManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import cx.hell.android.pdfview.OpenFileActivity;

public class HomeActivity extends AbstractLongPressHandleActivity {
	private static final int MENU_APP = (Menu.FIRST + 1);
	private static final int MENU_SETTINGS = (Menu.FIRST + 2);
	private static final String PDF_URI = "/mnt/sdcard/PDF/U_GUIDE_BT100.pdf";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        WallpaperManager wm = WallpaperManager.getInstance(this);
        try {
			wm.setResource(R.drawable.translucent);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_APP, Menu.NONE, R.string.menu_app).setIcon(android.R.drawable.ic_menu_view);
		menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, R.string.menu_settings).setIcon(android.R.drawable.ic_menu_preferences);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = true;
		switch(item.getItemId()) {
		case MENU_APP:
			Intent intent = new Intent();
			intent.setDataAndType(Uri.fromFile(new File(PDF_URI)), "application/pdf");
			intent.setClass(this, OpenFileActivity.class);
			intent.setAction("android.intent.action.VIEW");
			startActivity(intent);
			break;
		case MENU_SETTINGS:
			Intent settings = new Intent()
				.setAction(android.provider.Settings.ACTION_SETTINGS)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			startActivity(settings);
			break;
		default:
			result = super.onOptionsItemSelected(item);
			break;
		}
		return result;
	}

	@Override
	protected void longPressed(KeyEvent event) {
		Intent intent = new Intent();
		intent.setDataAndType(Uri.fromFile(new File(PDF_URI)), "application/pdf");
		intent.setClass(this, OpenFileActivity.class);
		intent.setAction("android.intent.action.VIEW");
		startActivity(intent);
	}
}
