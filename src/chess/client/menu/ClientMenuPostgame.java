package chess.client.menu;

import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;
import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlTexture;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import chess.common.Util;
import chess.client.ClientGame;
import chess.client.ClientGame.GameState;
import chess.client.ClientLoader;

public class ClientMenuPostgame {
	
	private ArrayList<ClientButton> buttons;

	public ClientMenuPostgame(ClientGame game) {
		buttons = new ArrayList<ClientButton>();
		buttons.add(new ClientButton(200, 120, Display.getWidth()/2f+450, Display.getHeight()/2f-100, "Play Again", () ->{			
			game.state = GameState.playingHuman;
			game.reset();
		}));
		buttons.add(new ClientButton(200, 120, Display.getWidth()/2f+450, Display.getHeight()/2f+100, "Main Menu", () ->{			
			game.state = GameState.menu;
			ClientMenuManager.menu = ClientMenuManager.MenuState.main;
			game.reset();
			
		}));

	}

	public void operate() {	
		
		for(ClientButton b : buttons) {
			b.operate();
		}
		
	}

}
