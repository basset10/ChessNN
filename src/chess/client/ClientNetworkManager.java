package chess.client;


import com.osreboot.hvol2.base.anarchy.HvlAgentClientAnarchy;
import com.osreboot.hvol2.direct.HvlDirect;
import com.osreboot.hvol2.direct.HvlDirect.HvlAgentStatus;

import chess.common.NetworkUtil;

public class ClientNetworkManager {

	public static String id;

	public static boolean isConnected = false;

	public static void initialize(){
		id = NetworkUtil.generateUUID();
	}

	public static void connect(String ip, int port){
		HvlDirect.initialize(NetworkUtil.TICK_RATE, new HvlAgentClientAnarchy(NetworkUtil.GAME_INFO, ip, port, id, true));
		HvlDirect.connect();
		isConnected = true;
	}

	public static void update(float delta){		
		if(isConnected) {
			HvlDirect.update(delta);
			if(HvlDirect.getStatus() == HvlAgentStatus.DISCONNECTED) {
				disconnect();
			}
		}	
	}
	
	public static void disconnect() {
		HvlDirect.disconnect();
		isConnected = false;
	}
}
