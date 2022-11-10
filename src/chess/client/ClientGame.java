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

import chess.client.ClientPiece.PieceColor;
import chess.client.ClientPiece.PieceType;
import chess.client.ClientPlayer.PlayerColor;
import chess.client.menu.ClientMenuMain;
import chess.client.menu.ClientMenuManager;
import chess.common.Util;

public class ClientGame {

	//Always draw the board from Player 1's perspective.
	//If a human player is present, they will always be player 1.
	//Create two separate game cases - AIvAI and PvAI

	public static final int GAME_END_STATE_CONTINUE = 0;
	public static final int GAME_END_STATE_CHECKMATE = 1;
	public static final int GAME_END_STATE_STALEMATE = 2;

	public enum GameState{
		menu,
		playingHuman,
		training,
		postgame,
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
		if(ClientMenuMain.color == ClientMenuMain.ColorSelection.RANDOM) {
			if(HvlMath.randomInt(0, 1) == 0) {
				player1.color = PlayerColor.WHITE;
				player2.color = PlayerColor.BLACK;
				playersTurn = true;
			}else {
				player1.color = PlayerColor.BLACK;
				player2.color = PlayerColor.WHITE;
				playersTurn = false;
			}
		}else if(ClientMenuMain.color == ClientMenuMain.ColorSelection.WHITE) {
			player1.color = PlayerColor.WHITE;
			player2.color = PlayerColor.BLACK;
			playersTurn = true;			
		}else if(ClientMenuMain.color == ClientMenuMain.ColorSelection.BLACK) {
			player1.color = PlayerColor.BLACK;
			player2.color = PlayerColor.WHITE;
			playersTurn = false;			
		}

	}

	public void update(float delta){

		if(state == GameState.menu) {
			ClientMenuManager.manageMenus(this);
		}
		else if(state == GameState.playingHuman) {
			if(!boardInitialized) {
				if(ClientMenuMain.color == ClientMenuMain.ColorSelection.RANDOM) {
					if(HvlMath.randomInt(0, 1) == 0) {
						player1.color = PlayerColor.WHITE;
						player2.color = PlayerColor.BLACK;
						playersTurn = true;
					}else {
						player1.color = PlayerColor.BLACK;
						player2.color = PlayerColor.WHITE;
						playersTurn = false;
					}
				}else if(ClientMenuMain.color == ClientMenuMain.ColorSelection.WHITE) {
					player1.color = PlayerColor.WHITE;
					player2.color = PlayerColor.BLACK;
					playersTurn = true;			
				}else if(ClientMenuMain.color == ClientMenuMain.ColorSelection.BLACK) {
					player1.color = PlayerColor.BLACK;
					player2.color = PlayerColor.WHITE;
					playersTurn = false;			
				}
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
					hvlFont(0).drawc("You are in check", Display.getWidth()/2+450, Display.getHeight()/2, 1.2f);
				}else {
					if(ClientPieceLogic.getCheckState(board, player2)) {
						hvlFont(0).drawc("Opponent is in check", Display.getWidth()/2+450, Display.getHeight()/2, 1.2f);
					}
				}
				//Human player controls
				if(playersTurn) {
					hvlFont(0).drawc("It is your turn", Display.getWidth()/2, Display.getHeight()-20, 1.2f);

					//Unmark any pieces as en passant vulnerable at the start of the turn.
					for(ClientPiece p : board.activePieces) {
						if((p.color == PieceColor.BLACK && player1.color == PlayerColor.BLACK) ||
								(p.color == PieceColor.WHITE && player1.color == PlayerColor.WHITE)) {
							if(p.type == PieceType.PAWN) {
								if(p.enPassantVulnerable) {
									p.enPassantVulnerable = false;
								}
							}
						}
					}

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

										//If the intended move moves a pawn two spaces, mark that pawn as en passant vulnerable.
										if(p.type == PieceType.PAWN) {
											if((p.yPos == 1 && m.y == 3) || (p.yPos == 6 && m.y == 4)) {
												p.enPassantVulnerable = true;
											}
										}

										//If the move is an en passant capture, remove the appropriate pawn.
										if(p.type == PieceType.PAWN) {
											if(p.xPos != m.x && board.isSpaceFree(m.x, m.y)) {
												if(p.color == PieceColor.BLACK) {
													if(board.getPieceAt(m.x, m.y-1).enPassantVulnerable) {
														board.claimedPieces.add(board.getPieceAt(m.x, m.y-1));
														board.activePieces.remove(board.getPieceAt(m.x, m.y-1));
													}
												}else if(p.color == PieceColor.WHITE) {
													if(board.getPieceAt(m.x, m.y+1).enPassantVulnerable) {
														board.claimedPieces.add(board.getPieceAt(m.x, m.y+1));
														board.activePieces.remove(board.getPieceAt(m.x, m.y+1));
													}
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

											//If the move is a castle, detect and move the appropriate rook
											if(m.castle) {
												if(m.x == 6 && m.y == 7) {
													board.getPieceAt(7, 7).xPos = 5;
												}else if(m.x == 2 && m.y == 7) {
													board.getPieceAt(0, 7).xPos = 3;
												}
											}
										}

										//Check for game-ending states
										int possibleMoves = 0;
										for(ClientPiece a : board.activePieces) {
											if((a.color == PieceColor.BLACK && player2.color == PlayerColor.BLACK) || 
													(a.color == PieceColor.WHITE && player2.color == PlayerColor.WHITE)) {
												possibleMoves = possibleMoves + a.getAllValidMoves(board, player2).size();
											}
										}
										if(possibleMoves == 0){
											if(ClientPieceLogic.getCheckState(board, player2)) {
												System.out.println("CHECKMATE BY HUMAN!");
												finalMove = player1.color;
												gameEndState = GAME_END_STATE_CHECKMATE;
											}else {
												System.out.println("STALEMATE!");
												gameEndState = GAME_END_STATE_STALEMATE;
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
					//Unmark any pieces as en passant vulnerable at the start of the turn.
					for(ClientPiece p : board.activePieces) {
						if((p.color == PieceColor.BLACK && player2.color == PlayerColor.BLACK) ||
								(p.color == PieceColor.WHITE && player2.color == PlayerColor.WHITE)) {
							if(p.type == PieceType.PAWN) {
								if(p.enPassantVulnerable) {
									p.enPassantVulnerable = false;
								}
							}
						}
					}

					//Generate the move...
					AiMove move = player2.generateRandomMove(board, player2);

					//If the move is an en passant capture, remove the appropriate pawn.
					if(move.piece.type == PieceType.PAWN) {
						if(move.piece.xPos != move.move.x && board.isSpaceFree(move.move.x, move.move.y)) {
							if(move.piece.color == PieceColor.BLACK) {
								if(board.getPieceAt(move.move.x, move.move.y-1).enPassantVulnerable) {
									board.claimedPieces.add(board.getPieceAt(move.move.x, move.move.y-1));
									board.activePieces.remove(board.getPieceAt(move.move.x, move.move.y-1));
								}
							}else if(move.piece.color == PieceColor.WHITE) {
								if(board.getPieceAt(move.move.x, move.move.y+1).enPassantVulnerable) {
									board.claimedPieces.add(board.getPieceAt(move.move.x, move.move.y+1));
									board.activePieces.remove(board.getPieceAt(move.move.x, move.move.y+1));
								}
							}

						}
					}

					//Claim any piece existing on the move square
					if(!board.isSpaceFree(move.move.x, move.move.y)) {
						for(int i = 0; i < board.activePieces.size(); i++) {
							if(board.activePieces.get(i).xPos == move.move.x && board.activePieces.get(i).yPos == move.move.y) {
								board.claimedPieces.add(board.activePieces.get(i));
								board.activePieces.remove(i);
								break;
							}
						}														
					}
					//If the intended move moves a pawn two spaces, mark that pawn as en passant vulnerable.
					if(move.piece.type == PieceType.PAWN) {
						if((move.piece.yPos == 1 && move.move.y == 3) || (move.piece.yPos == 6 && move.move.y == 4)) {
							move.piece.enPassantVulnerable = true;
						}
					}
					move.piece.translateToNewLocation(move.move.x, move.move.y, player2, this);	



					if(player2.color == PlayerColor.BLACK) {																						
						//If the move is a promotion, upgrade the pawn.
						//All AI promotions are queens
						if(move.piece.yPos == 7 && move.piece.type==PieceType.PAWN) {							
							board.getPieceAt(move.piece.xPos, move.piece.yPos).type = PieceType.QUEEN;							
						}

						//If the move is a castle, detect and move the appropriate rook
						if(move.move.castle) {
							if(move.move.x == 6 && move.move.y == 0) {
								board.getPieceAt(7, 0).xPos = 5;
							}else if(move.move.x == 2 && move.move.y == 0) {
								board.getPieceAt(0, 0).xPos = 3;
							}
						}
					}else {
						if(move.piece.yPos == 0 && move.piece.type==PieceType.PAWN) {
							board.getPieceAt(move.piece.xPos, move.piece.yPos).type = PieceType.QUEEN;
						}

						//If the move is a castle, detect and move the appropriate rook
						if(move.move.castle) {
							if(move.move.x == 6 && move.move.y == 7) {
								board.getPieceAt(7, 7).xPos = 5;
							}else if(move.move.x == 2 && move.move.y == 7) {
								board.getPieceAt(0, 7).xPos = 3;
							}
						}
					}										

					if(player2.color == PlayerColor.WHITE)
						moveCount++;
					int possibleMoves = 0;
					for(ClientPiece p : board.activePieces) {
						if((p.color == PieceColor.BLACK && player1.color == PlayerColor.BLACK) || 
								(p.color == PieceColor.WHITE && player1.color == PlayerColor.WHITE)) {
							possibleMoves = possibleMoves + p.getAllValidMoves(board, player1).size();
						}
					}
					if(ClientPieceLogic.getCheckState(board, player1)){
						inCheck = true;
						if(possibleMoves == 0){
							System.out.println("CHECKMATE BY AI!");
							finalMove = player2.color;
							gameEndState = GAME_END_STATE_CHECKMATE;
						}
					}else {
						if(possibleMoves == 0) {
							System.out.println("STALEMATE!");
							gameEndState = GAME_END_STATE_STALEMATE;
						}
					}
					playersTurn = true;
				}
			}else {
				if(gameEndState == GAME_END_STATE_CHECKMATE) {
					if(finalMove == player1.color) {
						hvlFont(0).drawc("Victory! Checkmate by " + finalMove.toString().toLowerCase() + " in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);	
					}else {
						hvlFont(0).drawc("Defeat! Checkmate by " + finalMove.toString().toLowerCase() + " in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);	

					}
				}else {
					hvlFont(0).drawc("Stalemate in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
				}
			}


		}
	}

}
