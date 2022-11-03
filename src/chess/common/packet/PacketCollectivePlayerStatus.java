package chess.common.packet;

import java.io.Serializable;
import java.util.HashMap;


public class PacketCollectivePlayerStatus implements Serializable{

	private static final long serialVersionUID = 4631703546025636103L;

	public HashMap <String, PacketPlayerStatus> collectivePlayerStatus;
	
	public PacketCollectivePlayerStatus(){}
	
	public PacketCollectivePlayerStatus(HashMap<String, PacketPlayerStatus> collectivePlayerStatusArg){
		collectivePlayerStatus = collectivePlayerStatusArg;
	}
	
}
