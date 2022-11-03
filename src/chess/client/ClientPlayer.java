package chess.client;

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
	
}
