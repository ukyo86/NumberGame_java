import java.io.*;
//ゲームの制御管理を行うクラス
public class GameController{
	private static final int GAME_START = 1; 
	private static final int GAME_HELP = 2;
	private static final int SERVER_TYPE = 1;
	private static final int CLIENT_TYPE = 2;
	private int connectionType; //socket通信の接続方式(サーバーorクライアント)
	private GameFunction gf;
	private SocketConnector sc;
	private String input,input2,message;
	private BufferedReader reader; 
	private boolean Connectionflag, Settingflag; //通信の確立フラグ、ゲーム設定完了フラグ
	
	public GameController(){ //コンストラクタ
		gf = new GameFunction();
		sc = new SocketConnector();
		reader = new BufferedReader(new InputStreamReader(System.in));
		connectionType = 0;
		Connectionflag = false;
		Settingflag = false;
	}
	
	public void MainMenu(){ //初期画面
		System.out.println("------------------------------");
		System.out.println("          Hit And Blow");
		System.out.println("------------------------------");
		while(true){
			System.out.println("----------MainMenu----------");
			System.out.println("1.GameStart	2.Help	 (数字で選択)");
			try{
				input = reader.readLine();
				try{
					if(Integer.parseInt(input)==GAME_START){
						return;
					}else if(Integer.parseInt(input)==GAME_HELP){
						DisplayHelp("Rule");
						DisplayHelp("Item");
					}else{
						System.out.println("誤った入力が確認されました。");
						System.out.println("1か2を入力してください。\n");
					}
				}catch(NumberFormatException e){
					System.out.println("誤った入力が確認されました。");
					System.out.println("1か2を入力してください。\n");
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public void DisplayHelp(String input){ //ヘルプを表示(inputには"Rule"or"Item"のみを受付)
		if(input == "Rule" || input == "Item"){	
			System.out.println(HelpMessageGenerator(input));
		}else{
			System.out.println("argument Error");
		}
	}
	
	public String HelpMessageGenerator(String input){ //ヘルプ内容を生成
		String helpmessage=null;
		if(input=="Rule"){
			helpmessage = "＜ルール＞\n" 
									+"二人対戦用の数当てゲームです。\n"
									+"各自1～9の3桁の数字を設定しHitとBlowのヒントを元に相手の数字を当てましょう。\n"
									+"※設定する数字は重複なし\n"
									+"Hit : 数字と位置があっている\n"
									+"Blow : 数字は含まれるが位置が異なる\n"
									+"例）設定数字 : 519\n"
									+"解答　523　->　1Hit 0Blow\n"
									+"解答　248　->　0Hit 0Blow\n"
									+"解答　915　->　1Hit 2Blow\n"
									+"解答　519　->　3Hit 0Blow\n";
		}else if(input=="Item"){
			helpmessage = "＜Itemの説明＞\n"
									+"相手が2Hitになったとき、自分はアイテムが使用できるようになります。\n\n"
									+"Double : 2回連続で自分のターンになります。\n\n"
									+"HighAndLow : 相手の設定数字がHighかLowか知ることができます。\n"
									+"(Low:1～4  High:5～9)\n"
									+"例）489 -> LHH\n\n"
									+"Shuffle : 自分の設定数字をランダムでシャッフルできます。\n\n"
									+"GetOneNumber : 相手の設定数字のうち1つだけ位置と数字を知ることができます。\n"
									+"例）539 -> **9\n";
		}
		return helpmessage;
	}
	
	public void ConnectionSetting(){ //通信の設定を行う
		System.out.println("-----Connection Setting-----");
		System.out.println("サーバーとクライアントのどちらになるか選択してください。");
		System.out.println("1.サーバー　2.クライアント　(数字で選択)");
		try{	
			input = reader.readLine();
			while(true){
				try{
					if(Integer.parseInt(input)==SERVER_TYPE || Integer.parseInt(input)==CLIENT_TYPE){
						connectionType = Integer.parseInt(input);
						break;
					}else{
						System.out.println("誤った入力が確認されました。");
						System.out.println("1か2を入力してください。");
						input = reader.readLine();
					}
				}catch(NumberFormatException e){
					System.out.println("誤った入力が確認されました。");
					System.out.println("1か2を入力してください。");
					input = reader.readLine();
				}
			}
			//通信の立場(サーバーorクライアント)の違いによって接続方法を分岐
			if(connectionType == SERVER_TYPE){
				sc.startServer();
				Connectionflag = true;
			}else if(connectionType == CLIENT_TYPE){
				while(true){
					System.out.println("接続先のホスト名を入力してください。");
					input = reader.readLine();
					sc.setHost(input);
					if(sc.startConnection()) {
						Connectionflag = true;
						break;
					}
				}	
			}
		}catch(IOException e){
				System.out.println("IOError");
				e.printStackTrace();
		}
	}
	
	public void ConnectionEnd(){ //通信を終了させる
		if(!Connectionflag){
			return;
		}else if(connectionType == SERVER_TYPE){
			sc.terminateServer();
			Connectionflag = false;
		}else if(connectionType == CLIENT_TYPE){
			sc.terminateConnection();
			Connectionflag = false;
		}
	}
	
	public void GameSetting(){ //ゲームの初期設定を行う
		if(!Connectionflag){
			System.out.println("Connection Error");
			return;
		}
		System.out.println("-------Number Setting-------");		//3桁の数字の設定
		System.out.println("1～9の3桁の数字を入力してください。(数字の重複なし)");
		try{
			input = reader.readLine();
			while(true){
				if(gf.checkInput(input)){
					gf.setMyNumber(input);
					break;
				}else{
					System.out.println("誤った入力を確認しました。");
					System.out.println("1～9の3桁の数字を入力してください。(数字の重複なし)");
					input = reader.readLine();
				}
			}	
			System.out.println("相手が数字を設定中...\n");
			if(connectionType == SERVER_TYPE){ //数字を設定したことを確認
				sc.messageSender("NumberSet OK");
				message = sc.messageReciever();
				System.out.println(message);
			}else if(connectionType == CLIENT_TYPE){
				message = sc.messageReciever();
				sc.messageSender("NumberSet OK");
				System.out.println(message);
			}
			
			if(message.equals("NumberSet OK")){	//先攻、後攻の決定 
				if(connectionType == SERVER_TYPE){
					System.out.println("\n先攻後攻をランダムで決めます。");
					int r = (int)(Math.random()*10);
					if(r<5){
						gf.setWhoseTurn("Your turn");
						sc.messageSender("Opponent's turn");
					}else{
						gf.setWhoseTurn("Opponent's turn");
						sc.messageSender("Your turn");
					}
				}else if(connectionType == CLIENT_TYPE){
					System.out.println("\n先攻後攻をランダムで決めます。");
					message = sc.messageReciever();
					gf.setWhoseTurn(message);
				}
			}else{
				System.out.println("NumberSetting Error");
				return;
			}
			if(gf.isMyTurnflag()){
				System.out.println("あなたは先攻です。\n");
			}else{
				System.out.println("あなたは後攻です。\n");
			}
			Settingflag = true;
		}catch(IOException e){
			System.out.println("IOError");
			e.printStackTrace();
		}
	}
	
	public void GameStart(){ //ゲームを開始させる
		if(!Settingflag){
			System.out.println("GameSetting Error");
			return;
		}
		System.out.println("-----Game Start!-----");
		try{
			while(!gf.isEndflag()){ //ゲーム終了フラグが立つまで自分と相手のターンを繰り返す。
				if(gf.isMyTurnflag()){ //gf.Turnflagでターンを管理
					System.out.println("-----自分のターン-----");
					if(gf.isItemflag()){ //gf.Itemflagがtrueのとき、アイテムの使用のアクションを行う。
						MyItemAction();
					}
					System.out.println("1～9の3桁の数字を入力してください。");
					input = reader.readLine();
					while(true){
						if(gf.checkInput(input)){
							gf.setMyAnswer(input);
							break;
						}else{
							System.out.println("誤った入力を確認しました。");
							System.out.println("1～9の3桁の数字を入力してください。(数字の重複なし)");
							input = reader.readLine();
						}
					}
					sc.messageSender(gf.getMyAnswer()); //自分の解答を送信
					input = sc.messageReciever(); //ヒット数を受信
					input2 = sc.messageReciever(); //ブロー数を受信
					gf.setMyHitandBlow(Integer.parseInt(input), Integer.parseInt(input2));
					System.out.println(input+"Hit  "+input2+"Blow");
					gf.checkMyAnswer(); //解答確認
					if(!gf.isMyDoubleflag()){
						gf.changeTurn(); //ターンの切り替え
					}else{
						gf.startMyDouble(); //アイテムDoubleを使用
					}
				}else{
					System.out.println("-----相手のターン-----");
					System.out.println("please wait");
					input = sc.messageReciever(); //相手の解答を受信
					if(input.equals("UseItem")){
						OppItemAction();
						input = sc.messageReciever(); 
					}
					System.out.println("相手は"+input+"と解答してきました。");
					gf.setOppAnswer(input); //相手の解答をセット
					gf.checkOppAnswer(); 	//解答の答え合わせ
					input = Integer.toString(gf.getOppHit());
					input2 = Integer.toString(gf.getOppBlow());
					sc.messageSender(input); //ヒット数を送信
					sc.messageSender(input2); //ブロー数を送信
					System.out.println(input+"Hit  "+input2+"Blow  |  自分の設定数字は"+gf.getMySetNumber());
					if(!gf.isOppDoubleflag()){
						gf.changeTurn(); //ターンの切り替え
					}else{
						gf.startOppDouble(); //相手がアイテムDoubleを使用
					}
				}
			}
			gf.checkResult(); //ゲームが終了したら勝敗結果を確認
			message = gf.getMyMessage(); 
			System.out.println(message); //勝敗結果を出力
			System.out.println("ゲームを終了します。");
			
		}catch(IOException e){
			System.out.println("IOError");
			e.printStackTrace();
		}
	}
	
	public void MyItemAction(){ //自分のアイテム使用アクション
		System.out.println("アイテムを使用できます。");
			while(true){
				System.out.println("アイテムを使用しますか？　1.はい　2.いいえ　(数字で選択)");
				try{
					input = reader.readLine();
					if(input.equals("1")){ //アイテム使用時
						System.out.println("使用アイテムを選択してください。");
						System.out.println("1.Double  2.HighAndLow  3.Shuffle  4.GetOneNumber　(数字で選択)");
						System.out.println("アイテムの説明を知りたい場合は5を入力してください。");
						input = reader.readLine();
						if(input.equals("1")){
							if(gf.useMyItem("Double")){
								sc.messageSender("UseItem");
								sc.messageSender(gf.getOppMessage());
								System.out.println(gf.getMyMessage());
								return;
							}else{
								System.out.println("Doubleは使えません。");
							}
							
						}else if(input.equals("2")){
							if(gf.useMyItem("HighAndLow")){
								sc.messageSender("UseItem");
								sc.messageSender(gf.getOppMessage());
								message = sc.messageReciever();
								System.out.println("相手の設定数字のHighAndLowを表示します。");
								System.out.println(message);
								return;
							}else{
								System.out.println("HighAndLowは使えません。");
							}
						}else if(input.equals("3")){
							if(gf.useMyItem("Shuffle")){
								System.out.println("自分の設定数字をシャッフルします。");
								sc.messageSender("UseItem");
								sc.messageSender(gf.getOppMessage());
								System.out.println("自分の設定数字は"+gf.getMySetNumber()+"になりました。\n");
								return;
							}else{
								System.out.println("GetOneNumberは使えません。");
							}
						}else if(input.equals("4")){
					    	if(gf.useMyItem("GetOneNumber")){
								sc.messageSender("UseItem");
								sc.messageSender(gf.getOppMessage());
								message = sc.messageReciever();
								System.out.println("相手の設定数字のうち1つだけ位置と数字を表示します。");
								System.out.println("非表示数字は＊となっています。");
								System.out.println(message);
								return;
							}else{
								System.out.println("GetOneNumberは使えません。");
							}
						}else if(input.equals("5")){
							DisplayHelp("Item");
						}else{
							System.out.println("誤った入力を確認しました。");
						}
					}else if(input.equals("2")){ //アイテム不使用時
						return;
					}else{
						System.out.println("誤った入力を確認しました。");
					}
				}catch(IOException e){
					System.out.println("誤った入力を確認しました。");
				}
			}
	}
	
	public void OppItemAction(){ //相手のアイテム使用アクション
		try{
			message = sc.messageReciever();
			if(message.equals("Double")){
				gf.useOppItem(message);
				message  = gf.getMyMessage();
				System.out.println(message);
			}else if(message.equals("HighAndLow")){
				gf.useOppItem(message);
				sc.messageSender(gf.getOppMessage());
				message = gf.getMyMessage();
				System.out.println(message);
			}else if(message.equals("Shuffle")){
				gf.useOppItem(message);
				message = gf.getMyMessage();
				System.out.println(message);
			}else if(message.equals("GetOneNumber")){
				gf.useOppItem(message);
				sc.messageSender(gf.getOppMessage());
				message  = gf.getMyMessage();
				System.out.println(message);
			}else{
				System.out.println("OppItemError");
			}
		}catch(IOException e){
			System.out.println("IOError");
		}
	}
	
	
		
}