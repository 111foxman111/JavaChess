package com.chess.engine.board;

import com.chess.engine.board.Board.Builder;
import com.chess.engine.pieces.Pawn;
import com.chess.engine.pieces.Piece;
import com.chess.engine.pieces.Rook;

public abstract class Move {
	final Board board;
	final Piece movedPiece;
	final int destinationCoordinate;
	private Move(final Board board, final Piece movedPiece, final int destination) {
		this.board = board;
		this.movedPiece = movedPiece;
		this.destinationCoordinate = destination;
	}
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + this.destinationCoordinate;
		result = 31 * result + this.movedPiece.hashCode();
		return result;
	}
	@Override
	public boolean equals(final Object other) {
		if(this == other) {
			return true;
		}
		if(!(other instanceof Move)) {
			return false;
		}
		final Move otherMove = (Move) other;
		return getCurrentCoordinate() == otherMove.getCurrentCoordinate() && getDestinationCoordinate() == otherMove.getDestinationCoordinate() && getMovedPiece().equals(otherMove.getMovedPiece());
	}
	public int getCurrentCoordinate() {
		return this.getMovedPiece().getPiecePosition();
	}
	public Board execute() {
		final Builder builder = new Builder();
		for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
			if(!this.movedPiece.equals(piece)) {
				builder.setPiece(piece);
			}
		}
		for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
			builder.setPiece(piece);
		}
		builder.setPiece(this.movedPiece.movePiece(this));
		builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
		return builder.build();
	}
	public int getDestinationCoordinate() {
		return this.destinationCoordinate;
	}
	public Piece getMovedPiece() {
		return this.movedPiece;
	}
	public boolean isAttack() {
		return false;
	}
	public boolean isCastlingMove() {
		return false;
	}
	public Piece getAttackedPiece() {
		return null;
	}
	public static final class MajorMove extends Move {
		public MajorMove(Board board, Piece movedPiece, int destination) {
			super(board, movedPiece, destination);
		}
	}
	public static class AttackMove extends Move {
		final Piece attackedPiece;
		public AttackMove(Board board, Piece movedPiece, int destination, final Piece attackedPiece) {
			super(board, movedPiece, destination);
			this.attackedPiece = attackedPiece;
		}
		@Override
		public int hashCode() {
			return this.attackedPiece.hashCode() + super.hashCode();
		}
		@Override
		public boolean equals(final Object other) {
			if(this == other) {
				return true;
			}
			if(!(other instanceof AttackMove)) {
				return false;
			}
			final AttackMove otherAttackMove = (AttackMove) other;
			return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
		}
		@Override
		public boolean isAttack() {
			return true;
		}
		@Override
		public Board execute() {
			return null;
		}
		@Override
		public Piece getAttackedPiece() {
			return this.attackedPiece;
		}
	}
	public static final class PawnMove extends Move {
		public PawnMove(Board board, Piece movedPiece, int destination) {
			super(board, movedPiece, destination);
		}
	}
	public static final class PawnAttackMove extends AttackMove {
		public PawnAttackMove(Board board, Piece movedPiece, int destination, final Piece attackedPiece) {
			super(board, movedPiece, destination,attackedPiece);
		}
	}
	public static final class PawnEnPassantAttackMove extends AttackMove {
		public PawnEnPassantAttackMove(Board board, Piece movedPiece, int destination, final Piece attackedPiece) {
			super(board, movedPiece, destination,attackedPiece);
		}
	}
	public static final class PawnJump extends Move {
		public PawnJump(Board board, Piece movedPiece, int destination) {
			super(board, movedPiece, destination);
		}
		@Override
        public Board execute() {
			final Builder builder = new Builder();
			for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
				if(!this.movedPiece.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
				builder.setPiece(piece);
			}
			final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
			builder.setPiece(movedPawn);
			builder.setEnPassantPawn(movedPawn);
			builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
			return builder.build();
        }
	}
	static abstract class CastleMove extends Move {
		protected final Rook castleRook;
		protected final int castleRookStart;
		protected final int castleRookDestination;
		public CastleMove(Board board, Piece movedPiece, int destination, final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
			super(board, movedPiece, destination);
			this.castleRook = castleRook;
			this.castleRookStart = castleRookStart;
			this.castleRookDestination = castleRookDestination;
		}
		public Rook getCastleRook() {
			return this.castleRook;
		}
		@Override
		public boolean isCastlingMove() {
			return true;
		}
		@Override
		public Board execute() {
			final Builder builder = new Builder();
			for(final Piece piece : this.board.currentPlayer().getActivePieces()) {
				if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
				builder.setPiece(piece);
			}
			builder.setPiece(this.movedPiece.movePiece(this));
			builder.setPiece(new Rook(this.castleRook.getPieceAlliance(), this.castleRookDestination));
			builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
			return builder.build();
		}
	}
	public static final class KingSideCastleMove extends CastleMove {
		public KingSideCastleMove(Board board, Piece movedPiece, int destination, final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
			super(board, movedPiece, destination, castleRook, castleRookStart, castleRookDestination);
		}
		@Override
		public String toString() {
			return "O-O";
		}
	}
	public static final class QueenSideCastleMove extends CastleMove {
		public QueenSideCastleMove(Board board, Piece movedPiece, int destination, final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
			super(board, movedPiece, destination, castleRook, castleRookStart, castleRookDestination);
		}
		@Override
		public String toString() {
			return "O-O-O";
		}
	}
	public static final class NullMove extends Move {
		public NullMove() {
			super(null,null,-1);
		}
		@Override
		public Board execute() {
			throw new RuntimeException("Cannot Execute Null Move");
		}
	}
	public static class MoveFactory {
		 private static final Move NULL_MOVE = new NullMove();
        private MoveFactory() {
            throw new RuntimeException("Not instantiatable!");
        }

        public static Move createMove(final Board board,
                                      final int currentCoordinate,
                                      final int destinationCoordinate) {
            for (final Move move : board.getAllLegalMoves()) {
                if (move.getCurrentCoordinate() == currentCoordinate &&
                    move.getDestinationCoordinate() == destinationCoordinate) {
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}