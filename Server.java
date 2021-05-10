import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

public class Server{
	private int port; // サーバの待ち受けポート
	private boolean [] online; //オンライン状態管理用配列
	private PrintWriter [] out; //データ送信用オブジェクト
	private Receiver [] receiver; //データ受信用オブジェクト

	//コンストラクタ
	public Server(int port) { //待ち受けポートを引数とする
		this.port = port; //待ち受けポートを渡す
		out = new PrintWriter [2]; //データ送信用オブジェクトを2クライアント分用意
		receiver = new Receiver [2]; //データ受信用オブジェクトを2クライアント分用意
		online = new boolean[2]; //オンライン状態管理用配列を用意
	}

	// データ受信用スレッド(内部クラス)
	class Receiver extends Thread {
		private InputStreamReader sisr; //受信データ用文字ストリーム
		private BufferedReader br; //文字ストリーム用のバッファ
		private int playerNo; //プレイヤを識別するための番号

		// 内部クラスReceiverのコンストラクタ
		Receiver (Socket socket, int playerNo){
			try{
				this.playerNo = playerNo; //プレイヤ番号を渡す
				sisr = new InputStreamReader(socket.getInputStream());
				br = new BufferedReader(sisr);
				sendColor(playerNo);
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
		// 内部クラス Receiverのメソッド
		public void run(){
			try{
				while(true) {// データを受信し続ける
					String inputLine = br.readLine();//データを一行分読み込む
					if (inputLine != null){ //データを受信したら
						forwardMessage(inputLine, playerNo); //もう一方に転送する
					}
				}
			} catch (IOException e){ // 接続が切れたとき
				System.err.println("プレイヤ " + playerNo + "との接続が切れました．");
				online[playerNo] = false; //プレイヤの接続状態を更新する
				printStatus(); //接続状態を出力する
			}
		}
	}

	// メソッド
	public void acceptClient(){ //クライアントの接続(サーバの起動)
		try {
			System.out.println("サーバが起動しました．");
			ServerSocket ss = new ServerSocket(port); //サーバソケットを用意
			int i=0;
			while (i<2) {
				Socket socket = ss.accept(); //新規接続を受け付ける
				System.out.println("クライアントと接続しました．"); //テスト用出力
					out[i] = new PrintWriter(socket.getOutputStream(), true);//データ送信オブジェクトを用意
					receiver[i] = new Receiver(socket,i);//データ受信オブジェクト(スレッド)を用意
					online[i] = true;
					receiver[i].start();//データ送信オブジェクト(スレッド)を起動
					i++;
			}
			forwardMessage("startNegotiation",0);
			forwardMessage("startNegotiation",1);
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}

	public void printStatus(){ //クライアント接続状態の確認
		if(online[0]) {
			System.out.println("プレイヤ0と接続しています");
		}else {
			System.out.println("プレイヤ0と接続していません");
		}
		if(online[1]) {
			System.out.println("プレイヤ1と接続しています");
		}else {
			System.out.println("プレイヤ1と接続していません");
		}
	}

	public void sendColor(int playerNo){ //先手後手情報(白黒)の送信
		if(playerNo == 0) {
			out[0].println("black");
            out[0].flush();
		}else if(playerNo == 1) {
			out[1].println("white");
            out[1].flush();
		}
	}

	public void forwardMessage(String msg, int playerNo){ //操作情報の転送
        if(playerNo == 0) {
            out[1].println(msg);
            out[1].flush();
        }else if(playerNo == 1) {
            out[0].println(msg);
            out[0].flush();
        }
	}

	public static void main(String[] args){ //main
		Server server = new Server(10000); //待ち受けポート10000番でサーバオブジェクトを準備
		server.acceptClient(); //クライアント受け入れを開始
	}
}