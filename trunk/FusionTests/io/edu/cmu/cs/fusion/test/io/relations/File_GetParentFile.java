package edu.cmu.cs.fusion.test.io.relations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.cmu.cs.fusion.annot.Relation;
import edu.cmu.cs.fusion.test.io.api.File;

@Relation({File.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface File_GetParentFile {
	public String[] value();
	public Relation.Effect effect() default Relation.Effect.ADD;
	public String test() default "";
}
