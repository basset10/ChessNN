package chess.common.packet;

import java.io.Serializable;

public class PacketClientMove implements Serializable{

	private static final long serialVersionUID = -5510632217376022223L;
	
	public static final int PAWN_PROMOTION_FALSE = 0;
	public static final int PAWN_PROMOTION_QUEEN = 1;
	public static final int PAWN_PROMOTION_ROOK = 2;
	public static final int PAWN_PROMOTION_KNIGHT = 3;
	public static final int PAWN_PROMOTION_BISHOP = 4;
	
	public int existingPieceX;
	public int existingPieceY;	
	public int intendedMoveX;
	public int intendedMoveY;
	public boolean castled;
	public boolean enPassant;
	public int promotionType;
	
	public String id;
	
	public PacketClientMove(int existingPieceXArg, int existingPieceYArg, int intendedMoveXArg, int intendedMoveYArg,
			String idArg, boolean castledArg, boolean enPassantArg, int promotionTypeArg) {
		existingPieceX = existingPieceXArg;
		existingPieceY = existingPieceYArg;
		intendedMoveX = intendedMoveXArg;
		intendedMoveY = intendedMoveYArg;
		id = idArg;
		castled = castledArg;
		enPassant = enPassantArg;
		promotionType = promotionTypeArg;
	}
	
}
