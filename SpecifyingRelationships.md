# Relationship Types #

To start using FUSION, you need to specify the types of the relationships. This is done using a Java 5 annotation. Below is an example of the _Item_ relationship.

```
import java.lang.annotation.*;

import edu.cmu.cs.fusion.annot.Relation;
import java.util.Collection;

@Relation({Object.class, Collection.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Item {
	public String[] value();
	public Relation.Effect effect() default Relation.Effect.ADD;
	public String test() default "";
}
```

The `@Relation` annotation defines the types on this relationship (in this case, a relation between an `Object` and a `Collection`.

To work properly, the relation must have the following:
  1. A runtime retention policy
  1. A target of constructor and method
  1. **Exactly** the three parameters above.
If it does not meet these requirements, there will be an error.

Once you have defined your relationship types, you can use them in [effects](SpecifyingEffects.md), [constraints](SpecifyingConstraints.md), [infer rules](SpecifyingInfers.md), and [XQuery retrieval](SpecifyingXML.md).