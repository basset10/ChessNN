package chess.client.menu;

import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import chess.client.ClientGame;
import chess.client.ClientGame.GameState;
import chess.common.Util;

public class ClientMenuManager {

	private ClientMenuManager() {}

	public static MenuState menu = MenuState.main;

	public static ClientMenuMain mainMenu;
	public static ClientMenuPostgame postgameMenu;

	public static enum MenuState{
		main,
		options,
		postgame;
	}

	public static void initialize(ClientGame game) {
		mainMenu = new ClientMenuMain(game);
		postgameMenu = new ClientMenuPostgame(game);
	}

	public static void manageMenus(ClientGame game) {
		if(menu == MenuState.main) {
			mainMenu.operate();
		}else if(menu == MenuState.postgame) {
			postgameMenu.operate();
		}
	}

}
