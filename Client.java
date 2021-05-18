//パッケージのインポート
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Client extends JFrame implements MouseListener,ActionListener {
	private JButton buttonArray[][];//オセロ盤用のボタン配列
	private JLabel colorLabel; // 色表示用ラベル
	private JLabel turnLabel; // 手番表示用ラベル
    private JLabel myTimerLabel;
    private JLabel opTimerLabel;
    private Timer myTimer;
    private Timer opTimer;
    private int mySec;
    private int opSec;
    private CardLayout layout;
    private JPanel J;
	private JPanel j1; // コンテナ
    private JPanel j2; // 
	private ImageIcon blackIcon, whiteIcon, boardIcon; //アイコン
	private PrintWriter out;//データ送信用オブジェクト
	private Receiver receiver; //データ受信用オブジェクト
	private Othello game; //Othelloオブジェクト
	private Player player; //Playerオブジェクト
    private JButton two, five,ten; //停止、スキップ用ボタン
    private JButton yes,no;
	private JLabel timeLimit; // 
	private JLabel opTime; // 手番表示用ラベル
    private JLabel request;
    private int myReqCount = 3,opReqCount = 3;
    private boolean isMyReady = false,isOpReady = false;
    private int myLastSelect;
    private int opLastSelect;

	// コンストラクタ
	public Client(Othello game, Player player) { //OthelloオブジェクトとPlayerオブジェクトを引数とする
		this.game = game; //引数のOthelloオブジェクトを渡す
		this.player = player; //引数のPlayerオブジェクトを渡す
		String [][] grids = game.getGrids(); //getGridメソッドにより局面情報を取得
		//ウィンドウ設定
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じる場合の処理
		setTitle("ネットワーク対戦型オセロゲーム");//ウィンドウのタイトル
		setBounds(0,0,8 * 45 + 10,8 * 45 + 200);//ウィンドウのサイズを設定
        j1 = new JPanel();
        j1.setLayout(null);
        timeLimit = new JLabel("制限時間を選択してください");
		timeLimit.setBounds(0,0,8 * 45 + 10,30);//境界を設定
		j1.add(timeLimit);//色表示用ラベルをペインに貼り付け
        two = new JButton("2分");
        two.setBounds(0,40,(8 * 45 + 10)/3,30);
        j1.add(two);
        two.addMouseListener(this);
        two.setActionCommand("two");
        five = new JButton("5分");
        five.setBounds((8 * 45 + 10)/3,40,(8 * 45 + 10)/3,30);
        j1.add(five);
        five.addMouseListener(this);
        five.setActionCommand("five");
        ten = new JButton("10分");
        ten.setBounds((8 * 45 + 10)/3*2,40,(8 * 45 + 10)/3,30);
        j1.add(ten);
        ten.addMouseListener(this);
        ten.setActionCommand("ten");
        opTime = new JLabel("相手は選択していません");
        opTime.setBounds(0,80,8 * 45 + 10,30);
        j1.add(opTime);
        yes = new JButton("はい");
        yes.setBounds(0,120,(8 * 45 + 10)/2,30);
        j1.add(yes);
        yes.addMouseListener(this);
        yes.setActionCommand("yes");
        yes.setEnabled(false);
        no = new JButton("いいえ");
        no.setBounds((8 * 45 + 10)/2,120,(8 * 45 + 10)/2,30);
        j1.add(no);
        no.addMouseListener(this);
        no.setActionCommand("no");
        no.setEnabled(false);
        request = new JLabel("リクエスト残り回数" + myReqCount + "回");
        request.setBounds(0,160,8 * 45 + 10,30);
        j1.add(request);
        j2 = new JPanel();
		//アイコン設定(画像ファイルをアイコンとして使う)
		whiteIcon = new ImageIcon("White.jpg");
		blackIcon = new ImageIcon("Black.jpg");
		boardIcon = new ImageIcon("GreenFrame.jpg");
		j2.setLayout(null);//
		//オセロ盤の生成
		buttonArray = new JButton[8][8];//ボタンの配列を作成
		for(int i=0 ;i<8;i++){
            for(int j=0;j<8;j++) {
                if(grids[i][j].equals("black")){ buttonArray[i][j] = new JButton(blackIcon);}//盤面状態に応じたアイコンを設定
			    if(grids[i][j].equals("white")){ buttonArray[i][j] = new JButton(whiteIcon);}//盤面状態に応じたアイコンを設定
			    if(grids[i][j].equals("board")){ buttonArray[i][j] = new JButton(boardIcon);}//盤面状態に応じたアイコンを設定
            
			    j2.add(buttonArray[i][j]);//ボタンの配列をペインに貼り付け
			    // ボタンを配置する
			    int x = (i % 8) * 45;
			    int y = (int) (j % 8) * 45;
			    buttonArray[i][j].setBounds(x, y, 45, 45);//ボタンの大きさと位置を設定する．
			    buttonArray[i][j].addMouseListener(this);//マウス操作を認識できるようにする
			    buttonArray[i][j].setActionCommand(Integer.toString(i*8+j));//ボタンを識別するための名前(番号)を付加する
            }
		}
		myTimerLabel = new JLabel("");
        myTimerLabel.setBounds(0, 8*45+30,8 * 45 + 10,30);
		j2.add(myTimerLabel);
        myTimer = new Timer(1000,myTimerListener);
        opTimerLabel = new JLabel("");
        opTimerLabel.setBounds(0, 8*45+60,8 * 45 + 10,30);
        j2.add(opTimerLabel);
        opTimer = new Timer(1000,opTimerListener);
		//色表示用ラベル
		String myName = player.getName();
		colorLabel = new JLabel(myName + "さんの色は未定です");//色情報を表示するためのラベルを作成
		colorLabel.setBounds(0, 8 * 45 + 90 , 8 * 45 + 10, 30);//境界を設定
		j2.add(colorLabel);//色表示用ラベルをペインに貼り付け
		//手番表示用ラベル
		turnLabel = new JLabel("手番は未定です");//手番情報を表示するためのラベルを作成
		turnLabel.setBounds(0, 8 * 45 + 120, 8 * 45 + 10, 30);//境界を設定
		j2.add(turnLabel);//手番情報ラベルをペインに貼り付け
        J = new JPanel();
        layout = new CardLayout();
        J.setLayout(layout);
        J.add(j1);
        J.add(j2);
        getContentPane().add(J);
	}

	// メソッド
	public void connectServer(String ipAddress, int port){	// サーバに接続
        Socket socket = null;
		try {
			socket = new Socket(ipAddress, port); //サーバ(ipAddress, port)に接続
			out = new PrintWriter(socket.getOutputStream(), true); //データ送信用オブジェクトの用意
			receiver = new Receiver(socket); //受信用オブジェクトの準備
			receiver.start();//受信用オブジェクト(スレッド)起動
		} catch (UnknownHostException e) {
			System.err.println("ホストのIPアドレスが判定できません: " + e);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("サーバ接続時にエラーが発生しました: " + e);
			System.exit(-1);
		}
	}

    public ActionListener myTimerListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            mySec--;
            myTimerLabel.setText("あなたの残り時間は" + mySec + "秒です");
            if(mySec == 0) {
            myTimer.stop();
            JOptionPane.showMessageDialog(J,"持ち時間が0になりました。あなたは負けました","result",JOptionPane.PLAIN_MESSAGE);
            sendMessage("timeup");
        }
        }
    };

    public ActionListener opTimerListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            opSec--;
            opTimerLabel.setText("相手の残り時間は" + opSec + "秒です");
        }
    };

	public void sendMessage(String msg){	// サーバに操作情報を送信
		out.println(msg);//送信データをバッファに書き出す
		out.flush();//送信データを送る
		System.out.println("サーバにメッセージ " + msg + " を送信しました"); //テスト標準出力
	}

    public void startGame() {
        if(isMyReady && isOpReady) {
            layout.last(J);
            if(player.getColor().equals("black")) {
                mySec = myLastSelect;
                opSec = opLastSelect;
                opTimerLabel.setText("相手の残り時間は" + opSec + "秒です");
                myTimer.start();
            }else if(player.getColor().equals("white")) {
                mySec = myLastSelect;
                opSec = opLastSelect;
                myTimerLabel.setText("あなたの残り時間は" + mySec + "秒です");
                opTimer.start();
            } 
        }
    }

	// データ受信用スレッド(内部クラス)
	class Receiver extends Thread {
		private InputStreamReader sisr; //受信データ用文字ストリーム
		private BufferedReader br; //文字ストリーム用のバッファ

		// 内部クラスReceiverのコンストラクタ
		Receiver (Socket socket){
			try{
				sisr = new InputStreamReader(socket.getInputStream()); //受信したバイトデータを文字ストリームに
				br = new BufferedReader(sisr);//文字ストリームをバッファリングする
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
		// 内部クラス Receiverのメソッド
		public void run(){
			try{
				while(true) {//データを受信し続ける
					String inputLine = br.readLine();//受信データを一行分読み込む
					if (inputLine != null){//データを受信したら
						receiveMessage(inputLine);//データ受信用メソッドを呼び出す
					}
				}
			} catch (IOException e){
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
	}

	public void receiveMessage(String msg){	// メッセージの受信
		System.out.println("サーバからメッセージ " + msg + " を受信しました"); //テスト用標準出力
        if(msg.equals("startNegotiation")) {
            setVisible(true);
        }else if(msg.equals("black")) {
            player.setColor("black");
            colorLabel.setText(player.getName() + "さんの色は黒です");
            turnLabel.setText("あなたの番です");
        }else if(msg.equals("white")) {
            player.setColor("white");
            colorLabel.setText(player.getName() + "さんの色は白です");
            turnLabel.setText("相手の番です");
        }else if(msg.equals("two")) {
            opTime.setText("相手が2分を選択しました。承認しますか?");
            yes.setEnabled(true);
            no.setEnabled(true);
            opLastSelect = 120;
        }else if(msg.equals("five")) {
            opTime.setText("相手が5分を選択しました。承認しますか?");
            yes.setEnabled(true);
            no.setEnabled(true);
            opLastSelect = 300;
        }else if(msg.equals("ten")) {
            opTime.setText("相手が10分を選択しました。承認しますか?");
            yes.setEnabled(true);
            no.setEnabled(true);
            opLastSelect = 600;
        }else if(msg.equals("yes")) {
            isMyReady = true;
            two.setEnabled(false);
            five.setEnabled(false);
            ten.setEnabled(false);
            startGame();
        }else if(msg.equals("no")) {
            if(myReqCount == 0) {
                isMyReady = true;
                startGame();
            }else if(myReqCount > 0) {
                two.setEnabled(true);
                five.setEnabled(true);
                ten.setEnabled(true);
            }
        }else if(msg.equals("pass")) {
            opTimer.stop();
            myTimer.start();
            JOptionPane.showMessageDialog(this,"相手がパスしました","info",JOptionPane.INFORMATION_MESSAGE);
            game.changeTurn();
            turnLabel.setText("あなたの番です");
        }else if(msg.equals("timeup")) {
            JOptionPane.showMessageDialog(this,"相手の持ち時間が0なりました。あなたは勝ちました","result",JOptionPane.PLAIN_MESSAGE);
        }else {
            opTimer.stop();
            int x = Integer.parseInt(msg)%8;
            int y = Integer.parseInt(msg)/8;
            game.putStone(x,y,game.getTurn(),true);
            updateDisp();
            String state = game.gameState(player.getColor());
            if(state.equals("pass")) {
                sendMessage("pass");
                opTimer.start();
                JOptionPane.showMessageDialog(this,"置ける場所がないのでパスしました","info",JOptionPane.INFORMATION_MESSAGE);
            }else if(state.equals("end")) {
                String winner = game.checkWinner();
                if(winner.equals(player.getColor())) {
                    JOptionPane.showMessageDialog(this,"対局が終了しました。あなたは勝ちました","result",JOptionPane.PLAIN_MESSAGE);
                }else if(winner.equals("even")) {
                    JOptionPane.showMessageDialog(this,"対局が終了しました。引き分けです","result",JOptionPane.PLAIN_MESSAGE);
                }else {
                    JOptionPane.showMessageDialog(this,"対局が終了しました。あなたは負けました","result",JOptionPane.PLAIN_MESSAGE);
                }
            }else {
                myTimer.start();
                game.changeTurn();
                turnLabel.setText("あなたの番です");
            }
        }
    }

	public void updateDisp(){	// 画面を更新する
        for(int i=0 ;i<8;i++){
            for(int j=0;j<8;j++) {
                if(game.getGrids()[i][j].equals("black")){ buttonArray[i][j].setIcon(blackIcon);;}
			    if(game.getGrids()[i][j].equals("white")){ buttonArray[i][j].setIcon(whiteIcon);}
			    if(game.getGrids()[i][j].equals("board")){ buttonArray[i][j].setIcon(boardIcon);}
            }
        }
	}

	public void acceptOperation(String command){	// プレイヤの操作を受付
        if(command.equals("two")) {
            sendMessage(command);
            myLastSelect = 120;
            timeLimit.setText("あなたは2分を選択しました");
            two.setEnabled(false);
            five.setEnabled(false);
            ten.setEnabled(false);
            request.setText("リクエスト残り回数" + --myReqCount + "回");
        }else if(command.equals("five")) {
            sendMessage(command);
            timeLimit.setText("あなたは5分を選択しました");
            myLastSelect = 300;
            two.setEnabled(false);
            five.setEnabled(false);
            ten.setEnabled(false);
            request.setText("リクエスト残り回数" + --myReqCount + "回");
        }else if(command.equals("ten")) {
            sendMessage(command);
            myLastSelect = 600;
            timeLimit.setText("あなたは10分を選択しました");
            two.setEnabled(false);
            five.setEnabled(false);
            ten.setEnabled(false);
            request.setText("リクエスト残り回数" + --myReqCount + "回");
        }else if(command.equals("yes")) {
            sendMessage(command);
            isOpReady = true;
            yes.setEnabled(false);
            no.setEnabled(false);
            startGame();
        }else if(command.equals("no")) {
            sendMessage(command);
            opReqCount--;
            if(opReqCount == 0) {
                isOpReady = true;
                yes.setEnabled(false);
                no.setEnabled(false);
                startGame();
            }else if(opReqCount > 0) {
                yes.setEnabled(false);
                no.setEnabled(false);
            }
        }else if(game.getTurn().equals(player.getColor())){
            int x = Integer.parseInt(command)%8;
            int y = Integer.parseInt(command)/8;
            if(game.putStone(x,y,player.getColor(),true)) {
                myTimer.stop();
                sendMessage(command);
                opTimer.start();
                updateDisp();
                game.changeTurn();
                turnLabel.setText("相手の番です");
                if(game.gameState(player.getColor()).equals("end")) {
                    opTimer.stop();
                    String winner = game.checkWinner();
                    if(winner.equals(player.getColor())) {
                        JOptionPane.showMessageDialog(this,"対局が終了しました。あなたは勝ちました","result",JOptionPane.PLAIN_MESSAGE);
                    }else if(winner.equals("even")) {
                        JOptionPane.showMessageDialog(this,"対局が終了しました。引き分けです","result",JOptionPane.PLAIN_MESSAGE);
                    }else {
                        JOptionPane.showMessageDialog(this,"対局が終了しました。あなたは負けました","result",JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }else {
                JOptionPane.showMessageDialog(this,"その場所には置けません","Error",JOptionPane.ERROR_MESSAGE);
            }
        }else {
            JOptionPane.showMessageDialog(this,"今は相手の番です","Error",JOptionPane.ERROR_MESSAGE);
        }
	}

  	//マウスクリック時の処理
	public void mouseClicked(MouseEvent e) {
		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．キャストを忘れずに
		if(theButton.isEnabled()) {
            String command = theButton.getActionCommand();//ボタンの名前を取り出す
		    System.out.println("マウスがクリックされました。押されたボタンは " + command + "です。");//テスト用に標準出力
            acceptOperation(command);
        }
	}
	public void mouseEntered(MouseEvent e) {}//マウスがオブジェクトに入ったときの処理
	public void mouseExited(MouseEvent e) {}//マウスがオブジェクトから出たときの処理
	public void mousePressed(MouseEvent e) {}//マウスでオブジェクトを押したときの処理
	public void mouseReleased(MouseEvent e) {}//マウスで押していたオブジェクトを離したときの処理

	//テスト用のmain
	public static void main(String args[]){
		//ログイン処理
		String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//名前がないときは，"No name"とする
		}
		Player player = new Player(); //プレイヤオブジェクトの用意(ログイン)
		player.setName(myName); //名前を受付
		Othello game = new Othello(); //オセロオブジェクトを用意
		Client oclient = new Client(game, player); //引数としてオセロオブジェクトを渡す
		oclient.connectServer("localhost", 10000);
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }
}
