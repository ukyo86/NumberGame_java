public class Main{
	public static void main(String[] args){
		GameController gc = new GameController();
		
		gc.MainMenu();
		gc.ConnectionSetting();
		gc.GameSetting();
		gc.GameStart();
		gc.ConnectionEnd();
	}
}