package com.chess.engine.pieces;

import java.util.Collection;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

public abstract class Piece 
{
	final PieceType pieceType;
	protected final int piecePosition;
	protected final Alliance pieceAlliance;
	protected final boolean isFirstMove;
	private final int cachedHashCode;
	Piece(final PieceType type, final int piecePosition, final Alliance pieceAlliance) {
		this.pieceType = type;
		this.pieceAlliance = pieceAlliance;
		this.piecePosition = piecePosition;
		this.isFirstMove = false;
		this.cachedHashCode = computeHashCode();
	}
	private int computeHashCode() {
		int result = pieceType.hashCode();
		result = 31 * result + pieceAlliance.hashCode();
		result = 31 * result + piecePosition;
		result = 31 * result + (isFirstMove ? 1 : 0);
		return result;
	}
	@Override
	public boolean equals(final Object other) {
		if(this == other) {
			return true;
		}
		if(!(other instanceof Piece)) {
			return false;
		}
		final Piece otherPiece = (Piece) other;
		return piecePosition == otherPiece.getPiecePosition() && pieceType == otherPiece.getPieceType() && pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
	}
	@Override
	public int hashCode() {
		return this.cachedHashCode;
	}
	public PieceType getPieceType() {
        return this.pieceType;
    }
	public int getPiecePosition() {
		return this.piecePosition;
	}
	public Alliance getPieceAlliance() {
		return this.pieceAlliance;
	}
	public boolean isFirstMove() {
		return this.isFirstMove;
	}
	public abstract Collection<Move> calculateLegalMoves(final Board board);
	public abstract Piece movePiece(Move move);
	public enum PieceType {
		PAWN("P") {
			@Override
			public boolean isRook() {
				return false;
			}
			@Override
			public boolean isKing() {
				return false;
			}
		},
		KNIGHT("N") {
			@Override
			public boolean isRook() {
				return false;
			}
			@Override
			public boolean isKing() {
				return false;
			}
		},
		BISHOP("B") {
			@Override
			public boolean isRook() {
				return false;
			}
			@Override
			public boolean isKing() {
				return false;
			}
		},
		ROOK("R") {
			@Override
			public boolean isRook() {
				return true;
			}
			@Override
			public boolean isKing() {
				return false;
			}
		},
		QUEEN("Q") {
			@Override
			public boolean isRook() {
				return false;
			}
			@Override
			public boolean isKing() {
				return false;
			}
		},
		KING("K") {
			@Override
			public boolean isRook() {
				return false;
			}
			@Override
			public boolean isKing() {
				return true;
			}
		};
		private String pieceName;
		PieceType(final String pieceName) {
			this.pieceName = pieceName;
		}
		@Override
		public String toString() {
			return this.pieceName;
		}
		public abstract boolean isRook();
		public abstract boolean isKing();
	}
}