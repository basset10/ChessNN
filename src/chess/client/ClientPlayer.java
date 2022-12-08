package chess.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


import com.samuel.Network;
import com.samuel.NetworkMain;

import chess.client.ClientPiece.PieceColor;


//Network Topology:
//Input Layer:
//64 x 64 (4096) squares, for each possible starting square and each possible landing square.
//First, check if a friendly piece exists on the starting square.
//If there is a friendly piece, check which squares are valid move positions.

//Output Layer: Same size as input.

public class ClientPlayer {

	public float fitness;
	public Network decisionNet = null;
	public Random rng = new Random("poggers".hashCode());
	public ArrayList<Float> fitnessCount;

	//If this is NOT null, an AI player will executed the queued move on the following possible frame, then reset this to null.
	public AiMove incomingMoveToExecute = null;

	public enum PlayerColor{
		NONE,
		WHITE,
		BLACK;
	}

	public boolean human;
	public String id;
	public PlayerColor color;

	public ClientPlayer(String idArg, boolean humanArg) {
		fitnessCount = new ArrayList<Float>();
		fitness = 10000;
		human = humanArg;
		id = idArg;
		color = PlayerColor.NONE;
		decisionNet = new Network(256, 64, 64, 256);
		//input, hidden, output
	}
	
	public ClientPlayer(String idArg, boolean humanArg, Network networkArg) {
		fitnessCount = new ArrayList<Float>();
		decisionNet = networkArg;
		fitness = 10000;
		human = humanArg;
		id = idArg;
		color = PlayerColor.NONE;
		//input, hidden, output
	}
	
	public void clone(ClientPlayer oldPlayer) {
		fitness = oldPlayer.fitness;
		decisionNet = Network.deepCopy(oldPlayer.decisionNet);
		//rng = new Random("poggers".hashCode());
		incomingMoveToExecute = oldPlayer.incomingMoveToExecute;
		color = oldPlayer.color;
		human = oldPlayer.human;
		id = oldPlayer.id;
		fitnessCount.clear();
		
	}

	//Used for AI players
	//Select a random valid move from a list of all valid moves for this player.
	public AiMove generateRandomMove(ClientBoard board, ClientPlayer player) {

		ArrayList<ClientPiece> possiblePieces = new ArrayList<ClientPiece>();
		ArrayList<ClientMove> possibleMoves = new ArrayList<ClientMove>();

		for(ClientPiece p : board.activePieces) {
			if((p.color == PieceColor.BLACK && player.color == PlayerColor.BLACK) || 
					(p.color == PieceColor.WHITE && player.color == PlayerColor.WHITE)) {
				if(p.getAllValidMoves(board, player).size() > 0) {
					possiblePieces.add(p);
					//System.out.println("piece " + p.type.toString() + " on space (" + p.xPos + ", " + p.yPos + ") has " + p.getAllValidMoves(board, player).size() + " legal moves.");
				}

			}
		}

		

		int pieceIndex = 0;
		if(possiblePieces.size() > 0) {
			pieceIndex = rng.nextInt(possiblePieces.size());
		}else {
			return null;
		}

		boolean legalPieceFlag = true;
		ClientPiece selectedPiece = null;

		while(legalPieceFlag) {
			if(possiblePieces.get(pieceIndex).getAllValidMoves(board, player).size() > 0) {
				selectedPiece = possiblePieces.get(pieceIndex);	
				legalPieceFlag = false;
			}else {
				pieceIndex = rng.nextInt(possiblePieces.size());
			}
		}

		//System.out.println("Chosen Piece: " + selectedPiece.type.toString());
		possibleMoves.addAll(selectedPiece.getAllValidMoves(board, player));
		//System.out.println("Number of possible moves: " + possibleMoves.size());
		int moveIndex = rng.nextInt(possibleMoves.size());

		return new AiMove(selectedPiece, possibleMoves.get(moveIndex));

	}


	public void updateNetwork(ClientGame game) {

		//Identify and set all input nodes.

		//16x16 total input nodes for all board squares and all possible landing squares.
		//First check if each initial square has a friendly piece.
		//If so, run a check on all possible landing squares.
		//Every legal move = 1
		//Everything else  = 0

		int nodeCounter = 0;

		for(int i = 0; i <= 3; i++) {
			for(int j = 0; j <= 3; j++) {
				if(!game.board.isSpaceFree(i, j)) {
					if(game.board.getPieceAt(i, j).color.toString().equals(color.toString())) {

						ArrayList<ClientMove> moves = game.board.getPieceAt(i, j).getAllValidMoves(game.board, this);

						for(int k = 0; k <= 3; k++) {
							for(int l = 0; l <= 3; l++) {

								boolean validSpace = false;
								for(ClientMove m : moves) {
									if(m.x == k && m.y == l) {
										//If a valid mode exists, this node is a 1
										decisionNet.layers.get(0).nodes.get(nodeCounter).value = 1;
										//System.out.println("Setting Node Number " + nodeCounter + " (VALID)");
										nodeCounter++;
										validSpace = true;
										break;
									}
								}
								if(!validSpace) {
									//This node is a 0
									decisionNet.layers.get(0).nodes.get(nodeCounter).value = 0;
									//System.out.println("Setting Node Number " + nodeCounter + " (NO LEGAL MOVE)");
									nodeCounter++;
								}



							}
						}
					}else {
						//All 16 landing nodes are 0
						for(int z = 0; z < 16; z++) {
							decisionNet.layers.get(0).nodes.get(nodeCounter).value = 0;
							//System.out.println("Setting Node Number " + nodeCounter + "(SKIP)");
							nodeCounter++;
						}
					}
				}else {
					//All 16 landing nodes are 0
					for(int z = 0; z < 16; z++) {
						decisionNet.layers.get(0).nodes.get(nodeCounter).value = 0;
						//System.out.println("Setting Node Number " + nodeCounter + "(SKIP)");
						nodeCounter++;						
					}
				}
			}

		}

		//System.out.println("OUTPUT NODES: " + decisionNet.lastLayer().nodes.size());

		NetworkMain.propogateAsNetwork(decisionNet);






	}


	//Read and translate output layer after propagation.
	//Translate each of the 256 output nodes into a move.
	public AiMove readOutputLayer(ClientBoard b) {
		int nodeCounter = 0;
		int recordNode = -1;
		int pieceX = 0;
		int pieceY = 0;
		int goalX = 0;
		int goalY = 0;

		//System.out.println("Beginning move generation.");

		for(int i = 0; i <= 3; i++) {

			for(int j = 0; j <= 3; j++) {
				for(int k = 0; k <= 3; k++) {
					for(int l = 0; l <= 3; l++) {
						//System.out.println("CHECK NODE " + nodeCounter);
						//System.out.println("Considering piece on X: " + i + " Y: " + j);
						//System.out.println("Considering goal square on X: " + k + " Y: " + l);

						//System.out.println("Check if this move is legal...");
						boolean legal = false;
						if(!b.isSpaceFree(i, j)) {
							//System.out.println("There is a piece on this square.");
							if(b.getPieceAt(i, j).color.toString().equals(color.toString())) {
								//System.out.println("There is a friendly piece on this square.");
								ArrayList<ClientMove> validMoves = b.getPieceAt(i, j).getAllValidMoves(b, this);
								for(ClientMove c : validMoves) {
									//System.out.println("");
									if(c.x == k && c.y == l) {
										legal = true;
									}
								}
							}
						}

						if(legal) {
							if(recordNode == -1) {
								//System.out.println("Setting initial valid node.");
								recordNode = nodeCounter;
								pieceX = i;
								pieceY = j;
								goalX = k;
								goalY = l;								
							}else {
								//System.out.println("Comparing node " + nodeCounter + " to current record node (" + recordNode + ") ...");
								//System.out.println("CURRENT NODE VALUE: " + decisionNet.lastLayer().nodes.get(nodeCounter).value);
								if(decisionNet.lastLayer().nodes.get(nodeCounter).value >= decisionNet.lastLayer().nodes.get(recordNode).value) {
									//System.out.println("This node is more fit! Setting new record node...");
									recordNode = nodeCounter;
									pieceX = i;
									pieceY = j;
									goalX = k;
									goalY = l;
									//return new AiMove(b.getPieceAt(i, j), new ClientMove(k, l, false, false));
								}else {
									//System.out.println("Record node still stands");
								}
							}
						}else {
							//System.out.println("Move is not legal - skip this node.");
						}
						nodeCounter++;
					}
				}
			}
		}
		//if(decisionNet.lastLayer().nodes.get(5).value > 0) {
		//	return true;
		//}
		//System.out.println();
		//System.out.println("Best move provided by node " + recordNode + ":");
		//System.out.println("Move piece at (" + pieceX + ", " + pieceY + ") to square (" + goalX + ", " + goalY + ")");
		//System.out.println();
		return new AiMove(b.getPieceAt(pieceX, pieceY), new ClientMove(goalX, goalY, false, false));
	}

	public void setNetwork(Network n) {
		decisionNet = n;
	}

	public ArrayList<ClientPiece> getActivePieces(ClientBoard board){
		ArrayList<ClientPiece> pieces = new ArrayList<ClientPiece>();
		for(ClientPiece p : board.activePieces) {
			if((p.color == PieceColor.BLACK && color == PlayerColor.BLACK) || 
					(p.color == PieceColor.WHITE && color == PlayerColor.WHITE)) {
				pieces.add(p);
			}
		}
		return pieces;
	}

	public float getFitness() {
		return fitness;
	}

}
