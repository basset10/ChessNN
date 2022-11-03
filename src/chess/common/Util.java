package chess.common;

import java.util.UUID;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import chess.client.ClientBoardSpace;
import chess.client.ClientPlayer;

public class Util {

	private static boolean leftMouseState = false;
	private static boolean leftMouseClick = false;

	public static String generateUUID() {
		return UUID.randomUUID().toString();
	}

	public static int getCursorX(){
		return Mouse.getX();
	}

	public static int getCursorY(){
		return Display.getHeight() - Mouse.getY();
	}

	public static float convertToPixelPositionX(int xArg, ClientPlayer p) {
		if(p.color == ClientPlayer.PlayerColor.WHITE) {
			return ((xArg)*ClientBoardSpace.SPACE_SIZE + Display.getWidth()/2 - ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2));
		}else {
			return (xArg)*-ClientBoardSpace.SPACE_SIZE + Display.getWidth()/2 + ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2);
		}
	}

	public static float convertToPixelPositionY(int yArg, ClientPlayer p) {
		if(p.color == ClientPlayer.PlayerColor.WHITE) {
			return (yArg)*ClientBoardSpace.SPACE_SIZE + Display.getHeight()/2 - ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2);
		}else {
			return (yArg)*-ClientBoardSpace.SPACE_SIZE + Display.getHeight()/2 + ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2);
		}
	}

	public static void update() {
		if(leftMouseClick) {
			leftMouseClick = false;
		}else {
			if(!leftMouseState) {
				if(!Mouse.isButtonDown(0)) {
					leftMouseClick = false;
				}else {
					leftMouseClick = true;
				}
			}
		}		
		if(Mouse.isButtonDown(0)) {
			leftMouseState = true;
		}else {
			leftMouseState = false;
		}		
	}	

	public static boolean leftMouseClick() {
		return leftMouseClick;
	}
}



