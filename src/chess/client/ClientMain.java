package chess.client;

import com.osreboot.ridhvl2.template.HvlDisplayWindowed;
import com.osreboot.ridhvl2.template.HvlTemplateI;

import chess.client.menu.ClientMenuManager;
import chess.common.Util;

public class ClientMain extends HvlTemplateI{

	private ClientGame game;

	//Convert to single player Vs. AI
	//Remove all packet communication, process everything on client
	//AI begins by calculating all legal moves and choosing one randomly
	//Game can be Player v. AI or AI v. AI
	
	
	//KNOWN BUGS
	//Ending the game with a promotion crashes?
	/*Exception in thread "main" java.lang.IllegalArgumentException: bound must be positive
		at java.base/java.util.Random.nextInt(Random.java:322)
		at chess.client.ClientPlayer.generateRandomMove(ClientPlayer.java:47)
		at chess.client.ClientGame.update(ClientGame.java:392)
		at chess.client.ClientMain.update(ClientMain.java:37)
		at com.osreboot.ridhvl2.template.HvlTemplate$1.tick(HvlTemplate.java:53)
		at com.osreboot.ridhvl2.template.HvlTimer.start(HvlTimer.java:46)
		at com.osreboot.ridhvl2.template.HvlTemplate.start(HvlTemplate.java:69)
		at com.osreboot.ridhvl2.template.HvlTemplateI.<init>(HvlTemplateI.java:59)
		at com.osreboot.ridhvl2.template.HvlTemplateI.<init>(HvlTemplateI.java:25)
		at chess.client.ClientMain.<init>(ClientMain.java:23)
		at chess.client.ClientMain.main(ClientMain.java:19)
	*/
	//Color selection can't be changed after returning to menu

	public static void main(String args[]) {
		new ClientMain();
	}

	public ClientMain() {
		super(new HvlDisplayWindowed(144, 1280, 720, "Chess Simulator", true));
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		ClientLoader.loadTextures();
		game = new ClientGame(Util.generateUUID());
		ClientMenuManager.initialize(game);
	}

	@Override
	public void update(float delta) {
		Util.update();
		game.update(delta);		
	}

}
