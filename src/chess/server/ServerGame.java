package chess.server;

import java.util.HashMap;
import java.util.Random;

import com.osreboot.hvol2.base.anarchy.HvlAgentServerAnarchy;
import com.osreboot.hvol2.base.anarchy.HvlIdentityAnarchy;
import com.osreboot.hvol2.direct.HvlDirect;

import chess.common.NetworkUtil;
import chess.common.packet.PacketClientGameOver;
import chess.common.packet.PacketClientGameReady;
import chess.common.packet.PacketClientMove;
import chess.common.packet.PacketServerGameReadyResponse;
import chess.common.packet.PacketServerMoveResponse;
import chess.common.packet.PacketCollectivePlayerStatus;
import chess.common.packet.PacketPlayerStatus;


public class ServerGame {

	static final int PLAYER_WHITE = 0;
	static final int PLAYER_BLACK = 1;

	private Random rd = new Random();
	private boolean colorTrack;
	private boolean secondColor = false;

	public static int playerTurn = PLAYER_WHITE;

	private static boolean boardInitialized = false;

	public void update() {

		HashMap<String, PacketPlayerStatus> collectivePlayerStatus = new HashMap<String, PacketPlayerStatus>();
		HashMap<String, PacketClientGameReady> collectiveClientGameReady = new HashMap<String, PacketClientGameReady>();

		//receive packets from clients...
		//Collect all player status packets and create CollectivePlayerStatus packet.
		for(HvlIdentityAnarchy i : HvlDirect.<HvlIdentityAnarchy>getConnections()) {

			if(HvlDirect.getKeys(i).contains(NetworkUtil.KEY_CLIENT_GAME_OVER)) {				
				PacketClientGameOver packet = HvlDirect.getValue(i, NetworkUtil.KEY_CLIENT_GAME_OVER);
				((HvlAgentServerAnarchy)HvlDirect.getAgent()).getTable(i).remove(NetworkUtil.KEY_CLIENT_GAME_OVER);
				System.out.println("Game is over!");	
				for(HvlIdentityAnarchy j : HvlDirect.<HvlIdentityAnarchy>getConnections()) {
					HvlDirect.writeTCP(j, NetworkUtil.KEY_SERVER_GAME_OVER_RESPONSE, packet);
				}
			}
			if(HvlDirect.getKeys(i).contains(NetworkUtil.KEY_PLAYER_STATUS)) {
				//System.out.println("PACKET RECEIVED!!!");
				collectivePlayerStatus.put(i.getName(), HvlDirect.getValue(i, NetworkUtil.KEY_PLAYER_STATUS));
			}

			if(HvlDirect.getKeys(i).contains(NetworkUtil.KEY_CLIENT_GAME_READY)) {
				System.out.println("Client Game Ready Packet Received");
				collectiveClientGameReady.put(i.getName(), HvlDirect.getValue(i, NetworkUtil.KEY_CLIENT_GAME_READY));
				((HvlAgentServerAnarchy)HvlDirect.getAgent()).getTable(i).remove(NetworkUtil.KEY_CLIENT_GAME_READY);
			}

			
			if(HvlDirect.getKeys(i).contains(NetworkUtil.KEY_CLIENT_MOVE)) {
				PacketClientMove movePacket = HvlDirect.getValue(i, NetworkUtil.KEY_CLIENT_MOVE);
				System.out.println("move attemped by client " + movePacket.id + " from space [" + movePacket.existingPieceX + "," + movePacket.existingPieceY + "] to space [" +
						movePacket.intendedMoveX + "," + movePacket.intendedMoveY + "]");
				for(HvlIdentityAnarchy j : HvlDirect.<HvlIdentityAnarchy>getConnections()) {
					HvlDirect.writeTCP(j, NetworkUtil.KEY_SERVER_MOVE_RESPONSE, new PacketServerMoveResponse(movePacket.id, movePacket));		
				}
				if(playerTurn == PLAYER_WHITE) {
					playerTurn = PLAYER_BLACK;
				}else if(playerTurn == PLAYER_BLACK) {
					playerTurn = PLAYER_WHITE;
				}
				((HvlAgentServerAnarchy)HvlDirect.getAgent()).getTable(i).remove(NetworkUtil.KEY_CLIENT_MOVE);
			}
		}

		//send packets to clients...
		for(HvlIdentityAnarchy i : HvlDirect.<HvlIdentityAnarchy>getConnections()) {
			HvlDirect.writeTCP(i, NetworkUtil.KEY_COLLECTIVE_PLAYER_STATUS, new PacketCollectivePlayerStatus(collectivePlayerStatus));
			//If both players are ready, initialize the game
			if(collectiveClientGameReady.size() == 2) {
				if(!secondColor) {
					colorTrack = rd.nextBoolean();
					secondColor = true;
				}else {
					colorTrack = !colorTrack;
					secondColor = false;
				}
				HvlDirect.writeTCP(i, NetworkUtil.KEY_COLLECTIVE_CLIENT_GAME_READY, new PacketServerGameReadyResponse(collectiveClientGameReady, colorTrack));
				boardInitialized = true;
			}		
			if(boardInitialized) {
				if(collectivePlayerStatus.size() != 2) {
					reset();
				}
			}
		}
	}

	public void reset() {
		boardInitialized = false;
		secondColor = false;
		playerTurn = PLAYER_WHITE;
	}
}
