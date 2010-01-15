package edu.cmu.cs.fusion.test.io.api;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.annot.Relation.Effect;
import edu.cmu.cs.fusion.test.io.relations.Closed;
import edu.cmu.cs.fusion.test.io.relations.Writeable;

@Constraints({
@Constraint(
		op = "FileWriter.write(String str) : void",
		trigger = "TRUE",
		requires = "Writeable(target)",
		effects = {}
),
@Constraint(
		op = "EOM",
		trigger = "x instanceof FileWriter",
		requires = "Closed(x)",
		effects = {}
)})
public class FileWriter {
	@Writeable({"target"})
	public FileWriter() {}
	
	public void write(String str) {}
	
	@Closed({"target"})
	@Writeable(value = {"target"}, effect = Effect.REMOVE)
	public void close() {}
}
