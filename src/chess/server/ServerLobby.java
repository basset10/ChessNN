package chess.server;


import java.util.HashSet;

import com.osreboot.hvol2.base.anarchy.HvlIdentityAnarchy;

public class ServerLobby {
	private HashSet<HvlIdentityAnarchy> ids;
	private ServerGame game;

	public ServerLobby(){
		ids = new HashSet<>();
		game = new ServerGame();
	}

	public void update(float delta){
		game.update();
		//drawStatusInfo();
	}
		
	public void onConnect(HvlIdentityAnarchy identity){
		ids.add(identity);
	}
	
	public void onDisconnect(HvlIdentityAnarchy identity){
		ids.remove(identity);

	}
	
	public HashSet<HvlIdentityAnarchy> getIds(){
		return ids;
	}
	
}
