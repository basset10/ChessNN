package chess.client.menu;

import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;
import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlTexture;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import chess.client.ClientNetworkManager;
import chess.common.Util;
import chess.client.ClientGame;
import chess.client.ClientGame.GameState;
import chess.client.ClientLoader;

public class ClientMenuMain {
	
	private static final String VALID_IP_CHARS = "1234567890.";
	private static final String VALID_PORT_CHARS = "1234567890";
	
	private ArrayList<ClientButton> buttons;
	private String ip = "";
	private String port = "";
	private boolean typingIP = false;
	private boolean typingPort = false;

	public ClientMenuMain(ClientGame game) {
		buttons = new ArrayList<ClientButton>();
		buttons.add(new ClientButton(300, 100, Display.getWidth()/2f+300, Display.getHeight()/2f+80, "Connect", () ->{
			game.state = GameState.connecting;
			ClientNetworkManager.connect(ip, Integer.parseInt(port));
		}));
		buttons.add(new ClientButton(300, 100, Display.getWidth()/2f, Display.getHeight()/2f+230, "Localhost", () ->{
			game.state = GameState.connecting;
			ClientNetworkManager.connect("localhost", 25565);
		}));
	}

	public void operate() {
		
		//hvlDraw(hvlQuadc(Display.getWidth()/2, Display.getHeight()/2, Display.getWidth(), Display.getHeight()), hvlTexture(ClientLoader.INDEX_MAIN_MENU_BG));

		hvlFont(0).drawc("IP:", Display.getWidth()/2-180, Display.getHeight()/2f+54, Color.white, 1.2f);
		if(typingIP) {
			hvlDraw(hvlQuadc(Display.getWidth()/2f, Display.getHeight()/2f+54, 295, 48), Color.lightGray);
		}else {
			hvlDraw(hvlQuadc(Display.getWidth()/2f, Display.getHeight()/2f+54, 295, 48), Color.white);
		}
		hvlFont(0).draw(ip, Display.getWidth()/2-140, Display.getHeight()/2f+45, Color.black, 1.2f);


		hvlFont(0).drawc("Port:", Display.getWidth()/2-180, Display.getHeight()/2f+106, Color.white, 1.2f);
		if(typingPort) {
			hvlDraw(hvlQuadc(Display.getWidth()/2f, Display.getHeight()/2f+106, 295, 48), Color.lightGray);
		}else {
			hvlDraw(hvlQuadc(Display.getWidth()/2f, Display.getHeight()/2f+106, 295, 48), Color.white);
		}
		hvlFont(0).draw(port, Display.getWidth()/2-140, Display.getHeight()/2f+97, Color.black, 1.2f);

		if(Util.leftMouseClick()) {
			if(Util.getCursorX() >= Display.getWidth()/2f - 295/2 && Util.getCursorX() <= Display.getWidth()/2f + 295/2
					&& Util.getCursorY() >= Display.getHeight()/2f+54 - 48/2 && Util.getCursorY() <= Display.getHeight()/2f+54 + 48/2){
				typingIP = true;
				if(typingPort) typingPort = false;
			}else if(Util.getCursorX() >= Display.getWidth()/2f - 295/2 && Util.getCursorX() <= Display.getWidth()/2f + 295/2
					&& Util.getCursorY() >= Display.getHeight()/2f+106 - 48/2 && Util.getCursorY() <= Display.getHeight()/2f+106 + 48/2) {
				typingPort = true;
				if(typingIP) typingIP = false;
			}else {
				if(typingIP) typingIP = false;
				if(typingPort) typingPort = false;
			}
		}

		if(typingIP) {
			Keyboard.poll();
			while(Keyboard.next()){
				if(Keyboard.getEventKeyState()){
					if(Keyboard.getEventKey() == Keyboard.KEY_BACK){
						ip = ip.substring(0, Math.max(ip.length() - 1, 0));
					}else if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
						ip = "";
					}else{
						if(ip.length() <= 15 && VALID_IP_CHARS.indexOf(Keyboard.getEventCharacter()) != -1)
						ip += Keyboard.getEventCharacter(); 
					}
				}
			}
		}
		if(typingPort) {
			Keyboard.poll();
			while(Keyboard.next()){
				if(Keyboard.getEventKeyState()){
					if(Keyboard.getEventKey() == Keyboard.KEY_BACK){
						port = port.substring(0, Math.max(port.length() - 1, 0));
					}else if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
						port = "";
					}else{
						if(port.length() <= 5 && VALID_PORT_CHARS.indexOf(Keyboard.getEventCharacter()) != -1)
						port += Keyboard.getEventCharacter();
					}
				}
			}
		}
		hvlFont(0).drawc("Chess Sim", Display.getWidth()/2, Display.getHeight()/2-100, Color.white, 4f);
		for(ClientButton b : buttons) {
			b.operate();
		}
	}

}
