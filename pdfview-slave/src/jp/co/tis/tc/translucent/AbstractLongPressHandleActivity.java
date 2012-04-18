package jp.co.tis.tc.translucent;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;

public abstract class AbstractLongPressHandleActivity extends Activity {
	private static final String TRANSLUCENT_TAG = "jp.co.tis.tc.translucent";
	private static final long THRETHOLD = 1000;
	private boolean isNewPressed = false;
	
	@Override
	protected void onResume() {
		super.onResume();
		isNewPressed = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 23) {
			Log.d(TRANSLUCENT_TAG, String.format("onKeyDown eventTime - downTime = %d new?:%s", event.getEventTime() - event.getDownTime(), isNewPressed));
			if (event.getEventTime() == event.getDownTime()) {
				isNewPressed = true;
			}
			if (isNewPressed && event.getEventTime() - event.getDownTime() > THRETHOLD) {
				Log.d(TRANSLUCENT_TAG, String.format("longpressed %d", event.getEventTime() - event.getDownTime()));
				longPressed(event);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected abstract void longPressed(KeyEvent event);
}
