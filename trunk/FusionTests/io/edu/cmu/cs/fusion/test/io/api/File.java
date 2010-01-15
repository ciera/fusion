package edu.cmu.cs.fusion.test.io.api;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.test.io.relations.File_GetAbsolutePath;
import edu.cmu.cs.fusion.test.io.relations.File_GetParentFile;
import edu.cmu.cs.fusion.test.io.relations.File_Init;
import edu.cmu.cs.fusion.test.io.relations.File_ToURL;
import edu.cmu.cs.fusion.annot.Relation.Effect;

@Constraints({
	@Constraint(
			op = "File(String s)",
			trigger = "x instanceof File",
			requires = "File_GetAbsolutePath(x)",
			effects = {}					
	),
	@Constraint(
			op = "File.getParentFile() : void",
			trigger = "TRUE",
			requires = "File_Init(target)",
			effects = {}
	),
	@Constraint(
			op = "File.toURL() : void",
			trigger = "x instanceof File",
			requires = "File_GetParentFile(x)",
			effects = {}
	),
	@Constraint(
			op = "EOM",
			trigger = "y instanceof File",
			requires = "File_ToURL(y)",
			effects = {}
	)
})

public class File {

	@File_Init({"target"})
	@File_GetAbsolutePath(value = {"*"}, effect = Effect.REMOVE)
	public File(String s) {		
	}
	
	@File_GetAbsolutePath({"target"})
	public void getAbsolutePath() {		
	}
	
	@File_GetParentFile({"target"})
	@File_Init(value = {"target"}, effect = Effect.REMOVE)
	public void getParentFile() {		
	}
	
	@File_ToURL({"target"})
	@File_GetParentFile(value = {"*"}, effect = Effect.REMOVE)
	public void toURL() {
	}
		
}
