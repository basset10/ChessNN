package chess.common.packet;

import java.io.Serializable;

public class PacketClientGameReady implements Serializable{

	private static final long serialVersionUID = -6294701676692928101L;
	
	public String id;
	public boolean isWhite;
	
	public PacketClientGameReady(String idArg) {
		id = idArg;
	}
	
	
}
