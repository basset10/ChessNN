package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlLoad;

public class ClientLoader {
	
	public static final int INDEX_PAWN_WHITE = 0,
							INDEX_ROOK_WHITE = 1,
							INDEX_KNIGHT_WHITE = 2,
							INDEX_BISHOP_WHITE = 3,
							INDEX_QUEEN_WHITE = 4,
							INDEX_KING_WHITE = 5,
							INDEX_PAWN_BLACK = 6,
							INDEX_ROOK_BLACK = 7,
							INDEX_KNIGHT_BLACK = 8,
							INDEX_BISHOP_BLACK = 9,
							INDEX_QUEEN_BLACK = 10,
							INDEX_KING_BLACK = 11,
							INDEX_MAIN_MENU_BG = 12;
	
	public static void loadTextures() {
		hvlLoad("INOF.hvlft");   	  	// font
		hvlFont(0);
		hvlLoad("PawnWhite.png");		// 0
		hvlLoad("RookWhite.png");		// 1
		hvlLoad("KnightWhite.png"); 	// 2
		hvlLoad("BishopWhite.png");		// 3
		hvlLoad("QueenWhite.png");		// 4
		hvlLoad("KingWhite.png");		// 5
		hvlLoad("PawnBlack.png");		// 6
		hvlLoad("RookBlack.png");		// 7
		hvlLoad("KnightBlack.png");		// 8
		hvlLoad("BishopBlack.png");		// 9
		hvlLoad("QueenBlack.png");		// 10
		hvlLoad("KingBlack.png");		// 11
		hvlLoad("MainMenuBG.png");		// 12
	}
}
