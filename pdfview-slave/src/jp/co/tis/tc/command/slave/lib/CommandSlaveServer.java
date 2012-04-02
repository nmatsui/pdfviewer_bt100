package jp.co.tis.tc.command.slave.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import android.util.Log;

public class CommandSlaveServer extends Observable {
	private static final String COMMAND_SLAVE_TAG = "jp.co.tis.tc.command.slave";
	private static final int MAX_THREAD = 1;
	
	private ThreadGroup workers;
	private ServerSocket serverSocket;
	
	public CommandSlaveServer(int port) {
		workers = new ThreadGroup("workers");
		try {
			serverSocket = new ServerSocket();
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(port));
			Log.d(COMMAND_SLAVE_TAG, String.format("CommandSlaveServer#init ServerSocket created port = %d", port));
		} catch (IOException e) {
			Log.e(COMMAND_SLAVE_TAG, String.format("CommandSlaveServer#init ServerSoket create failed : %S", e.getMessage()));
			throw new RuntimeException(e);
		}
	}
	public void listen() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d(COMMAND_SLAVE_TAG, "CommandSlaveServer#listen ServerSocket listen start");
				try {
					while(true) {
						Socket socket = serverSocket.accept();
						if (workers.activeCount() <= MAX_THREAD) {
							Log.d(COMMAND_SLAVE_TAG, String.format("ServerSocket accepted"));
							new Thread(workers, new CommandWorker(socket)).start();
						}
						else {
							Log.i(COMMAND_SLAVE_TAG, String.format("CommandSlaveServer#listen thread limit over (max = %d)", MAX_THREAD));
							socket.close();
						}
					}
				}
				catch (SocketException e) {
					Log.d(COMMAND_SLAVE_TAG, String.format("CommandSlaveServer#listen accept canceled : %S", e.getMessage()));
				}
				catch (IOException e) {
					Log.e(COMMAND_SLAVE_TAG, String.format("CommandSlaveServer#listen ServerSoket accept failed : %S", e.getMessage()));
					throw new RuntimeException(e);
				}
				Log.d(COMMAND_SLAVE_TAG, "CommandSlaveServer#listen ServerSocket listen end");
			}
		}).start();
	}
	public void halt() {
		try {
			serverSocket.close();
			Log.d(COMMAND_SLAVE_TAG, String.format("CommandSlaveServer#halt ServerSocket closed"));
		} catch (IOException e) {
			Log.e(COMMAND_SLAVE_TAG, String.format("CommandSlaveServer#halt ServerSoket close failed : %S", e.getMessage()));
			throw new RuntimeException(e);
		}
	}
	
	private class CommandWorker implements Runnable {
		private Socket socket;
		public CommandWorker(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String input;
				while ((input = in.readLine()) != null) {
					Log.i(COMMAND_SLAVE_TAG, String.format("CommandSlaveServer#CommandWorker Received from Client : %s", input));
					notifyObservers(new Command(input));
				}
			} catch (IOException e) {
				Log.e(COMMAND_SLAVE_TAG, String.format("CommandSlaveServer#CommandWorker Receive failed : %S", e.getMessage()));
				throw new RuntimeException(e);
			} finally {
				try {
					if (in != null) {
						in.close();
					}
					if (socket != null) {
						socket.close();
					}
				}
				catch (IOException e) {
					Log.e(COMMAND_SLAVE_TAG, String.format("CommandSlaveServer#CommandWorker Close failed : %S", e.getMessage()));
					throw new RuntimeException(e);
				}
			}
		}	
	}
}
