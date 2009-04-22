package edu.cmu.cs.fusion;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.cmu.cs.crystal.analysis.alias.Aliasing;
import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;
import edu.cmu.cs.crystal.analysis.constant.BooleanConstantLE;
import edu.cmu.cs.crystal.tac.Variable;
import edu.cmu.cs.crystal.util.ConsList;
import edu.cmu.cs.crystal.util.Lambda2;
import edu.cmu.cs.crystal.util.Pair;
import edu.cmu.cs.fusion.constraint.FreeVars;
import edu.cmu.cs.fusion.constraint.SpecVar;
import edu.cmu.cs.fusion.constraint.SubPair;
import edu.cmu.cs.fusion.constraint.Substitution;
import edu.cmu.cs.fusion.relationship.RelationshipContext;

public class FusionEnvironment {
	private enum MatchType {
		DEF, POS, NONE
	}

	RelationshipContext context;
	AliasContext alias;
	
	public FusionEnvironment(AliasContext aliasLattice, RelationshipContext relLattice, BooleanConstantLE boolLattice) {
		context = relLattice;
		alias = aliasLattice;
	}
	
	public SubPair findLabels(ConsList<Pair<SpecVar, Variable>> variables, FreeVars fv) {
		SubPair baseCase = new SubPair();
		baseCase.addDefiniteSub(new Substitution());
			
		return variables.foldl(findLabelsLambda, new Pair<SubPair, FreeVars>(baseCase, fv)).fst();
	}

	private Lambda2<Pair<SpecVar, Variable>, Pair<SubPair, FreeVars>, Pair<SubPair, FreeVars>> findLabelsLambda =
	 new Lambda2<Pair<SpecVar, Variable>, Pair<SubPair, FreeVars>, Pair<SubPair, FreeVars>>(){
		public Pair<SubPair, FreeVars> call(Pair<SpecVar, Variable> boundVar, Pair<SubPair, FreeVars> restAndVars) {
			FreeVars fv = restAndVars.snd();
			SubPair otherSubs = restAndVars.fst();
			SubPair subs = generateNewSubPair(boundVar.fst(), fv.getType(boundVar.fst()), alias.getAliases(boundVar.snd()), otherSubs);
			return new Pair<SubPair, FreeVars>(subs, fv);
		}
	};
			

	/**
	 * 
	 * @param subs
	 * @param fv
	 * @return
	 */
	public SubPair allValidSubs(Substitution existing, FreeVars fv) {
		FreeVars actualFreeVars = new FreeVars();
		
		for (SpecVar spec : fv) {
			if (existing.getSub(spec) != null) {
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
		if (Utils.isSubtypeCompatible(label.getType().getQualifiedName(), type)) {
			return MatchType.DEF;
		}
		else if (Utils.existsCommonSubtype(label.getType().getQualifiedName(), type)) {
			return MatchType.POS;
		}
		else
			return MatchType.NONE;
	}


	public RelationshipContext getContext() {
		return context;
	}
	
	public ThreeValue getBooleanValue(ObjectLabel label) {
		return null;
	}

	public String getType(ObjectLabel obj) {
		return null;
	}
}