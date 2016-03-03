import java.io.*;

/*Hit And Blow 数当てゲームの機能を含むクラス*/
public class GameFunction {
	
	private String mySetNumber; //自分の設定する3桁の数字
	private String myAnswer, oppAnswer; //自分の解答、対戦相手の解答
	private int myHit, myBlow, oppHit, oppBlow; //自分のヒット数、ブロー数、相手のヒット数、ブロー数
	private boolean Endflag, Itemflag, myTurnflag, Winnerflag; 
	//ゲーム終了フラグ、アイテム使用可能フラグ、自分のターンフラグ, 勝利フラグ
	private int Double, HighAndLow, GetOneNumber, Shuffle; //それぞれのアイテムの使用可能回数
	private boolean myDoubleflag, oppDoubleflag; //アイテムDoubleを使用中のフラグ
	private String myMessage, oppMessage; //自分へのメッセージ、相手へのメッセージ
	
	
	public GameFunction() { //GameFunctionクラスのコンストラクタ
		Endflag = false;
		Itemflag = false;
		myTurnflag = false;
		Winnerflag = false;
		myHit = 0;
		myBlow = 0;
		oppHit = 0;
		oppBlow = 0;
		
		Double = 1;
		HighAndLow = 1;
		GetOneNumber = 1;
		Shuffle = 1;
		myDoubleflag = false;
		oppDoubleflag = false;
	}
	
	//アクセスメソッド
	public void setMyNumber(String input){
		mySetNumber = input;
	}
	
	public void setMyAnswer(String input){
		myAnswer = input;
	}
	
	public void setOppAnswer(String input){
		oppAnswer = input;
	}
	
	public String getMySetNumber(){
		return mySetNumber;
	}
	public String getMyAnswer(){
		return myAnswer;
	}
	
	public void setMyHitandBlow(int hit, int blow){
		myHit = hit;
		myBlow = blow;
	}
	
	public int getOppHit(){
		return oppHit;
	}
	
	public int getOppBlow(){
		return oppBlow;
	}

	public void setMyMessage(String input){
		myMessage = input;
	}
	
	public void setOppMessage(String input){
		oppMessage  = input;
	}
	
	public String getMyMessage(){
		return myMessage;
	}
	
	public String getOppMessage(){
		return oppMessage;
	}
	
	public boolean isEndflag(){
		return Endflag;
	}

	public boolean isMyTurnflag(){
		return myTurnflag;
	}
	
	public boolean isItemflag(){
		return Itemflag;
	}
	
	public boolean isMyDoubleflag(){
		return myDoubleflag;
	}
	public boolean isOppDoubleflag(){
		return oppDoubleflag;
	}
	
	public void startMyDouble(){ //アイテムDoubleを使用したらフラグはfalseに戻す。
		myDoubleflag = false;
	}
	public void startOppDouble(){
		oppDoubleflag = false;
	}
	
	public void setWhoseTurn(String input){ //誰のターンなのかを指定する
		if(input.equals("Your turn")){
			myTurnflag=true;
		}else{
			myTurnflag=false;
		}
	}
	
	public void changeTurn(){ //ターンを切り替える
		if(myTurnflag==true){
			myTurnflag = false;
		}else{
			myTurnflag = true;
		}
	}
	public void checkWhoseTurn(){  //誰のターンなのかを確認する
		if(myTurnflag){
			myMessage = "My turn";
		}else{
			myMessage = "Opponent's turn";
		}
	}
	
	public void checkResult(){ //ゲームの勝敗の結果を確認する
		if(Endflag){
			if(Winnerflag){
				myMessage = "Win!!";
			}else{
				myMessage = "Lose...";
			}
		}else{
			myMessage = "This game hasn't finished yet";
		}
	}
	
	//自分のターンで使用
	public void checkMyAnswer(){ //自分の解答をチェックする
		if(myHit == 3){
			Winnerflag = true;
			Endflag = true;
		}
	}
	
	//相手のターンで使用
	public void checkOppAnswer(){ //自分の設定数字と相手の解答数字の一致度をチェックする
		if(oppAnswer.equals(mySetNumber)){
			oppHit = 3;
			oppBlow =0;
			Winnerflag = false;
			Endflag = true;
		}else{
			oppHit = 0;
			oppBlow = 0;
			for(int i = 0; i< 3; i++){
				String s = oppAnswer.substring(i,i+1);
				if(mySetNumber.contains(s)){
					if(mySetNumber.indexOf(s) == oppAnswer.indexOf(s)){
						oppHit++;
					}else{
						oppBlow++;
					}
				}
			}
			if(oppHit == 2 && Itemflag == false ){ //相手が2Hitになったら使用可能
				Itemflag = true;
			}
		}
		myMessage = oppHit + "Hit  " +oppBlow + "Blow";
	}
	
	public boolean checkInput(String input){//このゲーム内で使える数字であるかチェックする。
	//条件　・1～9の3桁の数字　・数字の重複なし
		try{
			if(Integer.parseInt(input) >= 123 && Integer.parseInt(input) <= 987){
				if(input.contains("0")){ //0は含んではいけない。
					return false;
				}
				String s1 = null;
				String s2 = null;
				for(int i = 0; i < 2; i++){ //数字は重複してはいけない。
					 s1 = input.substring(i,i+1);
					for(int j = i+1; j<3;j++){
						s2 = input.substring(j,j+1);
						if(s1.equals(s2)) return false;
					}
				}
				return true; //問題なければtrueを返す。
			}else{
				return false;
			}
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public boolean useMyItem(String input){ //自分がアイテムを使用(使用回数が残っていれば使用可能)
		if(input.equals("Double") && Double > 0){ //2回連続で自分のターン
			myDoubleflag = true;
			Itemflag = false;
			Double -= 1;
			oppMessage = "Double";
			myMessage = "2回連続で自分のターンになります。";
			return true;
		
		}else if(input.equals("HighAndLow") && HighAndLow > 0){ //相手の設定数字のHighかLowを知る(Low:1~4   High: 5~9)
			Itemflag = false;
			HighAndLow -= 1;
			oppMessage =  "HighAndLow";
			return true;
		
		}else if(input.equals("GetOneNumber") && GetOneNumber > 0){ //相手の設定数字のうち1つだけ位置と数字を知る
			Itemflag = false;
			GetOneNumber -= 1;
			oppMessage = "GetOneNumber";
			return true;
		
		}else if(input.equals("Shuffle") && Shuffle > 0){ //自分の設定数字をシャッフル
			Itemflag = false;
			Shuffle -= 1;
			oppMessage = "Shuffle";
			String[] swap = new String[3];//入替用文字列
			for(int i = 0; i < 3; i++){
				swap[i] = mySetNumber.substring(i,i+1);//swapに一字ずつ格納
			}
			int[] j = new int[3];
			for(int i = 0; i < 3; i++){
				j[i] = (int)(Math.random() * 3);
				for(int k = 1; k <= i; k++){
					if(j[i] == j[i-k]){
						i--;
						break;
					}
				}
			}
			mySetNumber = swap[j[0]] + swap[j[1]] + swap[j[2]]; //シャッフル結果を格納
			return true;
		}else{
			myMessage = "ItemError";
			return false;
		}
		
	}
	
	public boolean useOppItem(String input){ //相手がアイテムを使用した際、適当な送信メッセージを生成
		String str=null;
		String[] s = new String[3];
		if(input.equals("Double") ){
			oppDoubleflag = true;
			myMessage = "相手がDoubleを使用しました。\n2回連続で相手のターンです。";
			return true;
		}else if(input.equals("HighAndLow") ){ //自分の設定数字のHighかLowを教える(Low:1~4   High: 5~9)
			myMessage = "相手がHighAndLowを使用しました。\n"
									+"相手に自分の設定数字のHighかLowを教えます。";
			for(int i = 0; i < 3; i++){
				str = mySetNumber.substring(i,i+1);
				if(Integer.parseInt(str)<=4){
					s[i] = "L";
				}else{
					s[i] = "H";
				}
			}
			oppMessage = s[0]+s[1]+s[2];
			return true;
		}else if(input.equals("GetOneNumber") ){ //自分の設定数字のうち1つだけ位置と数字を教える
			myMessage = "相手がGetOneNumberを使用しました。";
			int j = (int)(Math.random() * 3);//数字を知る場所をランダムで決定
			for(int i = 0; i < 3; i++){
				s[i] = "*";
				if(i == j) s[i] = mySetNumber.substring(i,i+1);
			}
			oppMessage = s[0]+s[1]+s[2];
			return true;
		}else if(input.equals("Shuffle") ){ //相手の設定数字をシャッフル
			myMessage = "相手がShuffleを使用しました。";
			return true;
		}else{
			myMessage = "ItemError";
			return false;
		}
	}
}
