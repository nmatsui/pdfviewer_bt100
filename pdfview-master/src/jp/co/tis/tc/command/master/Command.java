package jp.co.tis.tc.command.master;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class Command {
	private static final String COMMAND_MASTER_TAG = "jp.co.tis.tc.command.master";
	private static final Pattern COMMAND_PATTERN = Pattern.compile("^.*(ägëÂ|èkè¨|ëSëÃ|è„|â∫|âE|ç∂|[0-9]+).*$");
	private static float VX = 1500.0f;
	private static float VY = 1000.0f;
	
	public static Command parceVoice(List<String> candidates) {
		Command command = new Command();
		for (String voice : candidates) {
			Log.d(COMMAND_MASTER_TAG, String.format("raw voice => %s", voice));
			Matcher m = COMMAND_PATTERN.matcher(voice.trim());
			if (m.matches()) {
				Log.d(COMMAND_MASTER_TAG, m.toString());
				String s = m.group(1);
				Log.d(COMMAND_MASTER_TAG, s);
				if ("ägëÂ".equals(s)) {
					command.setMessage("ZOOM_IN\n");
					break;
				}
				else if ("èkè¨".equals(s)) {
					command.setMessage("ZOOM_OUT\n");
					break;
				}
				else if ("ëSëÃ".equals(s)) {
					command.setMessage("ZOOM_FIT\n");
					break;
				}
				else if ("è„".equals(s)) {
					command.setMessage(String.format("FLING,vx=%f,vy=%f\n", 0.0f, -VY));
					break;
				}
				else if ("â∫".equals(s)) {
					command.setMessage(String.format("FLING,vx=%f,vy=%f\n", 0.0f, VY));
					break;
				}
				else if ("âE".equals(s)) {
					command.setMessage(String.format("FLING,vx=%f,vy=%f\n", VX, 0.0f));
					break;
				}
				else if ("ç∂".equals(s)) {
					command.setMessage(String.format("FLING,vx=%f,vy=%f\n", -VX, 0.0f));
					break;
				}
				else {
					command.setMessage(String.format("GOTO_PAGE,page=%d\n\n", Integer.parseInt(s)));
					break;
				}
			}
		}
		return command;
	}

	private String message;
	private Command() {
		this.message = "";
	}
	public String getMessage() {
		return message;
	}
	private void setMessage(String message) {
		this.message = message;
	}
}
