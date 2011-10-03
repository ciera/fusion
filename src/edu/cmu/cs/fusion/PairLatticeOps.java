package edu.cmu.cs.fusion;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.util.Pair;

public class PairLatticeOps<A,B> implements ILatticeOperations<Pair<A,B>> {
	ILatticeOperations<A> aOps;
	ILatticeOperations<B> bOps;
	
	public PairLatticeOps(ILatticeOperations<A> aOps, ILatticeOperations<B> bOps) {
		this.aOps = aOps;
		this.bOps = bOps;
	}

	public boolean atLeastAsPrecise(Pair<A, B> info, Pair<A, B> reference, ASTNode node) {
		return aOps.atLeastAsPrecise(info.fst(), reference.fst(), node) && bOps.atLeastAsPrecise(info.snd(), reference.snd(), node);
	}

	public Pair<A, B> bottom() {
		return new Pair<A, B>(aOps.bottom(), bOps.bottom());
	}

	public Pair<A, B> copy(Pair<A, B> original) {
		return new Pair<A, B>(aOps.copy(original.fst()), bOps.copy(original.snd()));
	}

	public Pair<A, B> join(Pair<A, B> someInfo, Pair<A, B> otherInfo, ASTNode node) {
		return new Pair<A, B>(aOps.join(someInfo.fst(), otherInfo.fst(), node), bOps.join(someInfo.snd(), otherInfo.snd(), node));
	}
}
