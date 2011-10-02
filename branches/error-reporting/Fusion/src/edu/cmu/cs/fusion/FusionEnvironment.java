package edu.cmu.cs.fusion;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.cmu.cs.crystal.tac.model.SourceVariable;
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
import edu.cmu.cs.fusion.constraint.predicates.PredicateSatMap;
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
	private PredicateSatMap overrideMap;
	
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

	public RelationshipDelta getInferredDelta(RelationshipPredicate rel, Substitution sub) {
		RelationshipDelta delta;
		
		if (variant != Variant.PRAGMATIC_VARIANT)
			return null;

		//check to see if we've looked for this relationship before with our existing continuation...
		if (alreadyLookingForRel(rel, sub))
			return null;
		
		continuation = ConsList.cons(new Pair<RelationshipPredicate, Substitution>(rel, sub), continuation);
		
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
							
					List<Substitution> newSubs = new LinkedList<Substitution>();
					assert labels != null : "labels was null for the variable " + boundVar.getSource().getSourceString();					
					for (Substitution sub : otherSubs) {
						for (ObjectLabel label : labels) {
							MatchType mType = checkTypes(label, specType);
							if (mType != MatchType.NONE) {
								newSubs.add(sub.addSub(spec, label));
							}
						}				
					}
					return new Pair<List<Substitution>, FreeVars>(newSubs, fv);
				}
			};
				


	public List<Substitution> allValidSubs(Substitution existing, FreeVars fv) {
		FreeVars actualFreeVars = new FreeVars();
		
		for (SpecVar spec : fv) {
			if (existing.getSub(spec) == null) {
				actualFreeVars = actualFreeVars.addVar(spec, fv.getType(spec));
			}
		}
		
		ConsList<Pair<SpecVar, String>> freeVarList = actualFreeVars.convertToConsList();
		
		List<Substitution> subs = new LinkedList<Substitution>();
		subs.add(existing);
		
		return freeVarList.foldl(validSubsLambda, subs);
	}
	
	private Lambda2<Pair<SpecVar, String>, List<Substitution>, List<Substitution>> validSubsLambda =
	 new Lambda2<Pair<SpecVar, String>, List<Substitution>, List<Substitution>>(){
		public List<Substitution> call(Pair<SpecVar, String> freeVar, List<Substitution> otherPairs) {
			SpecVar spec = freeVar.fst();
			String specType = freeVar.snd();
			Set<ObjectLabel> aliases = alias.getAllAliases();
			List<Substitution> newSubs = new LinkedList<Substitution>();
		
			assert(aliases != null);
			for (Substitution sub : otherPairs){
				for (ObjectLabel label : aliases) {
					MatchType mType = checkTypes(label, specType);
					if (mType != MatchType.NONE) {
						newSubs.add(sub.addSub(spec, label));
					}
				}				
			}			
			return newSubs;

		}
	};

	public PredicateSatMap getOverrideMap()
	{
		return overrideMap;
	}
	public void setOverrideMap(PredicateSatMap map)
	{
		this.overrideMap = map;
	}
	private MatchType checkTypes(ObjectLabel label, String type) {
		if (tHierarchy.isSubtypeCompatible(label.getTypeName(), type)) {
			return MatchType.DEF;
		}
		else if (tHierarchy.existsCommonSubtype(label.getTypeName(), type, true, false)) {
			return MatchType.POS;
		}
		else
			return MatchType.NONE;
	}

	//TODO: Consider removing this method. Replace with utility methods?
	@Deprecated
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
	/**
	 * 
	 * @param lab
	 * @return (x_1 ... x_n) such that this.alias.getAliases(x_i).contains(lab)
	 */
	public List<Variable> getPreImageVars(ObjectLabel lab)
	{
		List<Variable> vars = new LinkedList<Variable>();
		if(lab!=null)//?
		for(Variable var: this.alias.getVariables())
		{
			if (this.alias.getAliases(var).contains(lab))
				vars.add(var);
		}
		return vars;
	}
	/**
	 * 
	 * @param sub
	 * @param specVars
	 * @return getSourceVars(sub,specVars)[i] == 
	 * first source variable which is in the preimage of 
	 * the object label obtained after substituting specVars[i]
	 * on sub, if it exists, otherwise any variable in the same preimage 
	 */
	public Variable[] getSourceVars(Substitution sub, SpecVar ...specVars)
	{		
		Variable[] sourceVars = new Variable[specVars.length];
		for (int i = 0; i < specVars.length; i++) {
			
			ObjectLabel lab = sub.getSub(specVars[i]);
			List<Variable> vars = this.getPreImageVars(lab);
			for(Variable var: vars)
			{
				if(var instanceof SourceVariable)
					{
						sourceVars[i] = var;
						break;//possibly ignoring other source variables
					}
			}
			
			if(sourceVars[i]==null && vars.size()>0)
				sourceVars[i] = vars.get(0);//non source variable
			assert(sourceVars[i]!=null||vars.size()==0);
		}
		return sourceVars;
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
