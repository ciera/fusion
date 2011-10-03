package edu.cmu.cs.fusion.constraint;



import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ThisExpression;
import edu.cmu.cs.crystal.tac.model.KeywordVariable;
import edu.cmu.cs.crystal.tac.model.SourceVariable;
import edu.cmu.cs.crystal.tac.model.TempVariable;
import edu.cmu.cs.crystal.tac.model.ThisVariable;
import edu.cmu.cs.crystal.tac.model.Variable;
import edu.cmu.cs.fusion.FusionEnvironment;
import edu.cmu.cs.fusion.Relation;
import edu.cmu.cs.fusion.ThreeValue;
import edu.cmu.cs.fusion.alias.ObjectLabel;
import edu.cmu.cs.fusion.constraint.Move.MoveType;
import edu.cmu.cs.fusion.constraint.operations.MethodInvocationOp;
import edu.cmu.cs.fusion.constraint.predicates.AtomicPredicate;
import edu.cmu.cs.fusion.constraint.predicates.PredicateSatMap;
import edu.cmu.cs.fusion.constraint.predicates.RelationshipPredicate;
import edu.cmu.cs.fusion.relationship.FusionErrorReport;

public class SuggestionGenerator {
	
	ConstraintEnvironment cons;
	FusionEnvironment fe;
	HashMap<ObjectLabel,List<Variable>> preimageCache;
	HashMap<Substitution,HashMap<PredicateSatMap,List<MoveBundle>>> cache;
	

	public void compute(FusionErrorReport err,ConstraintEnvironment cons,ASTNode node)
	{
		fe = err.getFailingEnvironment();
		this.cons = cons;
		preimageCache = new HashMap<ObjectLabel, List<Variable>>();//independent of substitutions
		cache = new HashMap<Substitution, HashMap<PredicateSatMap,List<MoveBundle>>>();
		for(Substitution sub: err.getFailingVars())
		{
			List<PredicateSatMap> maps = err.getMaps(sub);
			cache.put(sub, getFixes(sub,maps,node));
		}
		System.out.println();
	}	
	public List<MoveBundle> getFixes(Substitution sub,PredicateSatMap map)
	{
		return cache.get(sub).get(map);
	}
	private  HashMap< PredicateSatMap,List<MoveBundle> > getFixes(Substitution sub, List<PredicateSatMap> maps, ASTNode node)
	{
		HashMap< PredicateSatMap,List<MoveBundle> > mapFixes = new HashMap<PredicateSatMap, List<MoveBundle> >();
		HashMap< Entry<AtomicPredicate,ThreeValue>,List<Move> > fixCache = new HashMap<Entry<AtomicPredicate,ThreeValue>, List<Move>>();
		
		for(PredicateSatMap map: maps)
		{
			List<MoveBundle> bundles  = new LinkedList<MoveBundle>();			
			for(Entry<AtomicPredicate, ThreeValue> entry: map.getMap().entrySet())
			{
				if(! fixCache.containsKey(entry))
				{
					List<Move> fixes = getFixes(sub,entry,node);
					fixCache.put(entry, fixes);
				}				
			}
			bundles = new CrossProductMove(fixCache, map, node).cross();
			mapFixes.put(map, bundles);
			//cross product. use cons list?
		}
		return mapFixes;		
	}

	private List<Move> getFixes(Substitution subOrig, Entry<AtomicPredicate, ThreeValue> entry, ASTNode node) {
		if(entry.getKey() instanceof RelationshipPredicate && entry.getValue()!=ThreeValue.UNKNOWN)
		{
			RelationshipPredicate relP = ((RelationshipPredicate)entry.getKey());
			List<Move> fixes = new LinkedList<Move>();
			for(Constraint con: cons)
			{
				nextCon:
				for(Effect eff: con.getEffects())
				{
					if(eff instanceof RelEffect)
					{
						RelEffect relEff = (RelEffect)eff;
						Relation rel = relEff.getRelation();
						if(rel.equals(relP.getRelation()) && con.getOp() instanceof MethodInvocationOp)//can include constructor.  
						{
							Move.MoveType type = null;
							if(relEff.getType().isTest())
							{
								type = Move.MoveType.MAKEBRANCH;
							}
							else if ((relEff.getType() == RelEffect.EffectType.ADD) == (entry.getValue() == ThreeValue.TRUE)
									^!relP.isPositive())//value can not be unknown here 
							{
								type = Move.MoveType.INSERTEXPRESSION;
							}
							
							if (type!=null)
							{
								MethodInvocationOp methodOp = (MethodInvocationOp)con.getOp();
								Substitution methodSub = new Substitution();
								SpecVar[] relVars = relP.getVars(), relEffVars = relEff.getVars();
								for(int i=0;i<relVars.length;i++)
								{
									methodSub = methodSub.addSub(relEffVars[i], subOrig.getSub(relVars[i]));
								}
								List<Substitution> validSubs;
								FreeVars methodOpFreeVars = methodOp.getFreeVariables(); 
								SpecVar[] relevant = new SpecVar[methodOpFreeVars.size()];
								 
								{
									int i=0, outsideDomain=0;
									for(SpecVar specVar: methodOpFreeVars)
									{
										relevant[i++]=specVar;
										if(methodSub.getSub(specVar)==null)
											outsideDomain++;
									}
									//restrict to changes we care about
									if(outsideDomain>0)
									{
										validSubs= fe.allValidSubs(methodSub, methodOp.getFreeVariables());//con.getFree or methodOp.getFree?
										validSubs = Substitution.collapseDomain(validSubs, relevant);
									}
									else
									{
										//no missing subs, no need to get allValidSubs
										validSubs = new LinkedList<Substitution>();
										validSubs.add(methodSub);										
									}
									
								}								
								
								ObjectLabel[] methodLabels = new ObjectLabel[relevant.length];
								if(validSubs.isEmpty())continue;
								assert(validSubs.isEmpty()==false);
								for(Substitution sub: validSubs)
								{
									for(int i=0;i<relevant.length;i++)
									{
										ObjectLabel lab = sub.getSub(relevant[i]);
										methodLabels[i] = lab;
										if (preimageCache.containsKey(lab)==false)
										{
											preimageCache.put(lab, fe.getPreImageVars(lab));//may include any kind of variable
										}										
									}
									//methodLabels is now full
									List<ASTNode> concreteMethods = new CrossProductVariable(preimageCache, methodLabels, methodOp, node).cross();
									for(ASTNode concMethod: concreteMethods)
									{
										if(type==Move.MoveType.MAKEBRANCH && !relP.isPositive())//need to negate exp
										{
											PrefixExpression booleanExp = node.getAST().newPrefixExpression();
											booleanExp.setOperand((Expression)concMethod);//concMethod is MethodInvocation :>Expression
											booleanExp.setOperator(PrefixExpression.Operator.NOT);
											concMethod = booleanExp;
										}
										fixes.add(new Move(concMethod,type));
									}
								}								
							}
							continue nextCon;//doesn't make sense to have another test/add for the same relation within the same constraint.
						}
					}
				}
			}			
			fixes.add(new Move(node,MoveType.REMOVENODE));
			fixes.add(Move.IDENTITY);
			return fixes;
		}
		return null;
	}
	
	private class CrossProductVariable
	{
		List<ASTNode> cum;
		ObjectLabel[] orderedMethodLabels;
		MethodInvocationOp method;
		ASTNode failing;
		
		public CrossProductVariable(HashMap<ObjectLabel, List<Variable>> preimageCache, ObjectLabel[] orderedMethodLabels, MethodInvocationOp method, ASTNode failing) {
			super();
			this.orderedMethodLabels = orderedMethodLabels;
			this.method = method;
			this.failing = failing;
			cum = new LinkedList<ASTNode>();
		}
		public List<ASTNode> cross()
		{
			recurr(0, new Variable[orderedMethodLabels.length]);//imperative stuff
			return cum;
		}
		private void recurr(int i, Variable[] partial)
		{
			if(i==partial.length)
			{
				ASTNode concreteMethod = makeAST(partial);
				if(concreteMethod!=null)
					cum.add(concreteMethod);
			}			 
			else
			{
				assert(i< orderedMethodLabels.length);
				assert(preimageCache.get(orderedMethodLabels[i])!=null);
				for(Variable var: preimageCache.get(orderedMethodLabels[i]))
				{
					partial[i] = var;
					recurr(i+1,partial);
				}
			}
		}
		private ASTNode makeAST(Variable[] partial)
		{			
			AST failingAST = failing.getAST();
			Expression[] replacements = new Expression[orderedMethodLabels.length];
			for(int i=0;i<replacements.length;i++)
			{
				replacements[i] = SuggestionGenerator.variableToASTExpression(partial[i], failingAST);
				if(replacements[i]==null)return null;
			}
			MethodInvocation methodCallAst = failing.getAST().newMethodInvocation();
			method.fillASTNode(replacements, methodCallAst);
			return methodCallAst;
		}
	}
	public static Expression variableToASTExpression(Variable v, AST ast)
	{
		if(v instanceof TempVariable)
		{
			return (Expression)ASTNode.copySubtree(ast, ((TempVariable)v).getNode());//safe cast?
		}
		else if (v instanceof KeywordVariable)
		{
			KeywordVariable kw = ((KeywordVariable)v);
			if(kw instanceof ThisVariable)//implicit, qualifiers, check this
			{
				ThisExpression thisExp = ast.newThisExpression();
				if(kw.isQualified())
				{
					thisExp.setQualifier(kw.getQualifier());//valid? cycle?
				}
				return thisExp;
			}
			else
			{
				return null;
				//TODO case for "super"
			}
		}
		else if(v instanceof SourceVariable)//add coverage of more types of variables
		{
			return ast.newSimpleName(v.getSourceString());
		}
		else return null;
				
	}
	private class CrossProductMove{
		HashMap< Entry<AtomicPredicate,ThreeValue>,List<Move> > fixCache;
		Entry<AtomicPredicate,ThreeValue>[] domain;
		ASTNode failing;
		public CrossProductMove(HashMap<Entry<AtomicPredicate, ThreeValue>, List<Move>> fixCache, PredicateSatMap map,ASTNode failing) {
			super();
			this.fixCache = fixCache;
			this.domain = new Entry[map.getMap().size()];
			int i=0;
			for(Entry<AtomicPredicate, ThreeValue> entry: map.getMap().entrySet())
			{
				domain[i++] = entry;
			}
			this.failing = failing; 
		}
		List<MoveBundle> cum;
		public List<MoveBundle> cross()
		{
			cum = new LinkedList<MoveBundle>();
			recurr(0,new Move[domain.length]);
			return cum;
		}
		private void recurr(int i,Move[] partial)
		{
			if(i==domain.length)
			{
				//partial is full
				MoveBundle bundle = null;
				try{
					int c =0 ;
					for(Move m: partial)if(m.getMoveType()!=MoveType.NOTHING)c++;
					if(c!=partial.length)
					{
						if(c==0)return;
						Move[] clean = new Move[c];
						for(int ii=0;ii<partial.length;ii++)
							if(partial[ii].getMoveType()!=MoveType.NOTHING)
								clean[c---1] = partial[ii];
						partial = clean;
					}
					bundle = new MoveBundle(partial,failing);
					cum.add(bundle);
				}
				catch (IllegalStateException e)
				{
					//moves were not compatible.
				}				
			}
			else{
				for(Move m: fixCache.get(domain[i]))
				{
					partial[i] = m;
					recurr(i+1,partial);
				}
			}
			
		}
		
	}
}
