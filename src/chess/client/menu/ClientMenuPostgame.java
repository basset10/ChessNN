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

import com.osreboot.ridhvl2.HvlMath;
import com.samuel.Network;

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

	public static Network championNetwork = null;

	private ArrayList<ClientButton> buttons;

	public ClientMenuPostgame(ClientGame game) {
		buttons = new ArrayList<ClientButton>();

		buttons.add(new ClientButton(230, 90, Display.getWidth()/2f+450, Display.getHeight()/2f-100, "Next Generation", () ->{
			if(ClientGame.training) {
				//If every game this generation has been played...

				int topPercent = GeneticsHandler.GAMES_PER_GENERATION/10;

				System.out.println("White won " + ((float)game.whiteWinCount/(float)GeneticsHandler.GAMES_PER_GENERATION)*100 + "% on generation " + GeneticsHandler.currentGeneration);

				Collections.sort(GeneticsHandler.population, GeneticsHandler.compareByScore);


				int chosenAgent1 = HvlMath.randomInt(0, topPercent-1);
				ClientPlayer par1 = GeneticsHandler.population.get(chosenAgent1);
				System.out.println(GeneticsHandler.population.get(chosenAgent1).fitness);
				int chosenAgent2 = HvlMath.randomInt(0, topPercent-1);
				while(chosenAgent1 == chosenAgent2) {
					chosenAgent2 = HvlMath.randomInt(0, topPercent-1);
				}
				ClientPlayer par2 = GeneticsHandler.population.get(chosenAgent2);
				System.out.println(GeneticsHandler.population.get(chosenAgent2).fitness);


				//championNetwork = Network.deepCopy(par1.decisionNet);
				GeneticsHandler.oldPop = new ArrayList<ClientPlayer>(GeneticsHandler.population);
				GeneticsHandler.population.clear();					
				GeneticsHandler.duplicateParents(par1, par2);


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
				//System.out.println("Start Generation " + GeneticsHandler.currentGeneration);
				game.player1.clone(GeneticsHandler.population.get(game.gameThisGen-1));
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
