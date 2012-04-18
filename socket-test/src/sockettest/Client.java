package sockettest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String sendMessage = "";
		
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(args[0], Integer.parseInt(args[1])));
		System.out.format("connect to %s:%d%n", socket.getInetAddress().getHostAddress(), socket.getPort());
		
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		sendMessage = "ZOOM_IN\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();
		
		sendMessage = "ZOOM_IN\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();

		sendMessage = "ZOOM_FIT\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();
		
		sendMessage = "ZOOM_OUT\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();
		
		sendMessage = "GOTO_PAGE,page=10\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();
		
		sendMessage = "FLING,vx=-1000.523,vy=-1600.352\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();
		
		sendMessage = "hoge\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();
		
		sendMessage = "GOTO_PAGE,page=200\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();
		
		sendMessage = "GOTO_PAGE,page=abc\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();
		
		sendMessage = "GOTO_PAGE\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();

		sendMessage = "FLING,vx=a000.523,vy=-1600.352\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();
		
		sendMessage = "FLING,vx=1000.523\n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();
		
		sendMessage = "FLING                                                                                                                                                                                               \n";
		out.write(sendMessage);
		out.flush();
		System.out.format("sended %s%nPlease press key...", sendMessage);
		in.readLine();
		
		socket.close();
		System.out.format("connection end%n");
	}
}
