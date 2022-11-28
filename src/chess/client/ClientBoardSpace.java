package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlColor;
import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;

import org.lwjgl.opengl.Display;

import com.osreboot.ridhvl2.HvlCoord;


/*
 * Class responsible for creating and drawing the individual squares used for an 8x8 chess board.
 */
public class ClientBoardSpace {

	
	public static final int SPACE_SIZE = 75;
	
	public int xPos;
	public int yPos;
	public Color color;

	public static enum Color{
		WHITE,
		BLACK;
	}

	public ClientBoardSpace(int xPosArg, int yPosArg, Color colorArg) {
		xPos = xPosArg;
		yPos = yPosArg;
		color = colorArg;
	}
	
	public void draw() {
		if(this.color == Color.BLACK) {
			hvlDraw(hvlQuadc(this.getPixelPosition().x, this.getPixelPosition().y, SPACE_SIZE, SPACE_SIZE), hvlColor(0.25f, 0.35f, 0.25f));
		}else if(this.color == Color.WHITE) {
			hvlDraw(hvlQuadc(this.getPixelPosition().x, this.getPixelPosition().y, SPACE_SIZE, SPACE_SIZE), hvlColor(0.8f, 0.8f, 0.8f));
		}
	}
	
	//Returns the exact center coordinate of the referenced grid square
	public HvlCoord getPixelPosition() {
		return new HvlCoord((xPos)*SPACE_SIZE + Display.getWidth()/2 - ((SPACE_SIZE * 2) - SPACE_SIZE/2),
				(yPos)*SPACE_SIZE + Display.getHeight()/2 - ((SPACE_SIZE * 2) - SPACE_SIZE/2));
	}	

}
