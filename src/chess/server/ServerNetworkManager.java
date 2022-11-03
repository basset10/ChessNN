package chess.server;

import com.osreboot.hvol2.base.anarchy.HvlAgentServerAnarchy;
import com.osreboot.hvol2.base.anarchy.HvlIdentityAnarchy;
import com.osreboot.hvol2.direct.HvlDirect;

import chess.common.NetworkUtil;

public class ServerNetworkManager {

	private static ServerLobby lobby;
	
	public static void initialize() {
		HvlDirect.initialize(NetworkUtil.TICK_RATE, new HvlAgentServerAnarchy(NetworkUtil.GAME_INFO, NetworkUtil.SERVER_HOST_PORT, true));
		HvlDirect.connect();
		
		HvlDirect.bindOnMessageReceived((m, i) -> {
			if(lobby.getIds().contains(i)){
			}else{
				m.getTable().clear();
			}
			return m;
		});

		//Client first establishes connection to the server - add their HvlIdentityAnarchy to the list of lobby IDs.
		HvlDirect.bindOnRemoteConnection(i -> {
			System.out.println("Connection - " + i);
			lobby.onConnect((HvlIdentityAnarchy)i);
		});

		//Client disconnects from the server - remove their HvlIdentityAnarchy from the list of lobby IDs.
		HvlDirect.bindOnRemoteDisconnection(i -> {
			System.out.println("Disconnection - " + i);
			lobby.onDisconnect((HvlIdentityAnarchy)i);
		});
				
		//Create a lobby for clients to connect to.
		lobby = new ServerLobby();
	}
	
	public static void update(float delta) {
		HvlDirect.update(delta);
		lobby.update(delta);
	}
	
}
