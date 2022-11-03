package chess.common;

import java.util.UUID;

import com.osreboot.hvol2.base.HvlGameInfo;

public class NetworkUtil {

	public static final HvlGameInfo GAME_INFO = new HvlGameInfo("chess_sim", "1.0");
	public static final int TICK_RATE = 16;
	public static final int SERVER_HOST_PORT = 25565;
	
	public static String generateUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	public static final String KEY_PLAYER_STATUS = "game.playerstatus";
	public static final String KEY_COLLECTIVE_PLAYER_STATUS = "game.collectiveplayerstatus";
	public static final String KEY_CLIENT_GAME_READY = "game.clientgameready";
	public static final String KEY_COLLECTIVE_CLIENT_GAME_READY = "game.collectiveclientgameready";
	public static final String KEY_CLIENT_MOVE = "game.clientmove";
	public static final String KEY_SERVER_MOVE_RESPONSE = "game.servermoveresponse";
	public static final String KEY_CLIENT_GAME_OVER = "game.clientgameover";
	public static final String KEY_SERVER_GAME_OVER_RESPONSE = "game.servergameoverresponse";
	
}
