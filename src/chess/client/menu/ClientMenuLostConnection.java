package chess.client.menu;

import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import chess.client.ClientGame;
import chess.client.ClientNetworkManager;
import chess.client.ClientGame.GameState;
import chess.client.menu.ClientMenuManager.MenuState;
import chess.common.Util;

public class ClientMenuLostConnection {

private ArrayList<ClientButton> buttons;
	
	public ClientMenuLostConnection() {
		buttons = new ArrayList<ClientButton>();
		buttons.add(new ClientButton(300, 100, 200, Display.getHeight()-100, "Return", () ->{
			ClientMenuManager.menu = MenuState.main;
			ClientNetworkManager.connect("localhost", 25565);
		}));
	}
	
	public void operate() {
		hvlFont(0).drawc("Lost connection to opponent!", Display.getWidth()/2, Display.getHeight()/3, Color.white, 2f);
		for(ClientButton b : buttons) {
			b.operate();
		}
	}
	
}
