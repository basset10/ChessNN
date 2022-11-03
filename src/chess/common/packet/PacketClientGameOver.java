package chess.common.packet;

import java.io.Serializable;

import chess.client.ClientPlayer;

public class PacketClientGameOver implements Serializable{

	private static final long serialVersionUID = 3138960309205733253L;
	public int gameEndState;
	public ClientPlayer.PlayerColor playerColor;
	
	public PacketClientGameOver(int gameEndStateArg, ClientPlayer.PlayerColor playerColorArg) {
		gameEndState = gameEndStateArg;
		playerColor = playerColorArg;
	}

}
