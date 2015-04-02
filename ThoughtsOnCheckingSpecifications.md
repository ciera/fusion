Before the FUSION analysis is run (on the plugin), we need to read in all the specifications from the framework side. This must be a global search within the workspace.

If the specifications have errors, we don't want to throw an error at this time, since it is the PLUGIN developer we are presenting errors to, and specification errors would be the fault of the framework developer.

Therefore, it seems there should be a secondary analysis that checks for consistency of the specifications. FUSION checker and FUSION analyzer? Maybe so...

Therefore, FUSION Checker is a separate Crystal analysis that just verifies that the specifications are parseable...
  1. Search for all @Relation annotations (in before all comp units)
    1. Verify that they make sense
    1. Store in a relations environment
    1. Store any errors found
  1. Analysis step:
    1. If a relation anno, report errors found in above step
    1. If one of the others, check parsing and type compatibility

FUSION Analyzer assumes that they are parseable, and does the search in the following order:
  1. Search for all @Relation annotations, and fill relations environment
  1. Search for all @Relation (meta) and @Constraint, fill constraint environment
  1. Search for all @InferRule, fill infer environment.
At each step, assume that the relations will exist and that the types are correct. This all happens in before all comp units, and no errors are reported. Just assume it all is ok.