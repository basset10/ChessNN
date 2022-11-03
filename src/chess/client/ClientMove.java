package chess.client;

public class ClientMove {
	
	public int x;
	public int y;
	public boolean castle;
	public boolean enPassant;
	
	public ClientMove(int x, int y, boolean castle, boolean enPassant) {
		this.x = x;
		this.y = y;
		this.castle = castle;
		this.enPassant = enPassant;
	}
}
