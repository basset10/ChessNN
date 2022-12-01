package chess.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;

import com.osreboot.ridhvl2.HvlMath;
import com.osreboot.ridhvl2.HvlConfig;
import com.samuel.Network;

import chess.client.ClientPlayer.PlayerColor;

public class GeneticsHandler {
	//Games are played and displayed one at a time.
	public static final int GAMES_PER_GENERATION = 100;

	public static int currentGeneration = 1;
	public static ArrayList<ClientPlayer> population;
	public static ArrayList<ClientPlayer> oldPop;


	// public static Player hero;

	public static void init(ClientGame game) {

		
		population = new ArrayList<>();
		// hero = new Player();
		// hero.fitness = 100;
		File bestPlayerData = new File("championNetwork.json");
		if (bestPlayerData.exists()) {
			Network championNet = HvlConfig.PJSON.load("championNetwork.json");
			ClientPlayer p = new ClientPlayer("", false);

			p.setNetwork(championNet);
			populate(p);

			for (int i = 1; i < GAMES_PER_GENERATION; i++) {
				populate(new ClientPlayer("", false));
			}
		} else {
			for (int i = 0; i < GAMES_PER_GENERATION; i++) {
				populate(new ClientPlayer("", false));
			}
		}
	}

	public static void populate(ClientPlayer p) {
		population.add(p);
	}

	//ONLY WHITE GETS TRAINED
	public static float calcFitness(ClientPlayer p, ClientGame g) {

		//Chess version - Check if player won, how many moves were made, MAYBE how many captures were made or friendly pieces remaining?
		//Lower fitness is better(??)		
		if(g.gameEndState == ClientGame.GAME_END_STATE_CHECKMATE && g.finalMove == PlayerColor.WHITE) {
			return (float)g.moveCount/1000;
		}else if(g.gameEndState == ClientGame.GAME_END_STATE_STALEMATE) {
			return (float)g.moveCount;
		}else{
			return 10000f;
		}
	}

	public static void duplicateParents(ClientPlayer par1, ClientPlayer par2) {
		Network parent1Network = par1.decisionNet;
		Network parent2Network = par2.decisionNet;
		ClientPlayer newPar1 = new ClientPlayer(UUID.randomUUID().toString(), false);
		newPar1.setNetwork(Network.deepCopy(parent1Network));
		ClientPlayer newPar2 = new ClientPlayer(UUID.randomUUID().toString(), false);
		newPar2.setNetwork(Network.deepCopy(parent2Network));
		populate(newPar1);
		populate(newPar2);

		for (int i = 0; i < (GAMES_PER_GENERATION - 2); i++) {
			mutatePlayer(crossOverGenes(newPar1, newPar2));
		}


	}

	public static void fillWithRankedChoice() {
		int totalRank = getTotalRank();
		int[] probList = generateProbList();
		for (int i = 0; i < (GAMES_PER_GENERATION - 2); i++) {
			Random r = new Random();
			int s1 = r.nextInt(totalRank);
			int s2 = r.nextInt(totalRank);

			Network parent1Network = rankedChoiceRoulette(s1, probList).decisionNet;
			Network parent2Network = rankedChoiceRoulette(s2, probList).decisionNet;

			ClientPlayer newPar1 = new ClientPlayer(UUID.randomUUID().toString(), false);
			newPar1.setNetwork(Network.deepCopy(parent1Network));
			ClientPlayer newPar2 = new ClientPlayer(UUID.randomUUID().toString(), false);
			newPar2.setNetwork(Network.deepCopy(parent2Network));

			mutatePlayer(crossOverGenes(newPar1, newPar2));
		}

	}

	private static int getTotalRank() {
		int rankTotal = 0;
		for (int r = GAMES_PER_GENERATION; r > 0; r--) {
			rankTotal += r;
		}

		return rankTotal;
	}

	public static int[] generateProbList() {
		int[] probList = new int[GAMES_PER_GENERATION + 1];
		for (int i = GAMES_PER_GENERATION + 1; i > 0; i--) {
			if (i == GAMES_PER_GENERATION + 1) {
				probList[GAMES_PER_GENERATION + 1 - i] = 0;
			} else {
				probList[GAMES_PER_GENERATION - i + 1] = i + probList[GAMES_PER_GENERATION + 1 - i - 1];
			}
		}
		return probList;
	}

	private static ClientPlayer rankedChoiceRoulette(int selection, int[] probList) {
		for (int s = 0; s < GAMES_PER_GENERATION; s++) {
			if (selection >= probList[s] && selection < probList[s + 1]) {
				return oldPop.get(s);
			}
		}
		return null;
	}

	public static ClientPlayer crossOverGenes(ClientPlayer c1, ClientPlayer c2) {

		ClientPlayer child = new ClientPlayer(UUID.randomUUID().toString(), false);

		float geneticBias = (c2.getFitness() - c1.getFitness()) / c2.getFitness();

		for (int l = 0; l < child.decisionNet.layers.size(); l++) {
			for (int n = 0; n < child.decisionNet.layers.get(l).numNodes; n++) {
				for (int i = 0; i < child.decisionNet.layers.get(l).nodes.get(n).connectionWeights.size(); i++) {
					double rand = Math.random();
					if (rand < 0.5 + geneticBias) {
						child.decisionNet.layers.get(l).nodes.get(n).connectionWeights.put(i,
								c1.decisionNet.layers.get(l).nodes.get(n).connectionWeights.get(i));
					} else {
						child.decisionNet.layers.get(l).nodes.get(n).connectionWeights.put(i,
								c2.decisionNet.layers.get(l).nodes.get(n).connectionWeights.get(i));
					}
				}
				double biasRand = Math.random();
				if (biasRand < 0.5 + geneticBias) {
					child.decisionNet.layers.get(l).nodes.get(n).bias = c1.decisionNet.layers.get(l).nodes.get(n).bias;
				} else {
					child.decisionNet.layers.get(l).nodes.get(n).bias = c2.decisionNet.layers.get(l).nodes.get(n).bias;
				}
			}
		}
		return child;
	}

	public static void mutatePlayer(ClientPlayer p) {
		for (int l = 0; l < p.decisionNet.layers.size(); l++) {
			for (int n = 0; n < p.decisionNet.layers.get(l).numNodes; n++) {
				for (int i = 0; i < p.decisionNet.layers.get(l).nodes.get(n).connectionWeights.size(); i++) {
					double rand = Math.random();
					if (rand < 0.05) {
						p.decisionNet.layers.get(l).nodes.get(n).connectionWeights.put(i, (float) Math.random());
					}
				}
				double biasRand = Math.random();
				if (biasRand < 0.05) {
					p.decisionNet.layers.get(l).nodes.get(n).bias = (float) Math.random();
				}
			}
		}
		populate(p);
	}

	public static Comparator<ClientPlayer> compareByScore = new Comparator<ClientPlayer>() {
		@Override
		public int compare(ClientPlayer p1, ClientPlayer p2) {
			return Float.compare(p1.getFitness(), p2.getFitness());
		}
	};

	//	public static void setHero(Player p) {
	//		hero = p;
	//	}
}
