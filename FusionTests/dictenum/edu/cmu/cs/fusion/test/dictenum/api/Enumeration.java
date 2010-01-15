package edu.cmu.cs.fusion.test.dictenum.api;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.annot.Relation.Effect;
import edu.cmu.cs.fusion.test.dictenum.relations.Dictionary_Keys;
import edu.cmu.cs.fusion.test.dictenum.relations.Enumeration_NextElement;
import edu.cmu.cs.fusion.test.dictenum.relations.Enumeration_HasMoreElements;
import edu.cmu.cs.fusion.test.dictenum.relations.Dictionary_Get;

@Constraints({
	@Constraint(
			op = "Enumeration.hasMoreElements() : void",
			trigger = "TRUE",
			requires = "Dictionary_Keys(d) OR Dictionary_Get(d)",
			effects = {}	
	),
	@Constraint(
			op = "Enumeration.nextElement() : void",
			trigger = "TRUE",
			requires = "Enumeration_HasMoreElements(target)",
			effects = {}	
	)
})
public class Enumeration {

	@Enumeration_HasMoreElements({"target"})
	@Dictionary_Keys(value = {"*"}, effect = Effect.REMOVE)
	@Dictionary_Get(value = {"*"}, effect = Effect.REMOVE)
	public void hasMoreElements() {}
	
	@Enumeration_NextElement({"target"})
	@Enumeration_HasMoreElements(value = {"target"}, effect = Effect.REMOVE)
	public void nextElement() {}
	
}

