package chess.client.menu;

import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl2.HvlAction;

import chess.common.Util;

public class ClientButton {

	public int xLength;
	public int yLength;
	public float xPos;
	public float yPos;
	public String text;
	HvlAction.A0 action;

	public ClientButton(int xLengthArg, int yLengthArg, float xPosArg, float yPosArg, String textArg, HvlAction.A0 actionArg) {
		xLength = xLengthArg;
		yLength = yLengthArg;
		xPos = xPosArg;
		yPos = yPosArg;
		action = actionArg;
		text = textArg;
	}

	public boolean clicked() {
		if(Util.getCursorX() >= xPos - xLength/2 && Util.getCursorX() <= xPos + xLength/2
				&& Util.getCursorY() >= yPos - yLength/2 && Util.getCursorY() <= yPos + yLength/2
				&& Util.leftMouseClick()) {
			return true;
		}else {
			return false;
		}
	}


	public void onClick() {
		if(clicked()) {
			action.run();
		}
	}

	public void operate() {
		//Hover
		if(Util.getCursorX() >= xPos - xLength/2 && Util.getCursorX() <= xPos + xLength/2
				&& Util.getCursorY() >= yPos - yLength/2 && Util.getCursorY() <= yPos + yLength/2){
			hvlDraw(hvlQuadc(xPos, yPos, xLength, yLength), Color.darkGray);
		}else {
			hvlDraw(hvlQuadc(xPos, yPos, xLength, yLength), Color.lightGray);
		}
		hvlFont(0).drawc(text, xPos-1, yPos+1, Color.black, 2f);
		hvlFont(0).drawc(text, xPos, yPos, Color.white, 2f);
		onClick();
	}

}
