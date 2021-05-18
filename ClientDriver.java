import java.io.*;
public class ClientDriver{
	public static void main(String [] args) throws Exception{
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in), 1);
		Player player = new Player(); //プレイヤオブジェクトの用意
		player.setName("test user"); //名前を受付
		Othello game = new Othello(); //オセロオブジェクトを用意
		Client oclient = new Client(game, player); //引数としてオセロオブジェクトとプレイヤオブジェクトを渡す
		oclient.setVisible(true);
		System.out.println("テスト用サーバに接続します");
		oclient.connectServer("localhost", 10000);
		System.out.println("受信用テストメッセージを入力してください");
		while(true){
			String s = r.readLine();
			oclient.receiveMessage(s);
			System.out.println("テストメッセージ「" + s + "」を受信しました");
			System.out.println("テスト操作を行った後、受信用テストメッセージを入力してください");
		}
	}
}