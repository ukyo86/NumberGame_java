import java.io.*;
import java.net.*;

//通信機能を含むクラス
public class SocketConnector {
	private static final int PORT = 50000;
	private InetAddress addr;
	private String host;
	private ServerSocket s;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	
	public void setHost(String input){ //ホスト名をセットするアクセスメソッド
		host = input;
	}
	public void startServer(){ //サーバーを起動させる
		try{
			s = new ServerSocket(PORT);
			System.out.println("Started:"+s);
			System.out.println("相手からの接続を受け付けています。\n");
			socket = s.accept();
			System.out.println("Connection accepted:"+socket);
			System.out.println("接続を受け付けました。\n");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
			System.out.println("通信を開始します。\n");
		}catch(IOException e){
			System.out.println("Server : IOError");
			e.printStackTrace();
		}
	}
	
	public void terminateServer(){ //サーバーを終了させる
		System.out.println("通信を終了します。");
		try{
			socket.close();
			s.close();
		}catch(IOException e){
			System.out.println("Server : IOError");
			e.printStackTrace();
		}
	}
	
	public boolean startConnection(){ //クライアントでの接続を開始させる
		try{
			addr = InetAddress.getByName(host);
			try{
				System.out.println("addr : "+addr);
				socket = new Socket(addr, PORT);
				System.out.println("socket : "+socket);
				System.out.println("接続に成功しました。\n");
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
				System.out.println("通信を開始します。");
				return true;
			}catch(IOException e){
				System.out.println("入力したホスト名は存在しません。\n");
				//e.printStackTrace();
				return false;
			}	
		}catch(UnknownHostException e){
			System.out.println("入力したホスト名は存在しません。\n");
			//e.printStackTrace();
			return false;
		}
	}
	
	public void terminateConnection(){ //クライアントでの接続を終了させる
		try{
			System.out.println("通信を終了します。");
			socket.close();
		}catch(IOException e){
			System.out.println("Client : IOError");
			//e.printStackTrace();
		}
	}
	
	public void messageSender(String input){ //通信での送信を行う
		out.println(input);
	}
	public String messageReciever() throws IOException{ //通信での受信を行う 
		String message=null;
		try{
			message = in.readLine();
		}catch(IOException e){
			System.out.println("通信が切断されました。");
		}
		return  message;
	}
}