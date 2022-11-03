package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;
import static com.osreboot.ridhvl2.HvlStatics.hvlLine;
import static com.osreboot.ridhvl2.HvlStatics.hvlTexture;
import static com.osreboot.ridhvl2.HvlStatics.hvlTranslate;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl2.HvlCoord;
import com.osreboot.ridhvl2.HvlMath;

import chess.client.ClientPlayer.PlayerColor;
import chess.common.Util;

public class ClientPiece {

	public static final int PIECE_SIZE = 50;
	public static final float TRANSLATION_SPEED = 750f;
	public boolean moved = false;
	public boolean inMotion = false;

	enum PieceType{
		PAWN(ClientLoader.INDEX_PAWN_BLACK, ClientLoader.INDEX_PAWN_WHITE),
		KNIGHT(ClientLoader.INDEX_KNIGHT_BLACK, ClientLoader.INDEX_KNIGHT_WHITE),
		ROOK(ClientLoader.INDEX_ROOK_BLACK, ClientLoader.INDEX_ROOK_WHITE),
		BISHOP(ClientLoader.INDEX_BISHOP_BLACK, ClientLoader.INDEX_BISHOP_WHITE),
		QUEEN(ClientLoader.INDEX_QUEEN_BLACK, ClientLoader.INDEX_QUEEN_WHITE),
		KING(ClientLoader.INDEX_KING_BLACK, ClientLoader.INDEX_KING_WHITE);

		final int blackTexture, whiteTexture;

		PieceType(int blackTextureArg, int whiteTextureArg){
			blackTexture = blackTextureArg;
			whiteTexture = whiteTextureArg;
		}
	}

	enum PieceColor{
		WHITE,
		BLACK;
	}

	public int xPos;
	public int yPos;
	public int translationMemoryX;
	public int translationMemoryY;
	public float horizontalTravel;
	public float verticalTravel;
	public boolean positiveHorizontalTravel;
	public boolean positiveVerticalTravel;
	public PieceType type;
	public PieceColor color;
	public boolean enPassantVulnerable = false;

	public ClientPiece(PieceType typeArg, PieceColor colorArg, int xPosArg, int yPosArg) {
		type = typeArg;
		xPos = xPosArg;
		yPos = yPosArg;
		color = colorArg;
	}

	public ClientPiece() {}

	public ArrayList<ClientMove> getAllValidMoves(ClientBoard boardArg, ClientPlayer player) {
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();
		moves = ClientPieceLogic.getAllValidMoves(this, boardArg, player, true);
		return moves;
	}

	public HvlCoord getPixelPosition(ClientPlayer p) {
		if(p.color == PlayerColor.WHITE) {
			return new HvlCoord((xPos)*ClientBoardSpace.SPACE_SIZE + Display.getWidth()/2 - ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2),
					(yPos)*ClientBoardSpace.SPACE_SIZE + Display.getHeight()/2 - ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2));

		}else {

			return new HvlCoord((xPos)*-ClientBoardSpace.SPACE_SIZE + Display.getWidth()/2 + ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2),
					(yPos)*-ClientBoardSpace.SPACE_SIZE + Display.getHeight()/2 + ((ClientBoardSpace.SPACE_SIZE * 4) - ClientBoardSpace.SPACE_SIZE/2));

		}
	}

	public void draw(ClientPlayer p) {
		if(!inMotion) {
			if(color == PieceColor.BLACK) {
				hvlDraw(hvlQuadc(getPixelPosition(p).x, getPixelPosition(p).y, PIECE_SIZE, PIECE_SIZE), hvlTexture(type.blackTexture));
			}else {
				hvlDraw(hvlQuadc(getPixelPosition(p).x, getPixelPosition(p).y, PIECE_SIZE, PIECE_SIZE), hvlTexture(type.whiteTexture));
			}
		}
	}

	public void translateToNewLocation(int xArg, int yArg, ClientPlayer player, ClientGame g) {
		inMotion = true;
		horizontalTravel = (Util.convertToPixelPositionX(xPos, player) - Util.convertToPixelPositionX(xArg, player));
		verticalTravel = (Util.convertToPixelPositionX(yPos, player) - Util.convertToPixelPositionX(yArg, player));
		translationMemoryX = xPos;
		translationMemoryY = yPos;
		xPos = xArg;
		yPos = yArg;

		if(horizontalTravel >= 0) {
			positiveHorizontalTravel = true;
		}else {
			positiveHorizontalTravel = false;
		}
		if(verticalTravel >= 0) {
			positiveVerticalTravel = true;
		}else {
			positiveVerticalTravel = false;
		}	

		if(player.color != g.player.color) {
			horizontalTravel = -horizontalTravel;
			verticalTravel = -verticalTravel;
			positiveVerticalTravel = !positiveVerticalTravel;
			positiveHorizontalTravel = !positiveHorizontalTravel;
		}

	}

	public void drawTranslation(ClientPlayer p, float delta, ClientGame g) {

		if(p.color == g.player.color) {
			if(color == PieceColor.BLACK) {
				hvlDraw(hvlQuadc((getPixelPosition(p).x + horizontalTravel), (getPixelPosition(p).y + verticalTravel), PIECE_SIZE, PIECE_SIZE), hvlTexture(type.blackTexture));
			}else {
				hvlDraw(hvlQuadc(getPixelPosition(p).x + horizontalTravel, getPixelPosition(p).y + verticalTravel, PIECE_SIZE, PIECE_SIZE), hvlTexture(type.whiteTexture));
			}
		}else {
			if(color == PieceColor.BLACK) {
				hvlDraw(hvlQuadc((getPixelPosition(p).x - horizontalTravel), (getPixelPosition(p).y - verticalTravel), PIECE_SIZE, PIECE_SIZE), hvlTexture(type.blackTexture));
			}else {
				hvlDraw(hvlQuadc(getPixelPosition(p).x - horizontalTravel, getPixelPosition(p).y - verticalTravel, PIECE_SIZE, PIECE_SIZE), hvlTexture(type.whiteTexture));
			}
		}
		if(!(type == ClientPiece.PieceType.KNIGHT)) {
			if(positiveHorizontalTravel) {
				if(horizontalTravel > 0) {
					horizontalTravel -= delta*TRANSLATION_SPEED;
				}else {
					horizontalTravel = 0;
				}
			}else {
				if(horizontalTravel < 0) {
					horizontalTravel += delta*TRANSLATION_SPEED;
				}else {
					horizontalTravel = 0;
				}			
			}
			if(positiveVerticalTravel) {
				if(verticalTravel > 0) {
					verticalTravel -= delta*TRANSLATION_SPEED;
				}else {
					verticalTravel = 0;
				}
			}else {
				if(verticalTravel < 0) {
					verticalTravel += delta*TRANSLATION_SPEED;
				}else {
					verticalTravel = 0;
				}			
			}
			if(horizontalTravel == 0 && verticalTravel == 0) {
				inMotion = false;
			}
		}else {
			if(positiveHorizontalTravel) {
				if(horizontalTravel > 0) {
					horizontalTravel -= delta*TRANSLATION_SPEED;
				}else {
					horizontalTravel = 0;
				}
			}else {
				if(horizontalTravel < 0) {
					horizontalTravel += delta*TRANSLATION_SPEED;
				}else {
					horizontalTravel = 0;
				}			
			}

			if(horizontalTravel == 0) {
				if(positiveVerticalTravel) {
					if(verticalTravel > 0) {
						verticalTravel -= delta*TRANSLATION_SPEED;
					}else {
						verticalTravel = 0;
					}
				}else {
					if(verticalTravel < 0) {
						verticalTravel += delta*TRANSLATION_SPEED;
					}else {
						verticalTravel = 0;
					}			
				}
				if(verticalTravel == 0) {
					inMotion = false;
				}
			}
		}
	}
}

