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

public class ClientMenuMain {
	
	private ArrayList<ClientButton> buttons;

	public ClientMenuMain(ClientGame game) {
		buttons = new ArrayList<ClientButton>();
		buttons.add(new ClientButton(300, 100, Display.getWidth()/2f, Display.getHeight()/2f+230, "Play AI", () ->{
			game.state = GameState.playing;
		}));
	}

	public void operate() {	
		//hvlDraw(hvlQuadc(Display.getWidth()/2, Display.getHeight()/2, Display.getWidth(), Display.getHeight()), hvlTexture(ClientLoader.INDEX_MAIN_MENU_BG));
		hvlFont(0).drawc("ChessJet", Display.getWidth()/2, Display.getHeight()/2-100, Color.white, 4f);
		for(ClientButton b : buttons) {
			b.operate();
		}
	}

}
