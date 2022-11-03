package chess.client;

import java.util.ArrayList;

import chess.client.ClientBoardSpace.Color;
import chess.client.ClientPiece.PieceColor;
import chess.client.ClientPiece.PieceType;

//The board the client sees and interacts with.
public class ClientBoard {

	public ArrayList<ArrayList<ClientBoardSpace>> board;
	//ArrayList containing all pieces currently in play.
	public ArrayList<ClientPiece> activePieces = new ArrayList<ClientPiece>();
	//ArrayList containing all pieces no longer in play.
	public ArrayList<ClientPiece> claimedPieces = new ArrayList<ClientPiece>();
	
	public ClientBoard(ArrayList<ClientPiece> activePiecesArg) {
		activePieces = activePiecesArg;
		board = new ArrayList<ArrayList<ClientBoardSpace>>();	
		boolean blackFlag = true;
		for(int i = 0; i < 8; i++) {
			board.add(new ArrayList<ClientBoardSpace>());			
			for(int j = 0; j < 8; j++) {
				if(blackFlag) {
					board.get(i).add(new ClientBoardSpace(j, i, Color.WHITE));
				}else {
					board.get(i).add(new ClientBoardSpace(j, i, Color.BLACK));
				}
				if(j != 7) blackFlag = !blackFlag;
			}
		}
	}

	public ClientBoard(ClientPlayer player) {
		//Initialize the board with starting pieces.
		initialize(player);
	}

	public void update(float delta, ClientPlayer player) {
		this.draw(player);
	}

	//Create the game board and populate it with starting pieces
	public void initialize(ClientPlayer player) {
		board = new ArrayList<ArrayList<ClientBoardSpace>>();	
		boolean blackFlag = true;
		for(int i = 0; i < 8; i++) {
			board.add(new ArrayList<ClientBoardSpace>());			
			for(int j = 0; j < 8; j++) {
				if(blackFlag) {
					board.get(i).add(new ClientBoardSpace(j, i, Color.WHITE));
				}else {
					board.get(i).add(new ClientBoardSpace(j, i, Color.BLACK));
				}
				if(j != 7) blackFlag = !blackFlag;
			}
		}
		assembleBoard();
	}

	//populate the game board with starting pieces
	private void assembleBoard() {
		activePieces.add(new ClientPiece(PieceType.ROOK, PieceColor.BLACK, 0, 0));
		activePieces.add(new ClientPiece(PieceType.KNIGHT, PieceColor.BLACK, 1, 0));
		activePieces.add(new ClientPiece(PieceType.BISHOP, PieceColor.BLACK, 2, 0));
		activePieces.add(new ClientPiece(PieceType.QUEEN, PieceColor.BLACK, 3, 0));
		activePieces.add(new ClientPiece(PieceType.KING, PieceColor.BLACK, 4, 0));
		activePieces.add(new ClientPiece(PieceType.BISHOP, PieceColor.BLACK, 5, 0));
		activePieces.add(new ClientPiece(PieceType.KNIGHT, PieceColor.BLACK, 6, 0));
		activePieces.add(new ClientPiece(PieceType.ROOK, PieceColor.BLACK, 7, 0));

		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.BLACK, 0, 1));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.BLACK, 1, 1));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.BLACK, 2, 1));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.BLACK, 3, 1));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.BLACK, 4, 1));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.BLACK, 5, 1));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.BLACK, 6, 1));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.BLACK, 7, 1));

		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.WHITE, 0, 6));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.WHITE, 1, 6));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.WHITE, 2, 6));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.WHITE, 3, 6));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.WHITE, 4, 6));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.WHITE, 5, 6));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.WHITE, 6, 6));
		activePieces.add(new ClientPiece(PieceType.PAWN, PieceColor.WHITE, 7, 6));

		activePieces.add(new ClientPiece(PieceType.ROOK, PieceColor.WHITE, 0, 7));
		activePieces.add(new ClientPiece(PieceType.KNIGHT, PieceColor.WHITE, 1, 7));
		activePieces.add(new ClientPiece(PieceType.BISHOP, PieceColor.WHITE, 2, 7));
		activePieces.add(new ClientPiece(PieceType.QUEEN, PieceColor.WHITE, 3, 7));
		activePieces.add(new ClientPiece(PieceType.KING, PieceColor.WHITE, 4, 7));
		activePieces.add(new ClientPiece(PieceType.BISHOP, PieceColor.WHITE, 5, 7));
		activePieces.add(new ClientPiece(PieceType.KNIGHT, PieceColor.WHITE, 6, 7));
		activePieces.add(new ClientPiece(PieceType.ROOK, PieceColor.WHITE, 7, 7));
	}

	/**
	 * Returns true if the given board space (x, y) does not currently hold a piece.
	 */
	public boolean isSpaceFree(int xArg, int yArg) {
		for(int i = 0; i < activePieces.size(); i++) {
			if(activePieces.get(i).xPos == xArg && activePieces.get(i).yPos == yArg) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the piece currently located on the given board position.
	 */
	public ClientPiece getPieceAt(int xArg, int yArg){
		
		if(!isSpaceFree(xArg, yArg)) {
		for(int i = 0; i < this.activePieces.size(); i++) {
			if(this.activePieces.get(i).xPos == xArg && activePieces.get(i).yPos == yArg) {
				try {
					return this.activePieces.get(i);
				}catch(NullPointerException e) {
					System.out.println("ERROR! Cannot locate piece at " + xArg + ", " + yArg);
				}				
			}
		}
		System.out.println("ERROR! Cannot locate piece at " + xArg + ", " + yArg);
		return new ClientPiece();
		}else {
			System.out.println("There is no piece on " + xArg + ", " + yArg);
			return new ClientPiece();
		}
	}

	//Returns the board space at a given x and y position.
	public ClientBoardSpace getSpaceAt(int xArg, int yArg) {
		return board.get(xArg).get(yArg);
	}

	private void draw(ClientPlayer player) {
		//Draw the board itself
		for(int i = 0; i < board.size(); i++) {	
			for(int j = 0; j < board.get(i).size(); j++) {
				getSpaceAt(i, j).draw();				
			}			
		}
		//Draw the board pieces in their current locations
		for(int i = 0; i < activePieces.size(); i++) {
			activePieces.get(i).draw(player);
		}
	}
}
