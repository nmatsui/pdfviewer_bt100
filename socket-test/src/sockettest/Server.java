package sockettest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) throws IOException  {
		ServerSocket serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(new InetSocketAddress(Integer.parseInt(args[0])));
		System.out.format("server start at %s:%d%n", InetAddress.getLocalHost().getHostAddress(), serverSocket.getLocalPort());
		while(true) {
			final Socket socket = serverSocket.accept();
			new Thread(new Runnable(){
				@Override
				public void run() {
					System.out.format("worker start%n");
					BufferedReader in = null;
					try {
						in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String input;
						while ((input = in.readLine()) != null) {
							System.out.format("Received:: %s%n", input);
						}
					} catch (IOException e) {
						System.err.format("Receive fail:: %s%n", e.getMessage());
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
							System.err.format("Close fail:: %s%n", e.getMessage());
							throw new RuntimeException(e);
						}
					}
					System.out.format("worker end%n");
				}
			}).start();
		}
	}
}
