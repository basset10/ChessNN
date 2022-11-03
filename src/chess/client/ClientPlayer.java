package chess.client;

public class ClientPlayer {
	
	public enum PlayerColor{
		NONE,
		WHITE,
		BLACK;
	}

	public String id;
	public PlayerColor color;
	
	public ClientPlayer(String idArg) {
		id = idArg;
		color = PlayerColor.NONE;
	}
	
}
