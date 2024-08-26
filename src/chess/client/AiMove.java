package chess.client;
//A move used by the AI. Needs to be formatted differently from a human move.
public class AiMove {

	public ClientPiece piece;
	public ClientMove move;

	public AiMove(ClientPiece clientPieceArg, ClientMove clientMoveArg) {
		piece = clientPieceArg;
		move = clientMoveArg;
	}
	
}
