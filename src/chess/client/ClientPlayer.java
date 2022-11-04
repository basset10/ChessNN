package chess.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import chess.client.ClientPiece.PieceColor;

public class ClientPlayer {

	public enum PlayerColor{
		NONE,
		WHITE,
		BLACK;
	}

	public boolean human;
	public String id;
	public PlayerColor color;

	public ClientPlayer(String idArg, boolean humanArg) {
		human = humanArg;
		id = idArg;
		color = PlayerColor.NONE;
	}

	//Used for AI players
	//Begin by selecting a random valid move from a list of all valid moves for this player.
	public AiMove generateMove(ClientBoard board, ClientPlayer player) {


		ArrayList<ClientPiece> possiblePieces = new ArrayList<ClientPiece>();
		ArrayList<ClientMove> possibleMoves = new ArrayList<ClientMove>();

		for(ClientPiece p : board.activePieces) {
			if((p.color == PieceColor.BLACK && player.color == PlayerColor.BLACK) || 
					(p.color == PieceColor.WHITE && player.color == PlayerColor.WHITE)) {
				possiblePieces.add(p);
			}
		}

		Random rng = new Random();
		int pieceIndex = rng.nextInt(possiblePieces.size());


		boolean legalPieceFlag = true;
		ClientPiece selectedPiece = null;

		while(legalPieceFlag) {
			if(possiblePieces.get(pieceIndex).getAllValidMoves(board, player).size() > 0) {
				selectedPiece = possiblePieces.get(pieceIndex);	
				legalPieceFlag = false;
			}else {
				pieceIndex = rng.nextInt(possiblePieces.size());
			}
		}


		System.out.println("Chosen Piece: " + selectedPiece.type.toString());
		possibleMoves.addAll(selectedPiece.getAllValidMoves(board, player));
		System.out.println("Number of possible moves: " + possibleMoves.size());
		int moveIndex = rng.nextInt(possibleMoves.size());

		return new AiMove(selectedPiece, possibleMoves.get(moveIndex));

	}

}
