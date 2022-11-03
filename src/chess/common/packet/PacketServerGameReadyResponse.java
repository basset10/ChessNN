package chess.common.packet;

import java.io.Serializable;
import java.util.HashMap;

public class PacketServerGameReadyResponse implements Serializable{

	private static final long serialVersionUID = 2099920113627957316L;
	
	public HashMap <String, PacketClientGameReady> collectiveClientGameReady;
	public boolean isWhite;
	
	public PacketServerGameReadyResponse(HashMap<String, PacketClientGameReady> collectivePlayerGameReadyArg, boolean isWhiteArg){
		collectiveClientGameReady = collectivePlayerGameReadyArg;
		isWhite = isWhiteArg;
	}
}
