 package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlColor;
import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlLine;
import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;
import static com.osreboot.ridhvl2.HvlStatics.hvlCirclec;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl2.HvlMath;
import com.osreboot.ridhvl2.painter.HvlCircle;

import chess.client.ClientPiece.PieceType;
import chess.client.ClientPlayer.PlayerColor;
import chess.client.menu.ClientMenuManager;
import chess.common.Util;

public class ClientGame {
	
	//Always draw the board from Player 1's perspective.
	//If a human player is present, they will always be player 1.
	//Need to start over, create two separate game cases - AIvAI and PvAI
	
	//Check after each move is made for a checkmate or stalemate condition and end the game.
	//Allow AI to promote pawns
	//Verify Castling and En Passant work correctly for AI

	public static final int GAME_END_STATE_CONTINUE = 0;
	public static final int GAME_END_STATE_CHECKMATE = 1;
	public static final int GAME_END_STATE_STALEMATE = 2;

	public enum GameState{
		menu,
		playing;
	}
	
	public enum GameMode{
		humanVAi,
		AivAi;
	}

	public GameState state = GameState.menu;
	public int gameEndState = GAME_END_STATE_CONTINUE;
	public boolean inCheck = false;
	public boolean playersTurn = false;
	public PlayerColor finalMove;
	public ClientBoard board;
	public ClientPlayer player1;
	public ClientPlayer player2;
	public boolean boardInitialized = false;
	public int moveCount = 0;

	private String id;		
	private boolean debug = false;
	private ArrayList<ClientMove> validMoves = new ArrayList<ClientMove>();
	private int selectedPiecexPos = -1;
	private int selectedPieceyPos = -1;	
	private boolean promotionUI = false;
	private int promotionX = -1;
	private int promotionY = -1;

	public ClientGame(String id) {
		this.id = id;
		player1 = new ClientPlayer(id, true);
		player2 = new ClientPlayer(id, true);
		if(HvlMath.randomInt(0, 1) == 0) {
			player1.color = PlayerColor.WHITE;
			player2.color = PlayerColor.BLACK;
			playersTurn = true;
		}else {
			player1.color = PlayerColor.BLACK;
			player2.color = PlayerColor.WHITE;
			playersTurn = false;
		}

		if(debug) {
			
		}
	}

	public void drawValidMoves() {
		for(ClientMove m : validMoves) {
			//hvlDraw(hvlQuadc(Util.convertToPixelPositionX(m.x, player), Util.convertToPixelPositionY(m.y, player), 10, 10), hvlColor(0f, 1f, 0f));
			if(board.isSpaceFree(m.x,m.y)) {
				hvlDraw(hvlCirclec(Util.convertToPixelPositionX(m.x, player1), Util.convertToPixelPositionY(m.y, player1),10), hvlColor(.1f, .1f, .1f, .6f));
			}else {
				hvlDraw(hvlCirclec(Util.convertToPixelPositionX(m.x, player1), Util.convertToPixelPositionY(m.y, player1),10), hvlColor(.1f, .1f, .1f, .6f));
			}

		}
	}

	public void reset() {
		validMoves = new ArrayList<ClientMove>();
		selectedPiecexPos = -1;
		selectedPieceyPos = -1;
		boardInitialized = false;
		//normalInput = true;
		playersTurn = true;
		state = GameState.menu;
		inCheck = false;
		gameEndState = GAME_END_STATE_CONTINUE;
		finalMove = null;
		moveCount = 0;
		promotionUI = false;
		if(HvlMath.randomInt(0, 1) == 0) {
			player1.color = PlayerColor.WHITE;
			player2.color = PlayerColor.BLACK;
			playersTurn = true;
		}else {
			player1.color = PlayerColor.BLACK;
			player2.color = PlayerColor.WHITE;
			playersTurn = false;
		}

	}

	public void update(float delta){

		if(state == GameState.menu) {
			ClientMenuManager.manageMenus(this);
		}
		else if(state == GameState.playing) {
				if(!boardInitialized) {
					board = new ClientBoard(player1);
					board.initialize(player1);
					boardInitialized = true;
				}
					board.update(delta, player1);
					drawValidMoves();
					for(ClientPiece p : board.activePieces) {
						if(p.inMotion) {							
							p.drawTranslation(player1, delta, this);							
						}
					}

					//Detect if a piece is clicked, highlight that piece's valid moves (if existing)
					if(gameEndState == GAME_END_STATE_CONTINUE) {
						if(inCheck) {
							hvlFont(0).drawc("Player 1 is in check", Display.getWidth()/2+450, Display.getHeight()/2, 1.2f);
						}else {
							if(ClientPieceLogic.getCheckState(board, player2)) {
								hvlFont(0).drawc("Player 2 is in check", Display.getWidth()/2+450, Display.getHeight()/2, 1.2f);
							}
						}
						//Human player controls
						if(playersTurn) {
							hvlFont(0).drawc("It is your turn", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
							if(!promotionUI) {
								for(ClientPiece p : board.activePieces) {
									if((Util.getCursorX() >= p.getPixelPosition(player1).x - ClientPiece.PIECE_SIZE/2
											&& Util.getCursorX() <= p.getPixelPosition(player1).x + ClientPiece.PIECE_SIZE/2
											&& Util.getCursorY() >= p.getPixelPosition(player1).y - ClientPiece.PIECE_SIZE/2
											&& Util.getCursorY() <= p.getPixelPosition(player1).y + ClientPiece.PIECE_SIZE/2)
											&& Util.leftMouseClick() && p.color.toString().equals(player1.color.toString())) {
										System.out.println("You clicked a " + p.color + " " + p.type + " on space [ " + p.xPos + ", " + p.yPos + " ]");
										selectedPiecexPos = p.xPos;
										selectedPieceyPos = p.yPos;
										validMoves = p.getAllValidMoves(board, player1);
									}									
								}

								//If a valid move is attempted as defined in ClientPieceLogic, execute the move.
								for(ClientMove m : validMoves) {
									boolean escape = false;
									if((Util.getCursorX() >= Util.convertToPixelPositionX(m.x, player1) - ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorX() <= Util.convertToPixelPositionX(m.x, player1) + ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorY() >= Util.convertToPixelPositionY(m.y, player1) - ClientBoardSpace.SPACE_SIZE/2
											&& Util.getCursorY() <= Util.convertToPixelPositionY(m.y, player1) + ClientBoardSpace.SPACE_SIZE/2)
											&& Util.leftMouseClick()) {

										for(ClientPiece p : board.activePieces) {
											if(p.xPos == selectedPiecexPos && p.yPos == selectedPieceyPos) {					
												//Claim any piece existing on the attempted move's square
												if(!board.isSpaceFree(m.x, m.y)) {
													for(int i = 0; i < board.activePieces.size(); i++) {
														if(board.activePieces.get(i).xPos == m.x && board.activePieces.get(i).yPos == m.y) {
															board.claimedPieces.add(board.activePieces.get(i));
															board.activePieces.remove(i);
															break;
														}
													}														
												}						
												//Move piece to new square
												//p.xPos = m.x;
												//p.yPos = m.y;
												p.translateToNewLocation(m.x, m.y, player1, this);
												//p.inMotion = true;
												if(inCheck) {
													inCheck = false;
												}



												if(player1.color == PlayerColor.BLACK) {												
													//If the move is a promotion, upgrade the pawn.
													if(p.yPos == 7 && p.type==PieceType.PAWN) {
														promotionUI = true;
														validMoves.clear();
														escape = true;
														promotionX = p.xPos;
														promotionY = p.yPos;
													}
													//If the move is en passant, detect and remove the appropriate pawn.
													if(m.enPassant) {
														for(int i = 0; i < board.activePieces.size(); i++) {
															if(board.activePieces.get(i).xPos == m.x && board.activePieces.get(i).yPos == m.y-1) {
																board.claimedPieces.add(board.activePieces.get(i));
																board.activePieces.remove(i);
																break;
															}
														}
													}
													//If the move is a castle, detect and move the appropriate rook
													if(m.castle) {
														if(m.x == 6 && m.y == 0) {
															board.getPieceAt(7, 0).xPos = 5;
														}else if(m.x == 2 && m.y == 0) {
															board.getPieceAt(0, 0).xPos = 3;
														}
													}
												}else {
													if(p.yPos == 0 && p.type==PieceType.PAWN) {
														promotionUI = true;
														validMoves.clear();
														escape = true;
														promotionX = p.xPos;
														promotionY = p.yPos;
													}
													if(m.enPassant) {
														for(int i = 0; i < board.activePieces.size(); i++) {
															if(board.activePieces.get(i).xPos == m.x && board.activePieces.get(i).yPos == m.y+1) {
																board.claimedPieces.add(board.activePieces.get(i));
																board.activePieces.remove(i);
																break;
															}
														}
													}
													//If the move is a castle, detect and move the appropriate rook
													if(m.castle) {
														if(m.x == 6 && m.y == 7) {
															board.getPieceAt(7, 7).xPos = 5;
														}else if(m.x == 2 && m.y == 7) {
															board.getPieceAt(0, 7).xPos = 3;
														}
													}
												}
												if(!p.moved) p.moved = true;
												validMoves.clear();
												escape = true;
												if(!promotionUI) playersTurn = false;
												if(player1.color == PlayerColor.WHITE)
													moveCount++;
											}					
											if(escape) {
												if(!promotionUI) {							
												}
												break;
											}
										}
									}

									if(escape) {
										if(!promotionUI) {
										}
										break;
									}
								}
							}else {
								//if promotion UI...
								ClientPromotionTypeUI.draw(player1);
								if(Util.getCursorX() <= Display.getWidth()/2 + 425+55+48 && Util.getCursorX() >= Display.getWidth()/2 + 425+55-48
										&& Util.getCursorY() <= Display.getHeight()/2+55+48 && Util.getCursorY() >= Display.getHeight()/2+55-48
										&& Util.leftMouseClick()) {
									board.getPieceAt(promotionX, promotionY).type = PieceType.QUEEN;
									promotionUI = false;
									playersTurn = false;
								}
								if(Util.getCursorX() <= Display.getWidth()/2 + 425+55+48 && Util.getCursorX() >= Display.getWidth()/2 + 425+55-48
										&& Util.getCursorY() <= Display.getHeight()/2-55+48 && Util.getCursorY() >= Display.getHeight()/2-55-48
										&& Util.leftMouseClick()) {
									board.getPieceAt(promotionX, promotionY).type = PieceType.KNIGHT;
									promotionUI = false;
									playersTurn = false;
								}
								if(Util.getCursorX() <= Display.getWidth()/2 + 425-55+48 && Util.getCursorX() >= Display.getWidth()/2 + 425-55-48
										&& Util.getCursorY() <= Display.getHeight()/2+55+48 && Util.getCursorY() >= Display.getHeight()/2+55-48
										&& Util.leftMouseClick()) {
									board.getPieceAt(promotionX, promotionY).type = PieceType.ROOK;
									promotionUI = false;
									playersTurn = false;
								}
								if(Util.getCursorX() <= Display.getWidth()/2 + 425-55+48 && Util.getCursorX() >= Display.getWidth()/2 + 425-55-48
										&& Util.getCursorY() <= Display.getHeight()/2-55+48 && Util.getCursorY() >= Display.getHeight()/2-55-48
										&& Util.leftMouseClick()) {
									board.getPieceAt(promotionX, promotionY).type = PieceType.BISHOP;
									promotionUI = false;
									playersTurn = false;
								}
							}
						}else {
							hvlFont(0).drawc("Waiting for opponent", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
							//Pick and execute random AI move
							//Change AI-Move to its own object, should contain the piece to move and the planned move space.
							//Will be much easier than a HashMap
							AiMove move = player2.generateMove(board, player2);
							if(!board.isSpaceFree(move.move.x, move.move.y)) {
								for(int i = 0; i < board.activePieces.size(); i++) {
									if(board.activePieces.get(i).xPos == move.move.x && board.activePieces.get(i).yPos == move.move.y) {
										board.claimedPieces.add(board.activePieces.get(i));
										board.activePieces.remove(i);
										break;
									}
								}														
							}
							move.piece.translateToNewLocation(move.move.x, move.move.y, player2, this);	
							if(ClientPieceLogic.getCheckState(board, player1)){
								inCheck = true;
							}
							playersTurn = true;
						}
						System.out.println("Number of pieces: " + board.activePieces.size());
					}else {
						if(gameEndState == GAME_END_STATE_CHECKMATE) {
							hvlFont(0).drawc("GG! Checkmate by " + finalMove.toString().toLowerCase() + " in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);					
						}else {
							hvlFont(0).drawc("Stalemate in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
						}
					}
				
			
		}
	}

}
