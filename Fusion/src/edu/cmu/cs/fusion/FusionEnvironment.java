package edu.cmu.cs.fusion;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Lambda2;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.InferredRel;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.SubPair;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

public class FusionEnvironment {
	private enum MatchType {
		DEF, POS, NONE
	}

	private RelationshipContext context;
	private BooleanContext bools;
	private AliasContext alias;
	private TypeHierarchy tHierarchy;
	private InferenceEnvironment inference;
	
	private ConsList<Pair<RelationshipPredicate, Substitution>> continuation;
	
	public FusionEnvironment(AliasContext aliasLattice, RelationshipContext relLattice, BooleanContext boolLattice, TypeHierarchy types, InferenceEnvironment inf) {
		context = relLattice;
		alias = aliasLattice;
		bools = boolLattice;
		tHierarchy = types;
		inference = inf;
		continuation = ConsList.empty();
	}

	@Deprecated
	public FusionEnvironment(AliasContext aliasLattice, RelationshipContext relLattice, BooleanContext boolLattice, TypeHierarchy types) {
		context = relLattice;
		alias = aliasLattice;
		bools = boolLattice;
		tHierarchy = types;
		inference = new InferenceEnvironment(null);
		continuation = ConsList.empty();
	}
	
	public RelationshipDelta getInferredDelta(RelationshipPredicate rel, Substitution sub) {
		RelationshipDelta delta;

		//check to see if we've looked for this relationship before with our existing continuation...
		if (alreadyLookingForRel(rel, sub))
			return null;
		
		continuation = continuation.cons(new Pair(rel, sub), continuation);
		
		for (InferredRel inf : inference) {	
			//first, find out what substitutions are needed to make rel. There might 
			//be more than one if more than one effect could be the one to use.
			List<Substitution> subs = inf.canProduce(rel, sub);
			
			for (Substitution partSub : subs) {
				//bind up the rest of the variables that we need
				SubPair pair = this.allValidSubs(partSub, inf.getFreeVars());
				//and then only look at definite ones (we want true/falses, not unknowns!)
				Iterator<Substitution> defSubs = pair.getDefiniteSubstitutions();
				
				while (defSubs.hasNext()) {
					Substitution finalSub = defSubs.next();
					ThreeValue val = inf.getPredicate().getTruth(this, finalSub);
					
					if (val != ThreeValue.TRUE)
						continue;
					
					//ok, see if this conflicts with what we have
					List<RelationshipDelta> eDeltas = new LinkedList<RelationshipDelta>();
					for (Effect effect : inf.getEffects())
						eDeltas.add(effect.makeEffects(this, finalSub));
					delta = RelationshipDelta.join(eDeltas);
					
					//if it doesn't conflict AND it makes some change, return it!
					if (delta.isStrictlyMorePrecise(context)) {
						continuation = continuation.tl();
						return delta;
					}
				}
			}
		}
		continuation = continuation.tl();
		return null;		
	}
	
	private boolean alreadyLookingForRel(RelationshipPredicate rel,
			Substitution sub) {
		for (Pair<RelationshipPredicate, Substitution> pair : continuation) 
			if (matches(pair.fst(), pair.snd(), rel, sub))
				return true;
		return false;
	}
	
	private boolean matches(RelationshipPredicate r1, Substitution s1, RelationshipPredicate r2, Substitution s2) {
		if (!(r1.getRelation().equals(r2.getRelation())))
			return false;
		
		for (int ndx = 0; ndx < r1.getVars().length; ndx++) {
			ObjectLabel o1 = s1.getSub(r1.getVars()[ndx]);
			ObjectLabel o2 = s2.getSub(r2.getVars()[ndx]);
			
			//It's possible that something is null, because it isn't bound to anything in particular
			//In that case, treat it as a match because it could be bound to the thing we have in the
			//other one. Basically, it's a free variable, so continue to treat it like one!
			if (o1 != null && o2 != null && !(o1.equals(o2)))
				return false;
		}
		return true;
	}

	/**
	 * Find the potential substitutions for some bound specification variables.
	 * @param variables The bound variables which we must produce aliasing substitutions for
	 * @param fv The types of the specification variables
	 * @return A pair of all potential substitutions.
	 */
	public SubPair findLabels(ConsList<Binding> variables, FreeVars fv) {
		SubPair baseCase = new SubPair();
		baseCase.addDefiniteSub(new Substitution());
			
		SubPair pair = variables.foldl(findLabelsLambda, new Pair<SubPair, FreeVars>(baseCase, fv)).fst();
//		assert(pair.numberOfSubstitutions() != 0); possible that there is no alias of the right subtype.
		return pair;
	}

	private Lambda2<Binding, Pair<SubPair, FreeVars>, Pair<SubPair, FreeVars>> findLabelsLambda =
	 new Lambda2<Binding, Pair<SubPair, FreeVars>, Pair<SubPair, FreeVars>>(){
		public Pair<SubPair, FreeVars> call(Binding boundVar, Pair<SubPair, FreeVars> restAndVars) {
			FreeVars fv = restAndVars.snd();
			SubPair otherSubs = restAndVars.fst();
			Set<ObjectLabel> labels = alias.getAliases(boundVar.getSource());
			String typeToFind = fv.getType(boundVar.getSpec());
			SubPair subs = generateNewSubPair(boundVar.getSpec(), typeToFind, labels, otherSubs);
			return new Pair<SubPair, FreeVars>(subs, fv);
		}
	};
			

	/**
	 * Find all possible substitutions for the specification variables in fv, with the starting substitutions given
	 * @param existing The existing substitutions, which must correspond to the free variables passed in
	 * @param fv The free variables to get substitutions for
	 * @return A SubPair of possible substutitions, where each sub contains existing and the domain of the sub is equal to the domain of freevars
	 */
	public SubPair allValidSubs(Substitution existing, FreeVars fv) {
		FreeVars actualFreeVars = new FreeVars();
		
		for (SpecVar spec : fv) {
			if (existing.getSub(spec) == null) {
				actualFreeVars = actualFreeVars.addVar(spec, fv.getType(spec));
			}
		}
		
		ConsList<Pair<SpecVar, String>> freeVarList = actualFreeVars.convertToConsList();
		
		SubPair pair = new SubPair();
		pair.addDefiniteSub(existing);
		
		return freeVarList.foldl(validSubsLambda, pair);
	}
	
	private Lambda2<Pair<SpecVar, String>, SubPair, SubPair> validSubsLambda =
	 new Lambda2<Pair<SpecVar, String>, SubPair, SubPair>(){
		public SubPair call(Pair<SpecVar, String> freeVar, SubPair otherPairs) {
			return generateNewSubPair(freeVar.fst(), freeVar.snd(), alias.getAllAliases(), otherPairs);
		}
	};
		
	private SubPair generateNewSubPair(SpecVar spec, String specType, Set<ObjectLabel> aliases, SubPair otherPairs) {
		SubPair newPair = new SubPair();
		
		Iterator<Substitution> itr =  otherPairs.getDefiniteSubstitutions();
		while (itr.hasNext()){
			Substitution tSub = itr.next();	
			for (ObjectLabel label : aliases) {
				MatchType mType = checkTypes(label, specType);
				if (mType == MatchType.DEF) {
					newPair.addDefiniteSub(tSub.addSub(spec, label));
				}
				else if (mType == MatchType.POS){
					newPair.addPossibleSub(tSub.addSub(spec, label));
				}
			}				
		}

		itr =  otherPairs.getPossibleSubstitutions();
		while (itr.hasNext()){
			Substitution uSub = itr.next();
			for (ObjectLabel label : aliases) {
				MatchType mType = checkTypes(label, specType);
				if (mType != MatchType.NONE) {
					newPair.addPossibleSub(uSub.addSub(spec, label));
				}
			}				
		}			
		return newPair;
	}
		
	private MatchType checkTypes(ObjectLabel label, String type) {
		if (tHierarchy.isSubtypeCompatible(label.getType().getQualifiedName(), type)) {
			return MatchType.DEF;
		}
		else if (tHierarchy.existsCommonSubtype(label.getType().getQualifiedName(), type, true, false)) {
			return MatchType.POS;
		}
		else
			return MatchType.NONE;
	}


	public RelationshipContext getContext() {
		return context;
	}
	
	public ThreeValue getBooleanValue(ObjectLabel label) {
		return bools.getBooleanValue(label);
	}

	public boolean isSubtypeCompatible(String subType, String superType) {
		return tHierarchy.isSubtypeCompatible(subType, superType);
	}

	public FusionEnvironment copy(RelationshipContext newContext) {
		FusionEnvironment env = new FusionEnvironment(alias, newContext, bools, tHierarchy, inference);
		env.continuation = continuation;
		return env;
	}
	
	public String printAllAliases() {
		return alias.toString();
	}
}
