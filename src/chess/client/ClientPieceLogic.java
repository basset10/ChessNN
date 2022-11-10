package chess.client;

import java.util.ArrayList;

import chess.client.ClientPiece.PieceColor;
import chess.client.ClientPiece.PieceType;
import chess.client.ClientPlayer.PlayerColor;

public class ClientPieceLogic{

	//Used to determine if a potential move will put the user in self-check.
	public static boolean legalMoveCheck(int intendedX, int intendedY, ClientPiece selectedPiece, ClientBoard boardArg, ClientPlayer playerArg) {
		//System.out.println("Verifying legality of move " + selectedPiece.color + " " + selectedPiece.type + " to space " + intendedX + ", " + intendedY);
		ArrayList<ClientPiece> piecesCopy = new ArrayList<ClientPiece>();

		for(ClientPiece p : boardArg.activePieces) {
			piecesCopy.add(new ClientPiece(p.type, p.color, p.xPos, p.yPos));
		}
		ClientBoard sim = new ClientBoard(piecesCopy);
		for(int i = 0; i < sim.activePieces.size(); i++) {
			if(sim.activePieces.get(i).xPos == intendedX && sim.activePieces.get(i).yPos == intendedY) {
				sim.activePieces.remove(i);
				break;
			}
		}
		for(int i = 0; i < sim.activePieces.size(); i++) {
			if(sim.activePieces.get(i).xPos == selectedPiece.xPos && sim.activePieces.get(i).yPos == selectedPiece.yPos) {
				sim.activePieces.get(i).xPos = intendedX;
				sim.activePieces.get(i).yPos = intendedY;
				break;
			}
		}
		if(getCheckState(sim, playerArg)) {
			return false;
		}else {
			return true;
		}
	}

	public static int getGameEndState(ClientBoard board, ClientPlayer player, boolean checkStateArg) {
		if(checkStateArg) {
			if(player.color == PlayerColor.WHITE) {
				for(ClientPiece piece : board.activePieces) {
					if(piece.color == PieceColor.WHITE) {						
						ArrayList<ClientMove> moveHolder = piece.getAllValidMoves(board, player);
						if(moveHolder.size() > 0) {
							return ClientGame.GAME_END_STATE_CONTINUE;
						}
					}
				}
				return ClientGame.GAME_END_STATE_CHECKMATE;
			}
			else if(player.color == PlayerColor.BLACK) {
				for(ClientPiece piece : board.activePieces) {
					if(piece.color == PieceColor.BLACK) {
						ArrayList<ClientMove> moveHolder = piece.getAllValidMoves(board, player);
						if(moveHolder.size() > 0) {
							return ClientGame.GAME_END_STATE_CONTINUE;
						}
					}
				}
				return ClientGame.GAME_END_STATE_CHECKMATE;
			}else {
				return ClientGame.GAME_END_STATE_CONTINUE;
			}
		}else {
			if(player.color == PlayerColor.WHITE) {
				for(ClientPiece piece : board.activePieces) {
					if(piece.color == PieceColor.WHITE) {						
						ArrayList<ClientMove> moveHolder = piece.getAllValidMoves(board, player);
						if(moveHolder.size() > 0) {
							return ClientGame.GAME_END_STATE_CONTINUE;
						}
					}
				}
				return ClientGame.GAME_END_STATE_STALEMATE;
			}
			else if(player.color == PlayerColor.BLACK) {
				for(ClientPiece piece : board.activePieces) {
					if(piece.color == PieceColor.BLACK) {
						ArrayList<ClientMove> moveHolder = piece.getAllValidMoves(board, player);
						if(moveHolder.size() > 0) {
							return ClientGame.GAME_END_STATE_CONTINUE;
						}
					}
				}
				return ClientGame.GAME_END_STATE_STALEMATE;
			}else {
				return ClientGame.GAME_END_STATE_CONTINUE;
			}
		}
	}


	//Returns true if the given player is currently in check on the given board.
	public static boolean getCheckState(ClientBoard board, ClientPlayer player) {
		if(player.color == PlayerColor.WHITE) {
			for(ClientPiece piece : board.activePieces) {
				if(piece.color == PieceColor.BLACK) {
					//System.out.println("Checking " + piece.color + " " + piece.type + " for check...");
					for(ClientMove c : getAllValidMoves(piece, board, player, false)){				
						for(ClientPiece cp : board.activePieces) {
							if(cp.color == PieceColor.WHITE && cp.type == PieceType.KING && cp.xPos == c.x && cp.yPos == c.y) {
								//Check if this square can be safely captured...

								//System.out.println(piece.color + " " + piece.type + " has a valid move on " + c.x + ", " + c.y);
								//System.out.println("your King (white) located on " + c.x + ", " + c.y);
								//System.out.println("King is in check!");
								return true;								
							}
						}
					}
				}
			}
			//System.out.println("King is not in check");
		}else if(player.color == PlayerColor.BLACK) {
			for(ClientPiece piece : board.activePieces) {
				if(piece.color == PieceColor.WHITE) {
					//System.out.println("Checking " + piece.color + " " + piece.type + " for check...");
					for(ClientMove c : getAllValidMoves(piece, board, player, false)){						
						for(ClientPiece cp : board.activePieces) {
							if(cp.color == PieceColor.BLACK && cp.type == PieceType.KING && cp.xPos == c.x && cp.yPos == c.y) {
								//Check if this square can be safely captured...

								//System.out.println(piece.color + " " + piece.type + " has a valid move on " + c.x + ", " + c.y);
								//System.out.println("your King (black) located on " + c.x + ", " + c.y);
								//System.out.println("King is in check!");
								return true;
							}
						}
					}
				}
			}
			//System.out.println("King is not in check");
		}
		return false;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece
	 * @return an ArrayList of all valid move coordinates
	 */
	public static ArrayList<ClientMove> getAllValidMoves(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();
		if(pieceArg.type == PieceType.PAWN) {
			moves = pawnMoveCheck(pieceArg, boardArg, player, checkTest);
		}else if(pieceArg.type == PieceType.KNIGHT) {
			moves = knightMoveCheck(pieceArg, boardArg, player, checkTest);
		}else if(pieceArg.type == PieceType.ROOK) {
			moves = rookMoveCheck(pieceArg, boardArg, player, checkTest);
		}else if(pieceArg.type == PieceType.BISHOP) {
			moves = bishopMoveCheck(pieceArg, boardArg, player, checkTest);
		}else if(pieceArg.type == PieceType.QUEEN) {
			moves = queenMoveCheck(pieceArg, boardArg, player, checkTest);
		}else if(pieceArg.type == PieceType.KING) {
			moves = kingMoveCheck(pieceArg, boardArg, player, checkTest);
		}
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a king
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> kingMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();
		//Castling moves
		if(checkTest) {
			if(!pieceArg.moved) {
				if(!getCheckState(boardArg, player)) {
					//White right castle
					if(pieceArg.color == PieceColor.WHITE) {
						if(!boardArg.isSpaceFree(7, 7) && boardArg.isSpaceFree(6, 7) && boardArg.isSpaceFree(5, 7)) {
							if(boardArg.getPieceAt(7, 7).type == PieceType.ROOK && !boardArg.getPieceAt(7, 7).moved) {
								if(legalMoveCheck(6, 7, pieceArg, boardArg, player)
										&& legalMoveCheck(5, 7, pieceArg, boardArg, player)) {
									moves.add(new ClientMove(6, 7, true, false));
								}
							}
						}
						//White left castle
						if(!boardArg.isSpaceFree(0, 7) && boardArg.isSpaceFree(1, 7) && boardArg.isSpaceFree(2, 7) && boardArg.isSpaceFree(3, 7)) {
							if(boardArg.getPieceAt(0, 7).type == PieceType.ROOK && !boardArg.getPieceAt(0, 7).moved) {
								if(legalMoveCheck(3, 7, pieceArg, boardArg, player)
										&& legalMoveCheck(2, 7, pieceArg, boardArg, player)) {
									moves.add(new ClientMove(2, 7, true, false));
								}
							}
						}
					}else {
						//black left castle
						if(!boardArg.isSpaceFree(7, 0) && boardArg.isSpaceFree(5, 0) && boardArg.isSpaceFree(6, 0)) {
							if(boardArg.getPieceAt(7, 0).type == PieceType.ROOK && !boardArg.getPieceAt(7, 0).moved) {
								if(legalMoveCheck(5, 0, pieceArg, boardArg, player)
										&& legalMoveCheck(6, 0, pieceArg, boardArg, player)) {
									moves.add(new ClientMove(6, 0, true, false));
								}
							}
						}
						//black right castle
						if(!boardArg.isSpaceFree(0, 0) && boardArg.isSpaceFree(1, 0) && boardArg.isSpaceFree(2, 0) && boardArg.isSpaceFree(3, 0)) {
							if(boardArg.getPieceAt(0, 0).type == PieceType.ROOK && !boardArg.getPieceAt(0, 0).moved) {
								if(legalMoveCheck(3, 0, pieceArg, boardArg, player)
										&& legalMoveCheck(2, 0, pieceArg, boardArg, player)) {
									moves.add(new ClientMove(2, 0, true, false));
								}
							}
						}
					}
				}
			}
		}

		if(pieceArg.yPos >= 1) {
			singleSquareCheck(pieceArg.xPos, pieceArg.yPos-1,  moves, pieceArg, boardArg, player, checkTest);
		}
		if(pieceArg.yPos >= 1 & pieceArg.xPos <= 6) {
			singleSquareCheck(pieceArg.xPos+1, pieceArg.yPos-1,  moves, pieceArg, boardArg, player, checkTest);
		}
		if(pieceArg.xPos <= 6) {
			singleSquareCheck(pieceArg.xPos+1, pieceArg.yPos,  moves, pieceArg, boardArg, player, checkTest);
		}
		if(pieceArg.yPos <= 6 & pieceArg.xPos <= 6) {
			singleSquareCheck(pieceArg.xPos+1, pieceArg.yPos+1,  moves, pieceArg, boardArg, player, checkTest);
		}
		if(pieceArg.yPos <= 6) {
			singleSquareCheck(pieceArg.xPos, pieceArg.yPos+1,  moves, pieceArg, boardArg, player, checkTest);
		}
		if(pieceArg.yPos <= 6 & pieceArg.xPos >= 1) {
			singleSquareCheck(pieceArg.xPos-1, pieceArg.yPos+1,  moves, pieceArg, boardArg, player, checkTest);
		}
		if(pieceArg.xPos >= 1) {
			singleSquareCheck(pieceArg.xPos-1, pieceArg.yPos,  moves, pieceArg, boardArg, player, checkTest);
		}
		if(pieceArg.yPos >= 1 & pieceArg.xPos >= 1) {
			singleSquareCheck(pieceArg.xPos-1, pieceArg.yPos-1,  moves, pieceArg, boardArg, player, checkTest);
		}
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a queen
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> queenMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();
		diagonalMoveCheck(moves, pieceArg, boardArg, player, checkTest);
		straightMoveCheck(moves, pieceArg, boardArg, player, checkTest);
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a bishop
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> bishopMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();
		diagonalMoveCheck(moves, pieceArg, boardArg, player, checkTest);
		return moves;
	}


	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a rook
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> rookMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();		
		straightMoveCheck(moves, pieceArg, boardArg, player, checkTest);
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a knight
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> knightMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();	
		//Rightmost moves
		if(pieceArg.xPos <= 5) {
			//Upper
			if(pieceArg.yPos >= 1) {
				singleSquareCheck(pieceArg.xPos+2, pieceArg.yPos-1,  moves, pieceArg, boardArg, player, checkTest);
			}
			//Lower
			if(pieceArg.yPos <= 6) {
				singleSquareCheck(pieceArg.xPos+2, pieceArg.yPos+1,  moves, pieceArg, boardArg, player, checkTest);
			}
		}		
		//Mid-right moves
		if(pieceArg.xPos <= 6) {
			//Upper
			if(pieceArg.yPos >= 2) {
				singleSquareCheck(pieceArg.xPos+1, pieceArg.yPos-2,  moves, pieceArg, boardArg, player, checkTest);
			}
			//Lower
			if(pieceArg.yPos <= 5) {
				singleSquareCheck(pieceArg.xPos+1, pieceArg.yPos+2,  moves, pieceArg, boardArg, player, checkTest);
			}
		}		
		//Mid-left moves
		if(pieceArg.xPos >= 1) {
			//Upper
			if(pieceArg.yPos <= 5) {
				singleSquareCheck(pieceArg.xPos-1, pieceArg.yPos+2,  moves, pieceArg, boardArg, player, checkTest);
			}
			//Lower
			if(pieceArg.yPos >= 2) {
				singleSquareCheck(pieceArg.xPos-1, pieceArg.yPos-2,  moves, pieceArg, boardArg, player, checkTest);
			}
		}		
		//Leftmost moves
		if(pieceArg.xPos >= 2) {
			//Upper
			if(pieceArg.yPos <= 6) {
				singleSquareCheck(pieceArg.xPos-2, pieceArg.yPos+1,  moves, pieceArg, boardArg, player, checkTest);
			}
			//Lower
			if(pieceArg.yPos >= 1) {
				singleSquareCheck(pieceArg.xPos-2, pieceArg.yPos-1,  moves, pieceArg, boardArg, player, checkTest);
			}
		}		
		return moves;
	}

	/**
	 * Finds all possible valid moves on this turn for this piece, assuming it is a pawn
	 * @return an ArrayList of all valid move coordinates
	 */
	private static ArrayList<ClientMove> pawnMoveCheck(ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest){
		//TODO promotion
		ArrayList<ClientMove> moves = new ArrayList<ClientMove>();

		if(pieceArg.color == PieceColor.WHITE) {

			//En passant moves
			if(pieceArg.yPos == 3) {
				//Left en passant check
				if(pieceArg.xPos-1 >= 0) {
					if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos) && boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos-1)) {
						if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos).enPassantVulnerable) {
							if(checkTest) {
								if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos-1, pieceArg, boardArg, player))
									//System.out.println("EN PASSANT MOVE DETECTED");
									moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-1, false, true));
							}else {
								//System.out.println("EN PASSANT MOVE DETECTED");
								moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos-1, false, true));
							}
						}
					}
				}
				//Right en passant check
				if(pieceArg.xPos+1 <= 7) {
					if(!boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos) && boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos-1)) {
						if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos).enPassantVulnerable) {
							if(checkTest) {
								if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos-1, pieceArg, boardArg, player))
									//System.out.println("EN PASSANT MOVE DETECTED");
									moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-1, false, true));
							}else {
								//System.out.println("EN PASSANT MOVE DETECTED");
								moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos-1, false, true));
							}
						}
					}
				}
			}

			//Advancing moves
			if(pieceArg.yPos == 6) {
				if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos-1)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false, false));
					}
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos-2)) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-2, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-2, false, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-2, false, false));
						}
					}
				}
			}else {
				if(pieceArg.yPos-1 >= 0) {
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos-1)) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos-1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos-1, false, false));
						}
					}
				}
			}
			//Attacking moves
			if(pieceArg.xPos + 1 <= 7 && pieceArg.yPos-1 >= 0) {
				pawnAttackCheck(pieceArg.xPos+1, pieceArg.yPos-1, moves, pieceArg, boardArg, player, checkTest);
			}
			if(pieceArg.xPos - 1 >= 0 && pieceArg.yPos-1 >= 0) {
				pawnAttackCheck(pieceArg.xPos-1, pieceArg.yPos-1, moves, pieceArg, boardArg, player, checkTest);		
			}
		} else if(pieceArg.color == PieceColor.BLACK) {
			//En passant moves
			if(pieceArg.yPos == 4) {
				//Left en passant check
				if(pieceArg.xPos-1 >= 0) {
					if(!boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos) && boardArg.isSpaceFree(pieceArg.xPos-1, pieceArg.yPos+1)) {
						if(boardArg.getPieceAt(pieceArg.xPos-1, pieceArg.yPos).enPassantVulnerable) {
							if(checkTest) {
								if(legalMoveCheck(pieceArg.xPos-1, pieceArg.yPos+1, pieceArg, boardArg, player))
									//System.out.println("EN PASSANT MOVE DETECTED");
									moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+1, false, true));
							}else {
								//System.out.println("EN PASSANT MOVE DETECTED");
								moves.add(new ClientMove(pieceArg.xPos-1, pieceArg.yPos+1, false, true));
							}
						}
					}
				}
				//Right en passant check
				if(pieceArg.xPos+1 <= 7) {
					if(!boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos) && boardArg.isSpaceFree(pieceArg.xPos+1, pieceArg.yPos+1)) {
						if(boardArg.getPieceAt(pieceArg.xPos+1, pieceArg.yPos).enPassantVulnerable) {
							if(checkTest) {
								if(legalMoveCheck(pieceArg.xPos+1, pieceArg.yPos+1, pieceArg, boardArg, player))
									//System.out.println("EN PASSANT MOVE DETECTED");
									moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+1, false, true));
							}else {
								//System.out.println("EN PASSANT MOVE DETECTED");
								moves.add(new ClientMove(pieceArg.xPos+1, pieceArg.yPos+1, false, true));
							}
						}
					}
				}
			}

			//Advancing moves
			if(pieceArg.yPos == 1) {
				if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos+1)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+1, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false, false));
					}
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos+2)) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+2, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+2, false, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+2, false, false));
						}
					}
				}
			}else {
				if(pieceArg.yPos+1 <= 7) {
					if(boardArg.isSpaceFree(pieceArg.xPos, pieceArg.yPos+1)) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos, pieceArg.yPos+1, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos, pieceArg.yPos+1, false, false));
						}
					}
				}
			}
			//Attacking moves
			if(pieceArg.xPos + 1 <= 7 && pieceArg.yPos+1 <= 7) {
				pawnAttackCheck(pieceArg.xPos+1, pieceArg.yPos+1, moves, pieceArg, boardArg, player, checkTest);
			}
			if(pieceArg.xPos - 1 >= 0 && pieceArg.yPos+1 <= 7) {
				pawnAttackCheck(pieceArg.xPos-1, pieceArg.yPos+1, moves, pieceArg, boardArg, player, checkTest);		
			}
		}
		return moves;
	}

	/**
	 * Checks if a single square is legal to play.
	 */
	private static void singleSquareCheck(int checkX, int checkY, ArrayList<ClientMove> moves, ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest) {
		if(boardArg.isSpaceFree(checkX, checkY)) {
			if(checkTest) {
				if(legalMoveCheck(checkX, checkY, pieceArg, boardArg, player))
					moves.add(new ClientMove(checkX, checkY, false, false));
			}else {
				moves.add(new ClientMove(checkX, checkY, false, false));
			}
		}else {
			if(boardArg.getPieceAt(checkX, checkY).color != pieceArg.color) {
				if(checkTest) {
					if(legalMoveCheck(checkX, checkY, pieceArg, boardArg, player))
						moves.add(new ClientMove(checkX, checkY, false, false));
				}else {
					moves.add(new ClientMove(checkX, checkY, false, false));
				}
			}
		}
	}

	private static void pawnAttackCheck(int checkX, int checkY, ArrayList<ClientMove> moves, ClientPiece pieceArg,
			ClientBoard boardArg, ClientPlayer player, boolean checkTest) {
		if(!boardArg.isSpaceFree(checkX,checkY)) {				
			if(boardArg.getPieceAt(checkX, checkY).color != pieceArg.color) {
				if(checkTest) {
					if(legalMoveCheck(checkX, checkY, pieceArg, boardArg, player))
						moves.add(new ClientMove(checkX, checkY, false, false));
				}else {
					moves.add(new ClientMove(checkX, checkY, false, false));
				}
			}
		}
	}

	private static void diagonalMoveCheck(ArrayList<ClientMove> moves, ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest) {
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos-i >=0 && pieceArg.yPos-i >=0) {
				if(boardArg.isSpaceFree(pieceArg.xPos-i, pieceArg.yPos-i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos-i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos-i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos-i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos-i, false, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
		//Upper-right squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos+i <=7 && pieceArg.yPos-i >=0) {
				if(boardArg.isSpaceFree(pieceArg.xPos+i, pieceArg.yPos-i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos-i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos-i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos-i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos-i, false, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
		//Lower-left squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos-i >=0 && pieceArg.yPos+i <=7) {
				if(boardArg.isSpaceFree(pieceArg.xPos-i, pieceArg.yPos+i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos+i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos-i, pieceArg.yPos+i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos-i, pieceArg.yPos+i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos-i, pieceArg.yPos+i, false, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
		//Lower-right squares
		for(int i = 1; i <= 7; i++) {
			boolean escape = false;
			if(pieceArg.xPos+i <=7 && pieceArg.yPos+i <=7) {
				if(boardArg.isSpaceFree(pieceArg.xPos+i, pieceArg.yPos+i)) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos+i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false, false));
					}
				}else {
					if(boardArg.getPieceAt(pieceArg.xPos+i, pieceArg.yPos+i).color != pieceArg.color) {
						if(checkTest) {
							if(legalMoveCheck(pieceArg.xPos+i, pieceArg.yPos+i, pieceArg, boardArg, player))
								moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false, false));
						}else {
							moves.add(new ClientMove(pieceArg.xPos+i, pieceArg.yPos+i, false, false));
						}
						escape = true;
					}else {
						escape = true;
					}
				}
			}else {
				break;
			}
			if(escape) break;
		}
	}

	private static void straightMoveCheck(ArrayList<ClientMove> moves, ClientPiece pieceArg, ClientBoard boardArg, ClientPlayer player, boolean checkTest) {
		for(int i = pieceArg.xPos+1; i <= 7; i++) {
			boolean escape = false;
			if(boardArg.isSpaceFree(i, pieceArg.yPos)) {
				if(checkTest) {
					if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
						moves.add(new ClientMove(i, pieceArg.yPos, false, false));
				}else {
					moves.add(new ClientMove(i, pieceArg.yPos, false, false));
				}
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new ClientMove(i, pieceArg.yPos, false, false));
					}else {
						moves.add(new ClientMove(i, pieceArg.yPos, false, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}
		//Left squares
		for(int i = pieceArg.xPos-1; i >= 0; i--) {
			boolean escape = false;
			if(boardArg.isSpaceFree(i, pieceArg.yPos)) {
				if(checkTest) {
					if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
						moves.add(new ClientMove(i, pieceArg.yPos, false, false));
				}else {
					moves.add(new ClientMove(i, pieceArg.yPos, false, false));
				}
			}else {				
				if(boardArg.getPieceAt(i, pieceArg.yPos).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(i, pieceArg.yPos, pieceArg, boardArg, player))
							moves.add(new ClientMove(i, pieceArg.yPos, false, false));
					}else {
						moves.add(new ClientMove(i, pieceArg.yPos, false, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}
		//Lower squares
		for(int i = pieceArg.yPos+1; i <= 7; i++) {
			boolean escape = false;
			if(boardArg.isSpaceFree(pieceArg.xPos, i)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos, i, false, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos, i, false, false));
				}
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, i, false, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, i, false, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}
		//Upper squares
		for(int i = pieceArg.yPos-1; i >= 0; i--) {
			boolean escape = false;
			if(boardArg.isSpaceFree(pieceArg.xPos, i)) {
				if(checkTest) {
					if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
						moves.add(new ClientMove(pieceArg.xPos, i, false, false));
				}else {
					moves.add(new ClientMove(pieceArg.xPos, i, false, false));
				}
			}else {				
				if(boardArg.getPieceAt(pieceArg.xPos, i).color != pieceArg.color) {
					if(checkTest) {
						if(legalMoveCheck(pieceArg.xPos, i, pieceArg, boardArg, player))
							moves.add(new ClientMove(pieceArg.xPos, i, false, false));
					}else {
						moves.add(new ClientMove(pieceArg.xPos, i, false, false));
					}
					escape = true;
				}else {
					escape = true;
				}
			}
			if(escape) break;
		}				
	}
}
