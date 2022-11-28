package chess.client.menu;

import static com.osreboot.ridhvl2.HvlStatics.hvlFont;
import static com.osreboot.ridhvl2.HvlStatics.hvlQuadc;
import static com.osreboot.ridhvl2.HvlStatics.hvlDraw;
import static com.osreboot.ridhvl2.HvlStatics.hvlTexture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import chess.common.Util;
import chess.client.ClientBoard;
import chess.client.ClientGame;
import chess.client.ClientGame.GameState;
import chess.client.ClientPlayer.PlayerColor;
import chess.client.ClientLoader;
import chess.client.ClientMove;
import chess.client.ClientPlayer;
import chess.client.GeneticsHandler;

public class ClientMenuPostgame {

	private ArrayList<ClientButton> buttons;

	public ClientMenuPostgame(ClientGame game) {
		buttons = new ArrayList<ClientButton>();

		buttons.add(new ClientButton(230, 90, Display.getWidth()/2f+450, Display.getHeight()/2f-100, "Next Generation", () ->{
			if(ClientGame.training) {
				//If every game this generation has been played...

				//ArrayList<ClientPlayer>topPlayers = new ArrayList<ClientPlayer>();
				//Collections.sort(GeneticsHandler.population, GeneticsHandler.compareByScore);

				
				
				
				
			//	for(int i = 0; i < 100; i++) {
			//		topPlayers.add(GeneticsHandler.population.get(i));
			//	}
				//ClientPlayer par1 = GeneticsHandler.population.get(0);
				//ClientPlayer par2 = GeneticsHandler.population.get(1);
				/*
				for(int p = 2; p < GeneticsHandler.population.size(); p++) {
					Network.deleteNetwork(GeneticsHandler.population.get(p).decisionNet);
				}
				 */
				//GeneticsHandler.oldPop = new ArrayList<ClientPlayer>(GeneticsHandler.population);
				
				
				//Number of winners not always increasing - perhaps black is not using seeded movement every generation
				ArrayList<ClientPlayer>topPlayers = new ArrayList<ClientPlayer>();
				
				for(ClientPlayer c : GeneticsHandler.population) {
					if(c.getFitness() <= 1) {
						topPlayers.add(c);
					}
				}
				
				System.out.println(topPlayers.size() + " players with fitness less than 1");
				
				GeneticsHandler.population.clear();

			/*	for(ClientPlayer c : topPlayers) {
					GeneticsHandler.population.add(c);
				}*/
				
				for(int i = 0; i < 500; i++) {
					GeneticsHandler.population.add(topPlayers.get(0));
				}
				
				//int remaining = 500 - topPlayers.size();
				
				//for(int i = 0; i < remaining; i++) {
				//	GeneticsHandler.populate(new ClientPlayer("", false));
				//}
				
				
			//	for(int i = 0; i < 100; i++) {
			//		for(int j = 0; j < 4; j++) {
			//			GeneticsHandler.mutatePlayer(GeneticsHandler.population.get(i));
			//		}
					//GeneticsHandler.population.add(topPlayers.get(i));
			//	}
				
				//CLONE PARENTS, MUTATE PARENTS, AND REPOPULATE
				//GeneticsHandler.duplicateParents(par1, par2);
				GeneticsHandler.currentGeneration++;
				game.player2.rng = new Random("poggers".hashCode());
				game.state = GameState.training;
				
				game.gameThisGen = 1;
				game.validMoves = new ArrayList<ClientMove>();
				game.selectedPiecexPos = -1;
				game.selectedPieceyPos = -1;
				game.drawCount = 0;
				game.incrementDrawCount = true;
				//state = GameState.menu;
				game.inCheck = false;
				game.gameEndState = ClientGame.GAME_END_STATE_CONTINUE;
				game.finalMove = null;
				game.moveCount = 0;
				game.promotionUI = false;
				System.out.println("Start Generation " + GeneticsHandler.currentGeneration);
				game.player1 = GeneticsHandler.population.get(game.gameThisGen-1);
				game.player1.color = PlayerColor.WHITE;
				game.player2.color = PlayerColor.BLACK;
				game.player1Turn = true;
				game.player2.rng = new Random("poggers".hashCode());

				
				
				game.board = new ClientBoard(game.player1);
				game.board.initialize(game.player1);
				game.boardInitialized = true;
				game.whiteWinCount=0;
				game.blackWinCount=0;
				game.stalemateCount=0;
				game.state = GameState.training;
			}else {
				game.state = GameState.playingHuman;
			}
			game.reset();
		}));
		buttons.add(new ClientButton(230, 90, Display.getWidth()/2f+450, Display.getHeight()/2f+100, "Main Menu", () ->{
			game.reset();
			game.state = GameState.menu;
			ClientMenuManager.menu = ClientMenuManager.MenuState.main;

		}));
	}


	public void operate() {	

		for(ClientButton b : buttons) {
			b.operate();
		}

	}

}
