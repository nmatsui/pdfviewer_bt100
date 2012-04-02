package jp.co.tis.tc.command.slave.lib;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public abstract class AbstractCommandSlaveActivity extends Activity implements Observer {
	private static final String COMMAND_SLAVE_TAG = "jp.co.tis.tc.command.slave";
	private static final int PORT = 10009;
	
	private CommandSlaveServer commandSlaveServer;
	private Handler handler;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	Log.d(COMMAND_SLAVE_TAG, "AbstractCommandSlaveActivity#onCreate");
        handler = new Handler();
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(COMMAND_SLAVE_TAG, "AbstractCommandSlaveActivity#onResume");
		commandSlaveServer = new CommandSlaveServer(PORT);
		commandSlaveServer.addObserver(this);
		commandSlaveServer.listen();
	}    
    
	@Override
	protected void onPause() {
		Log.d(COMMAND_SLAVE_TAG, "AbstractCommandSlaveActivity#onPause");
		commandSlaveServer.halt();
		commandSlaveServer.removeObserver(this);
		commandSlaveServer = null;
		super.onPause();
	}
	
	@Override
	public void notifyCommand(final Command command) {
		Log.d(COMMAND_SLAVE_TAG, String.format("AbstractCommandSlaveActivity#notifyCommand : message = %s", command.getAction()));
		handler.post(new Runnable() {
			@Override
			public void run() {
				onReceiveCommand(command);
			}
		});
	}
	
	protected abstract void onReceiveCommand(Command command);
}
