package chess.client;

import com.osreboot.hvol2.direct.HvlDirect;
import com.osreboot.ridhvl2.template.HvlChronology;
import com.osreboot.ridhvl2.template.HvlDisplayWindowed;
import com.osreboot.ridhvl2.template.HvlTemplateI;

import chess.client.menu.ClientMenuManager;
import chess.common.Util;

public class ClientMain extends HvlTemplateI{

	private ClientGame game;

	//Test!
	
	//Currently disconnects as soon as the server relays the other player lost connection.
	//May need to add some buffer time in case of packet delay
	

	public static void main(String args[]) {
		HvlChronology.registerChronology(HvlDirect.class);
		new ClientMain();
	}

	public ClientMain() {
		super(new HvlDisplayWindowed(144, 1280, 720, "Chess Simulator", true));
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() {
		ClientLoader.loadTextures();
		ClientNetworkManager.initialize();
		game = new ClientGame(ClientNetworkManager.id);
		ClientMenuManager.initialize(game);
	}

	@Override
	public void update(float delta) {
		ClientNetworkManager.update(delta);
		Util.update();
		game.update(delta);		
	}

}
