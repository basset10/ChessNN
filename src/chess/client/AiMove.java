package chess.client;

public class AiMove {

	public ClientPiece piece;
	public ClientMove move;

	public AiMove(ClientPiece clientPieceArg, ClientMove clientMoveArg) {
		piece = clientPieceArg;
		move = clientMoveArg;
	}
	
}
