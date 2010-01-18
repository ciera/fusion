package api;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.annot.Relation.Effect;
import relations.Dictionary_Keys;
import relations.Enumeration_NextElement;

@Constraints({
	@Constraint(
			op = "Enumeration.nextElement() : void",
			trigger = "x instanceof Dictionary",
			requires = "Dictionary_Keys(x) OR Enumeration_NextElement(target)",
			effects = {}					
	),
	@Constraint(
			op = "EOM",
			trigger = "x instanceof Enumeration",
			requires = "Enumeration_NextElement(x)",
			effects = {}
	)
})
public class Enumeration {

	@Enumeration_NextElement({"target"})
	@Dictionary_Keys(value = {"*"}, effect = Effect.REMOVE)
	public void nextElement() {}

}
