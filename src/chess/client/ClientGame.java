package chess.client;

import static com.osreboot.ridhvl2.HvlStatics.hvlColor;
import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlLine;
import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;
import static com.osreboot.ridhvl2.HvlStatics.hvlCirclec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.osreboot.ridhvl2.HvlConfig;
import com.osreboot.ridhvl2.HvlMath;
import com.osreboot.ridhvl2.painter.HvlCircle;
import com.samuel.Network;

import chess.client.ClientPiece.PieceColor;
import chess.client.ClientPiece.PieceType;
import chess.client.ClientPlayer.PlayerColor;
import chess.client.menu.ClientMenuMain;
import chess.client.menu.ClientMenuManager;
import chess.client.menu.ClientMenuPostgame;
import chess.common.Util;

public class ClientGame {

	boolean autoTrain = false;

	//Always draw the board from Player 1's perspective.
	//If a human player is present, they will always be Player 1.
	//Create two separate game cases - AIvAI and PvAI

	public static final int GAME_END_STATE_CONTINUE = 0;
	public static final int GAME_END_STATE_CHECKMATE = 1;
	public static final int GAME_END_STATE_STALEMATE = 2;

	//The minimum length of time, in seconds, the AI waits before making a move.
	public static final float CPU_MINIMUM_WAIT_TIME = 0f;

	public enum GameState{
		menu,
		playingHuman,
		training,
		postgame,
	}

	public GameState state = GameState.menu;
	public int gameEndState = GAME_END_STATE_CONTINUE;

	//Used to track when the human player is in check.
	public boolean inCheck = false;
	//Used to track when it is Player 1's turn to move.
	public boolean player1Turn = false;
	//Used to track which player made the last move.
	public PlayerColor finalMove;
	public ClientBoard board;
	public ClientPlayer player1;
	public ClientPlayer player2;
	//Used to initialize and reset the game board.
	public boolean boardInitialized = false;
	//Count the number of moves in the game. Increments only when white moves.
	public int moveCount = 0;
	//Counts the number of consecutive moves  with no capture or pawn moves.
	//Increments only when Black moves.
	//If this reaches 50, the game draws.
	public int drawCount = 0;
	public boolean incrementDrawCount = true;
	//The length of time taken by the CPU to make a move.
	public float moveTimer = 0f;
	//Used for switching between training and playing mode.
	public static boolean training;

	public int whiteWinCount = 0;
	public int blackWinCount = 0;
	public int stalemateCount = 0;

	public int totalWhiteWinCount = 0;
	public int totalBlackWinCount = 0;
	public int totalStalemateCount = 0;

	//Used to track which game is being currently played this training generation.
	//Caps at GeneticsHandler.GAMES_PER_GENERATION
	public int gameThisGen = 1;

	public String id;		
	public boolean debug = false;
	public ArrayList<ClientMove> validMoves = new ArrayList<ClientMove>();
	public int selectedPiecexPos = -1;
	public int selectedPieceyPos = -1;	
	public boolean promotionUI = false;
	public int promotionX = -1;
	public int promotionY = -1;

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
		//System.out.println("Reset!");
		validMoves = new ArrayList<ClientMove>();
		selectedPiecexPos = -1;
		selectedPieceyPos = -1;
		drawCount = 0;
		incrementDrawCount = true;
		//state = GameState.menu;
		inCheck = false;
		gameEndState = GAME_END_STATE_CONTINUE;
		finalMove = null;
		moveCount = 0;
		promotionUI = false;
		boardInitialized = false;
		//GeneticsHandler.init();

	}

	public void update(float delta){

		///?Champion Saving????
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			Collections.sort(GeneticsHandler.population, GeneticsHandler.compareByScore);
			HvlConfig.PJSON.save(GeneticsHandler.population.get(0).decisionNet, "championNetwork.json");
		}
		//////////////////////

		if(state == GameState.menu) {
			ClientMenuManager.manageMenus(this);
			if(boardInitialized) boardInitialized = false;
		}
		else if(state == GameState.playingHuman || (state == GameState.postgame && training == false)) {
			if(state == GameState.postgame){
				ClientMenuManager.manageMenus(this);		
			}
			if(!boardInitialized) {
				if(ClientMenuMain.color == ClientMenuMain.ColorSelection.RANDOM) {
					if(HvlMath.randomInt(0, 1) == 0) {
						player1.color = PlayerColor.WHITE;
						player2.color = PlayerColor.BLACK;
						player1Turn = true;
					}else {
						player1.color = PlayerColor.BLACK;
						player2.color = PlayerColor.WHITE;
						player1Turn = false;
					}
				}else if(ClientMenuMain.color == ClientMenuMain.ColorSelection.WHITE) {
					player1.color = PlayerColor.WHITE;
					player2.color = PlayerColor.BLACK;
					//player2.rng = new Random("poggers".hashCode());
					player1Turn = true;
				}else if(ClientMenuMain.color == ClientMenuMain.ColorSelection.BLACK) {
					player1.color = PlayerColor.BLACK;
					player2.color = PlayerColor.WHITE;
					player1Turn = false;
				}
				board = new ClientBoard(player1);
				board.initialize(player1);
				boardInitialized = true;
				GeneticsHandler.init(this);
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
				incrementDrawCount = true;
				if(inCheck) {
					hvlFont(0).drawc("You are in check", Display.getWidth()/2+450, Display.getHeight()/2, 1.2f);
				}else {
					if(ClientPieceLogic.getCheckState(board, player2)) {
						hvlFont(0).drawc("Opponent is in check", Display.getWidth()/2+450, Display.getHeight()/2, 1.2f);
					}
				}
				//Human player controls
				if(player1Turn) {
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
											if(p.yPos == 3 && p.type==PieceType.PAWN) {
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
												state = GameState.postgame;
												ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
											}else {
												System.out.println("STALEMATE!");
												gameEndState = GAME_END_STATE_STALEMATE;
												state = GameState.postgame;
												ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
											}
										}


										if(!p.moved) p.moved = true;
										validMoves.clear();
										escape = true;
										if(!promotionUI) player1Turn = false;
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
									state = GameState.postgame;
									ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
								}else {
									System.out.println("STALEMATE!");
									gameEndState = GAME_END_STATE_STALEMATE;
									state = GameState.postgame;
									ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
								}
							}
							promotionUI = false;
							player1Turn = false;
						}
						if(Util.getCursorX() <= Display.getWidth()/2 + 425+55+48 && Util.getCursorX() >= Display.getWidth()/2 + 425+55-48
								&& Util.getCursorY() <= Display.getHeight()/2-55+48 && Util.getCursorY() >= Display.getHeight()/2-55-48
								&& Util.leftMouseClick()) {
							board.getPieceAt(promotionX, promotionY).type = PieceType.KNIGHT;
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
									state = GameState.postgame;
									ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
								}else {
									System.out.println("STALEMATE!");
									gameEndState = GAME_END_STATE_STALEMATE;
									state = GameState.postgame;
									ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
								}
							}
							promotionUI = false;
							player1Turn = false;
						}
						if(Util.getCursorX() <= Display.getWidth()/2 + 425-55+48 && Util.getCursorX() >= Display.getWidth()/2 + 425-55-48
								&& Util.getCursorY() <= Display.getHeight()/2+55+48 && Util.getCursorY() >= Display.getHeight()/2+55-48
								&& Util.leftMouseClick()) {
							board.getPieceAt(promotionX, promotionY).type = PieceType.ROOK;
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
									state = GameState.postgame;
									ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
								}else {
									System.out.println("STALEMATE!");
									gameEndState = GAME_END_STATE_STALEMATE;
									state = GameState.postgame;
									ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
								}
							}
							promotionUI = false;
							player1Turn = false;
						}
						if(Util.getCursorX() <= Display.getWidth()/2 + 425-55+48 && Util.getCursorX() >= Display.getWidth()/2 + 425-55-48
								&& Util.getCursorY() <= Display.getHeight()/2-55+48 && Util.getCursorY() >= Display.getHeight()/2-55-48
								&& Util.leftMouseClick()) {
							board.getPieceAt(promotionX, promotionY).type = PieceType.BISHOP;
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
									state = GameState.postgame;
									ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
								}else {
									System.out.println("STALEMATE!");
									gameEndState = GAME_END_STATE_STALEMATE;
									state = GameState.postgame;
									ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
								}
							}
							promotionUI = false;
							player1Turn = false;
						}
					}
				}else {
					moveTimer += delta;
					//System.out.println(moveTimer);
					if(moveTimer >= CPU_MINIMUM_WAIT_TIME) {
						moveTimer = 0f;
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

						AiMove move = null;

						if(ClientMenuPostgame.championNetwork!=null) {
							player2.decisionNet = Network.deepCopy(ClientMenuPostgame.championNetwork);
							System.out.println("Champion network generating move");
							move = player2.readOutputLayer(board);
						}else {
							move = player2.generateRandomMove(board, player2);
						}
						if(move == null) {
							System.out.println("Something has gone wrong.");
						}
						if(move.piece.type == PieceType.PAWN) {
							drawCount = 0;
						}

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
									drawCount = 0;
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
							if(move.piece.yPos == 3 && move.piece.type==PieceType.PAWN) {							
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
								state = GameState.postgame;
								ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
							}
						}else {
							if(possibleMoves == 0) {
								System.out.println("STALEMATE!");
								gameEndState = GAME_END_STATE_STALEMATE;
								state = GameState.postgame;
								ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
							}
						}
						player1Turn = true;
					}
				}
			}else {
				if(gameEndState == GAME_END_STATE_CHECKMATE) {
					if(finalMove == player1.color) {
						hvlFont(0).drawc("Checkmate by " + finalMove.toString().toLowerCase() + " in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);	
					}else {
						hvlFont(0).drawc("Checkmate by " + finalMove.toString().toLowerCase() + " in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);	

					}
				}else {
					hvlFont(0).drawc("Stalemate in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
				}
			}



		}else if(state == GameState.training || (state == GameState.postgame && training == true)) {
			if(state == GameState.postgame){



				//LOOP FOR NUMBER OF GAMES PER GENERATION
				GeneticsHandler.population.get(gameThisGen-1).fitness = GeneticsHandler.calcFitness(player1, this);
				if(gameThisGen < GeneticsHandler.GAMES_PER_GENERATION) {
					//Switch to next player in population and reset
					gameThisGen++;
					validMoves = new ArrayList<ClientMove>();
					selectedPiecexPos = -1;
					selectedPieceyPos = -1;
					drawCount = 0;
					incrementDrawCount = true;
					//state = GameState.menu;
					inCheck = false;
					gameEndState = GAME_END_STATE_CONTINUE;
					finalMove = null;
					moveCount = 0;
					promotionUI = false;
					//System.out.println("Re-Initializing Board!");
					//if(game.player1.color == ClientPlayer.PlayerColor.WHITE) {
					//	game.player1.decisionNet = new Network(256,128,256);
					//}
					player1.clone(GeneticsHandler.population.get(gameThisGen-1));
					player1.color = PlayerColor.WHITE;
					player2.color = PlayerColor.BLACK;
					player1Turn = true;

					player2.rng = new Random("poggers".hashCode());
					board = new ClientBoard(player1);
					board.initialize(player1);
					boardInitialized = true;
					state = GameState.training;
				}else {


					//Manage menu AFTER generation has completed
					//During this phase, also operate all genetic algorithms with a completed population list.
					//Where does decisionNet information get updated?
					ClientMenuManager.manageMenus(this);
					/*if(gameEndState == GAME_END_STATE_STALEMATE) {
						hvlFont(0).drawc("Stalemate in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);	
					}else if( gameEndState == GAME_END_STATE_CHECKMATE) {
						hvlFont(0).drawc("Checkmate by " + finalMove.toString() + " in " + moveCount + " moves.", Display.getWidth()/2, Display.getHeight()-20, 1.2f);	
					}*/

					hvlFont(0).drawc("GENERATION " + GeneticsHandler.currentGeneration, Display.getWidth()/2, Display.getHeight()-133, 1.2f);
					hvlFont(0).drawc("White won " + whiteWinCount + " games this generation. ( " + ((float)whiteWinCount/(float)GeneticsHandler.GAMES_PER_GENERATION)*100 + " percent )  [ " + totalWhiteWinCount + " total wins ]" , Display.getWidth()/2, Display.getHeight()-100, 1.2f);
					hvlFont(0).drawc("Black won " + blackWinCount + " games this generation. ( " + ((float)blackWinCount/(float)GeneticsHandler.GAMES_PER_GENERATION)*100 + " percent )  [ " + totalBlackWinCount + " total wins ]", Display.getWidth()/2, Display.getHeight()-66, 1.2f);
					hvlFont(0).drawc(stalemateCount + " games ended in stalemate this generation. ( " + ((float)stalemateCount/(float)GeneticsHandler.GAMES_PER_GENERATION)*100 + " percent )  [ " + totalStalemateCount + " total stalemates ]", Display.getWidth()/2, Display.getHeight()-33, 1.2f);
					//float total = 0;

					//	for(ClientPlayer c : GeneticsHandler.population) {
					//		total += c.getFitness();
					//	}
					//System.out.println("AVERAGE FITNESS: " + total/GeneticsHandler.GAMES_PER_GENERATION);



				}
			}
			if(!boardInitialized) {
				GeneticsHandler.init(this);
				for(ClientPlayer c : GeneticsHandler.population) {
					c.decisionNet = new Network(256, 64, 64 ,256);
				}

				System.out.println("Initializing Board!");
				player1.clone(GeneticsHandler.population.get(gameThisGen-1));
				player1.color = PlayerColor.WHITE;
				player2.color = PlayerColor.BLACK;
				player2.rng = new Random("poggers".hashCode());
				player1Turn = true;

				board = new ClientBoard(player1);
				board.initialize(player1);
				boardInitialized = true;
			}
			//System.out.println(GeneticsHandler.population.size());
			board.update(delta, player1);
			for(ClientPiece p : board.activePieces) {
				if(p.inMotion) {							
					p.drawTranslation(player1, delta, this);							
				}
			}

			if(gameEndState == GAME_END_STATE_CONTINUE) {
				incrementDrawCount = true;
				if(ClientPieceLogic.getCheckState(board, player1)) {
					hvlFont(0).drawc("WHITE is in check", Display.getWidth()/2+450, Display.getHeight()/2, 1.2f);
				}else {
					if(ClientPieceLogic.getCheckState(board, player2)) {
						hvlFont(0).drawc("BLACK is in check", Display.getWidth()/2+450, Display.getHeight()/2, 1.2f);
					}			
				}
				if(board.activePieces.size() == 2) {
					//System.out.println("STALEMATE!");
					gameEndState = GAME_END_STATE_STALEMATE;
					stalemateCount++;
					totalStalemateCount++;
					state = GameState.postgame;
					ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
				}
				if(player1Turn) {
					moveTimer += delta;
					if(moveTimer >= CPU_MINIMUM_WAIT_TIME) {
						moveTimer = 0f;
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

						//Generate the move...
						player1.updateNetwork(this);
						AiMove move;
						player1.incomingMoveToExecute = player1.readOutputLayer(board);

						if(player1.incomingMoveToExecute != null) {
							move = player1.incomingMoveToExecute;
							player1.incomingMoveToExecute = null;
						}else {
							System.out.println("Random move being generated for WHITE!!");
							move = player1.generateRandomMove(board, player1);
						}


						if(move == null) {
							System.out.println("Something has gone wrong.");
						}
						if(move.piece.type == PieceType.PAWN) {
							drawCount = 0;
							incrementDrawCount = false;
						}


						/*if(moveCount == 0) {
							System.out.println("First move by WHITE:");
							System.out.println(move.piece.type.toString() + " on (" + move.piece.xPos + ", " + move.piece.yPos + ") to (" + move.move.x + ", " + move.move.y + ")" );

						}

						if(moveCount == 1) {
							System.out.println("Second move by WHITE:");
							System.out.println(move.piece.type.toString() + " on (" + move.piece.xPos + ", " + move.piece.yPos + ") to (" + move.move.x + ", " + move.move.y + ")" );

						}*/


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
									drawCount = 0;
									incrementDrawCount = false;
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
						move.piece.translateToNewLocation(move.move.x, move.move.y, player1, this);	



						if(player1.color == PlayerColor.BLACK) {																						
							//If the move is a promotion, upgrade the pawn.
							//All AI promotions are queens
							if(move.piece.yPos == 3 && move.piece.type==PieceType.PAWN) {							
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

						if(player1.color == PlayerColor.WHITE)
							moveCount++;
						//Check if this move ended the game...
						int possibleMoves = 0;
						for(ClientPiece p : board.activePieces) {
							if((p.color == PieceColor.BLACK && player2.color == PlayerColor.BLACK) || 
									(p.color == PieceColor.WHITE && player2.color == PlayerColor.WHITE)) {
								possibleMoves = possibleMoves + p.getAllValidMoves(board, player2).size();
							}
						}
						if(ClientPieceLogic.getCheckState(board, player2)){
							if(possibleMoves == 0){
								//	System.out.println("CHECKMATE BY PLAYER 1!");
								finalMove = player1.color;
								gameEndState = GAME_END_STATE_CHECKMATE;
								whiteWinCount++;
								totalWhiteWinCount++;
								state = GameState.postgame;
								ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
							}
						}else {
							if(possibleMoves == 0) {
								//	System.out.println("STALEMATE!");
								gameEndState = GAME_END_STATE_STALEMATE;
								stalemateCount++;
								totalStalemateCount++;
								state = GameState.postgame;
								ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
							}
						}
						if(incrementDrawCount) {
							drawCount++;
						}
						if(drawCount >= 50) {
							//System.out.println("STALEMATE!");
							gameEndState = GAME_END_STATE_STALEMATE;
							stalemateCount++;
							totalStalemateCount++;
							state = GameState.postgame;
							ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
						}
						player1Turn = false;
					}
				}else {
					//Player 2 (Black)
					moveTimer += delta;
					if(moveTimer >= CPU_MINIMUM_WAIT_TIME) {
						moveTimer = 0f;
						//hvlFont(0).drawc("Waiting for opponent", Display.getWidth()/2, Display.getHeight()-20, 1.2f);
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
						AiMove move;

						if(player2.incomingMoveToExecute != null) {
							move = player2.incomingMoveToExecute;
							player2.incomingMoveToExecute = null;
						}else {
							move = player2.generateRandomMove(board, player2);
						}

						if(move == null) {
							System.out.println("Something has gone wrong.");
						}

						/*if(moveCount == 1) {
							System.out.println("First move by BLACK:");
							System.out.println(move.piece.type.toString() + " on (" + move.piece.xPos + ", " + move.piece.yPos + ") to (" + move.move.x + ", " + move.move.y + ")" );
							System.out.println("");

						}

						if(moveCount == 2) {
							System.out.println("Second move by BLACK:");
							System.out.println(move.piece.type.toString() + " on (" + move.piece.xPos + ", " + move.piece.yPos + ") to (" + move.move.x + ", " + move.move.y + ")" );
							System.out.println("");

						}*/

						if(move.piece.type == PieceType.PAWN) {
							drawCount = 0;
							incrementDrawCount = false;
						}
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
									drawCount = 0;
									incrementDrawCount = false;
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
							if(move.piece.yPos == 3 && move.piece.type==PieceType.PAWN) {							
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
						//Check if this move ended the game...
						int possibleMoves = 0;
						for(ClientPiece p : board.activePieces) {
							if((p.color == PieceColor.BLACK && player1.color == PlayerColor.BLACK) || 
									(p.color == PieceColor.WHITE && player1.color == PlayerColor.WHITE)) {
								possibleMoves = possibleMoves + p.getAllValidMoves(board, player1).size();
							}
						}
						if(ClientPieceLogic.getCheckState(board, player1)){
							if(possibleMoves == 0){
								//	System.out.println("CHECKMATE BY PLAYER 2!");
								finalMove = player2.color;
								gameEndState = GAME_END_STATE_CHECKMATE;
								blackWinCount++;
								totalBlackWinCount++;
								state = GameState.postgame;
								ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
							}
						}else {
							if(possibleMoves == 0) {
								//System.out.println("STALEMATE!");
								gameEndState = GAME_END_STATE_STALEMATE;
								stalemateCount++;
								totalStalemateCount++;
								state = GameState.postgame;
								ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
							}
						}
						if(incrementDrawCount) {
							drawCount++;
						}
						if(drawCount >= 50) {
							//	System.out.println("STALEMATE!");
							gameEndState = GAME_END_STATE_STALEMATE;
							stalemateCount++;
							totalStalemateCount++;
							state = GameState.postgame;
							ClientMenuManager.menu = ClientMenuManager.MenuState.postgame;
						}
						player1Turn = true;
					}
				}
			}
		}
	}
}
