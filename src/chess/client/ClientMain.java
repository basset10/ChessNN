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
	//Color selection can't be changed after returning to menu

	public static void main(String args[]) {
		new ClientMain();
	}

	public ClientMain() {
		super(new HvlDisplayWindowed(500, 1280, 720, "Chess Simulator", false));
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
