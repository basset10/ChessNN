package chess.common.packet;

import java.io.Serializable;

public class PacketPlayerStatus implements Serializable{

	private static final long serialVersionUID = 4748226049531864818L;
	
	public boolean connected;
	
	public PacketPlayerStatus(boolean connectedArg){
		connected = connectedArg;
	}

}
