package chess.common.packet;

import java.io.Serializable;

public class PacketServerMoveResponse implements Serializable{

	private static final long serialVersionUID = -5069162660121105360L;
	public String id;
	public PacketClientMove packet;
	
	public PacketServerMoveResponse(String idArg, PacketClientMove packetArg) {
		id = idArg;
		packet = packetArg;
	}
}
