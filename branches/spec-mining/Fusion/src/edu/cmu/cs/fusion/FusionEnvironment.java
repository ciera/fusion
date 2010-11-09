package edu.cmu.cs.fusion;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Lambda2;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.crystal.util.TypeHierarchy;
import edu.cmu.cs.fusion.alias.AliasContext;
import edu.cmu.cs.fusion.alias.AliasDelta;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.Effect;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.InferenceEnvironment;
import edu.cmu.cs.fusion.constraint.InferredRel;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.relationship.RelationshipContext;
import edu.cmu.cs.fusion.relationship.RelationshipDelta;

public class FusionEnvironment<AC extends AliasContext> {
	private enum MatchType {
		DEF, POS, NONE
	}

	private RelationshipContext context;
	private BooleanContext bools;
	private AC alias;
	private TypeHierarchy tHierarchy;
	private InferenceEnvironment inference;
	
	private ConsList<Pair<RelationshipPredicate, Substitution>> continuation;
	private Variant variant;
	
	public FusionEnvironment(AC aliasLattice, RelationshipContext relLattice, BooleanContext boolLattice, TypeHierarchy types, InferenceEnvironment inf, Variant variant) {
		context = relLattice;
		alias = aliasLattice;
		bools = boolLattice;
		tHierarchy = types;
		inference = inf;
		continuation = ConsList.empty();
		this.variant = variant;
	}

	@Deprecated
	public FusionEnvironment(AC aliasLattice, RelationshipContext relLattice, BooleanContext boolLattice, TypeHierarchy types) {
		context = relLattice;
		alias = aliasLattice;
		bools = boolLattice;
		tHierarchy = types;
		inference = new InferenceEnvironment(null);
		continuation = ConsList.empty();
	}
	
/*
 	public RelationshipDelta getInferredDelta(RelationshipPredicate rel, Substitution sub) {
		RelationshipDelta delta;
		
		if (variant != Variant.PRAGMATIC_VARIANT)
			return null;

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
					delta = new RelationshipDelta();
					for (Effect effect : inf.getEffects())
						delta.override(effect.makeEffects(this, finalSub));
					
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
*/

	public RelationshipDelta getInferredDelta(RelationshipPredicate rel, Substitution sub) {
		RelationshipDelta delta;
		
		if (variant != Variant.PRAGMATIC_VARIANT)
			return null;

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
				List<Substitution> finalSubs = this.allValidSubs(partSub, inf.getFreeVars());
				//and then only look at definite ones (we want true/falses, not unknowns!)
				
				for (Substitution finalSub : finalSubs) {
					ThreeValue val = inf.getPredicate().getTruth(this, finalSub);
					
					if (val != ThreeValue.TRUE)
						continue;
					
					//ok, see if this conflicts with what we have
					delta = new RelationshipDelta();
					for (Effect effect : inf.getEffects())
						delta.override(effect.makeEffects(this, finalSub));
					
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
/*	
	public SubPair findLabels(ConsList<Binding> variables, FreeVars fv) {
		SubPair baseCase = new SubPair();
		baseCase.addDefiniteSub(new Substitution());
		
		//first, get a list of sigmas, with bindings according to variables.
		SubPair boundSubs = variables.foldl(findLabelsLambda, new Pair<SubPair, FreeVars>(baseCase, fv)).fst();
		
		//then, pass each one into allValidSubs
		SubPair includeFV = new SubPair();
		Iterator<Substitution> boundItr = boundSubs.getDefiniteSubstitutions();
		while (boundItr.hasNext()) {
			Substitution sub = boundItr.next();
			SubPair pair = allValidSubs(sub, fv);

			Iterator<Substitution> itr = pair.getDefiniteSubstitutions();
			while (itr.hasNext())
				includeFV.addDefiniteSub(itr.next());
			
			itr = pair.getPossibleSubstitutions();
			while (itr.hasNext())
				includeFV.addPossibleSub(itr.next());
		}
		
		boundItr = boundSubs.getPossibleSubstitutions();
		while (boundItr.hasNext()) {
			Substitution sub = boundItr.next();
			SubPair pair = allValidSubs(sub, fv);

			Iterator<Substitution> itr = pair.getDefiniteSubstitutions();
			assert (!itr.hasNext());
			
			itr = pair.getPossibleSubstitutions();
			while (itr.hasNext())
				includeFV.addPossibleSub(itr.next());
		}
				
		return includeFV;
	}

	private Lambda2<Binding, Pair<SubPair, FreeVars>, Pair<SubPair, FreeVars>> findLabelsLambda =
		 new Lambda2<Binding, Pair<SubPair, FreeVars>, Pair<SubPair, FreeVars>>(){
			public Pair<SubPair, FreeVars> call(Binding boundVar, Pair<SubPair, FreeVars> restAndVars) {
				FreeVars fv = restAndVars.snd();
				SubPair otherSubs = restAndVars.fst();
				Set<ObjectLabel> labels = alias.getAliases(boundVar.getSource());
				SpecVar spec = boundVar.getSpec();
				String specType = fv.getType(spec);
						
				SubPair newPair = new SubPair();
				
				Iterator<Substitution> itr = otherSubs.getDefiniteSubstitutions();
				while (itr.hasNext()) {
					Substitution sub = itr.next();
					for (ObjectLabel label : labels) {
						MatchType mType = checkTypes(label, specType);
						if (mType == MatchType.DEF) {
							newPair.addDefiniteSub(sub.addSub(spec, label));
						}
						else if (mType == MatchType.DEF) {
							newPair.addPossibleSub(sub.addSub(spec, label));
						}
					}				
				}

				itr = otherSubs.getPossibleSubstitutions();
				while (itr.hasNext()) {
					Substitution sub = itr.next();
					for (ObjectLabel label : labels) {
						MatchType mType = checkTypes(label, specType);
						if (mType != MatchType.NONE) {
							newPair.addPossibleSub(sub.addSub(spec, label));
						}
					}				
				}
				return new Pair<SubPair, FreeVars>(newPair, fv);
			}
		};
		
*/
		public List<Substitution> findLabels(ConsList<Binding> variables, FreeVars fv) {
			List<Substitution> baseCase = new LinkedList<Substitution>();
			baseCase.add(new Substitution());
			
			//first, get a list of sigmas, with bindings according to variables.
			List<Substitution> boundSubs = variables.foldl(findLabelsLambda, new Pair<List<Substitution>, FreeVars>(baseCase, fv)).fst();
			
			//then, pass each one into allValidSubs
			List<Substitution> includeFV = new LinkedList<Substitution>();
			for (Substitution sub : boundSubs) {
				List<Substitution> restSubs = allValidSubs(sub, fv);

				for (Substitution finalSub : restSubs) {
					includeFV.add(finalSub);
				}
			}
					
			return includeFV;
		}
		
		private Lambda2<Binding, Pair<List<Substitution>, FreeVars>, Pair<List<Substitution>, FreeVars>> findLabelsLambda =
			 new Lambda2<Binding, Pair<List<Substitution>, FreeVars>, Pair<List<Substitution>, FreeVars>>(){
				public Pair<List<Substitution>, FreeVars> call(Binding boundVar, Pair<List<Substitution>, FreeVars> restAndVars) {
					FreeVars fv = restAndVars.snd();
					List<Substitution> otherSubs = restAndVars.fst();
					Set<ObjectLabel> labels = alias.getAliases(boundVar.getSource());
					SpecVar spec = boundVar.getSpec();
					String specType = fv.getType(spec);
							
					List<Substitution> newPair = new LinkedList<Substitution>();
					
					for (Substitution sub : otherSubs) {
						for (ObjectLabel label : labels) {
							MatchType mType = checkTypes(label, specType);
							if (mType != MatchType.NONE) {
								newPair.add(sub.addSub(spec, label));
							}
						}				
					}
					return new Pair<List<Substitution>, FreeVars>(newPair, fv);
				}
			};
				

	/**
	 * Find all possible substitutions for the specification variables in fv, with the starting substitutions given
	 * @param existing The existing substitutions, which must correspond to the free variables passed in
	 * @param fv The free variables to get substitutions for
	 * @return A SubPair of possible substitutions, where each sub contains existing and the domain of the sub is equal to the domain of freevars
	 */
/*	public SubPair allValidSubs(Substitution existing, FreeVars fv) {
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
			SpecVar spec = freeVar.fst();
			String specType = freeVar.snd();
			Set<ObjectLabel> aliases = alias.getAllAliases();
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
	};
*/

	public List<Substitution> allValidSubs(Substitution existing, FreeVars fv) {
		FreeVars actualFreeVars = new FreeVars();
		
		for (SpecVar spec : fv) {
			if (existing.getSub(spec) == null) {
				actualFreeVars = actualFreeVars.addVar(spec, fv.getType(spec));
			}
		}
		
		ConsList<Pair<SpecVar, String>> freeVarList = actualFreeVars.convertToConsList();
		
		List<Substitution> pair = new LinkedList<Substitution>();
		pair.add(existing);
		
		return freeVarList.foldl(validSubsLambda, pair);
	}
	
	private Lambda2<Pair<SpecVar, String>, List<Substitution>, List<Substitution>> validSubsLambda =
	 new Lambda2<Pair<SpecVar, String>, List<Substitution>, List<Substitution>>(){
		public List<Substitution> call(Pair<SpecVar, String> freeVar, List<Substitution> otherPairs) {
			SpecVar spec = freeVar.fst();
			String specType = freeVar.snd();
			Set<ObjectLabel> aliases = alias.getAllAliases();
			List<Substitution> newPair = new LinkedList<Substitution>();
		
			for (Substitution sub : otherPairs){
				for (ObjectLabel label : aliases) {
					MatchType mType = checkTypes(label, specType);
					if (mType != MatchType.NONE) {
						newPair.add(sub.addSub(spec, label));
					}
				}				
			}			
			return newPair;

		}
	};

	
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

	//TODO: Consider removing this method. Replace with utility methods?
	public RelationshipContext getContext() {
		return context;
	}
	
	public ThreeValue getBooleanValue(ObjectLabel label) {
		return bools.getBooleanValue(label);
	}

	public boolean isSubtypeCompatible(String subType, String superType) {
		return tHierarchy.isSubtypeCompatible(subType, superType);
	}

	public boolean existsPossibleSubtype(String subType, String superType) {
		return tHierarchy.existsCommonSubtype(subType, superType);
	}

	public FusionEnvironment<AC> copy(RelationshipContext newContext) {
		FusionEnvironment<AC> env = new FusionEnvironment<AC>(alias, newContext, bools, tHierarchy, inference, variant);
		env.continuation = continuation;
		return env;
	}
	
	public String printAllAliases() {
		return alias.toString();
	}

	public AC makeNewAliases(AliasDelta aliasDelta) {
		AC newContext = (AC) alias.clone();
		
		for (Variable var : aliasDelta) {
			Set<ObjectLabel> labels = aliasDelta.getChanges(var);
			if (labels != null) {
				assert(newContext.getAliases(var).containsAll(labels));
				newContext.reset(var, labels);
			}
		}
		newContext.cleanPotentialLabels();
		
		return newContext;
	}
}