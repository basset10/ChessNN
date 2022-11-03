package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;
import static com.osreboot.ridhvl2.HvlStatics.hvlTexture;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

public class ClientPromotionTypeUI {
	
	public static final int NONE = 0;
	public static final int QUEEN = 1;
	public static final int KNIGHT = 2;
	public static final int ROOK = 3;
	public static final int BISHOP = 4;
	
	public static int selectedPromotion = NONE;

	public static void draw(ClientPlayer player) {
		hvlFont(0).drawc("Select Promotion", Display.getWidth()/2 + 425, Display.getHeight()/2-125, Color.white, 0.9f);
		
		hvlDraw(hvlQuadc(Display.getWidth()/2 + 425, Display.getHeight()/2, 225, 225), Color.lightGray);
		
		hvlDraw(hvlQuadc(Display.getWidth()/2 + 425+55, Display.getHeight()/2+55, 96, 96), Color.darkGray);
		hvlDraw(hvlQuadc(Display.getWidth()/2 + 425+55, Display.getHeight()/2-55, 96, 96), Color.darkGray);
		hvlDraw(hvlQuadc(Display.getWidth()/2 + 425-55, Display.getHeight()/2+55, 96, 96), Color.darkGray);
		hvlDraw(hvlQuadc(Display.getWidth()/2 + 425-55, Display.getHeight()/2-55, 96, 96), Color.darkGray);
		
		hvlDraw(hvlQuadc(Display.getWidth()/2 + 425+55, Display.getHeight()/2+55, 90, 90), Color.white);
		hvlDraw(hvlQuadc(Display.getWidth()/2 + 425+55, Display.getHeight()/2-55, 90, 90), Color.white);
		hvlDraw(hvlQuadc(Display.getWidth()/2 + 425-55, Display.getHeight()/2+55, 90, 90), Color.white);
		hvlDraw(hvlQuadc(Display.getWidth()/2 + 425-55, Display.getHeight()/2-55, 90, 90), Color.white);
		
		if(player.color == ClientPlayer.PlayerColor.BLACK) {
			hvlDraw(hvlQuadc(Display.getWidth()/2 + 425+55, Display.getHeight()/2+55, 80, 80), hvlTexture(ClientLoader.INDEX_QUEEN_BLACK));
			hvlDraw(hvlQuadc(Display.getWidth()/2 + 425+55, Display.getHeight()/2-55, 80, 80), hvlTexture(ClientLoader.INDEX_KNIGHT_BLACK));
			hvlDraw(hvlQuadc(Display.getWidth()/2 + 425-55, Display.getHeight()/2+55, 80, 80), hvlTexture(ClientLoader.INDEX_ROOK_BLACK));
			hvlDraw(hvlQuadc(Display.getWidth()/2 + 425-55, Display.getHeight()/2-55, 80, 80), hvlTexture(ClientLoader.INDEX_BISHOP_BLACK));
		}else {
			hvlDraw(hvlQuadc(Display.getWidth()/2 + 425+55, Display.getHeight()/2+55, 80, 80), hvlTexture(ClientLoader.INDEX_QUEEN_WHITE));
			hvlDraw(hvlQuadc(Display.getWidth()/2 + 425+55, Display.getHeight()/2-55, 80, 80), hvlTexture(ClientLoader.INDEX_KNIGHT_WHITE));
			hvlDraw(hvlQuadc(Display.getWidth()/2 + 425-55, Display.getHeight()/2+55, 80, 80), hvlTexture(ClientLoader.INDEX_ROOK_WHITE));
			hvlDraw(hvlQuadc(Display.getWidth()/2 + 425-55, Display.getHeight()/2-55, 80, 80), hvlTexture(ClientLoader.INDEX_BISHOP_WHITE));
		}		
	}	
}
