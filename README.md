# ecore-mswitch
An additional switch to for packages generated with the ecore code generator.

## Goal
When using EMF and are generating code from ECore files, it generates you a switch which can be used like this:
```java
String result = new XYZSwitch<String>() {
  @Override
  public Integer caseA(A a) {
    return "an A Object";
  }
  	
  @Override
  public Integer caseB(B b) {
    return "a B Object";
  }
		
  @Override
  public Integer defaultCase(EObject object) {
    return "something else";
  }
}.doSwitch(someEObject);
```

Note that, as above code has to override the switch base class, extension of the switch class is limited.

The goal of this project is to additionally generate a switch which can be used like this:
```java
String result = new XYZMSwitch<String>()
    .when((A a) -> "an A Object")
    .when((B b) -> "a B Object")
    .orElse((EObject object) -> "something else")
    .doSwitch(someObject);
```

It should be:
* easy to use
* composable (for which we implement merge functionality a la `sw1.merge(sw2).merge(sw3)`)
* not too slow
* easy to setup as part of an MWE2-Build workflow

## Dependencies
* This relies upon the ecore workflow from [MDSD-Tools](https://github.com/MDSD-Tools/Ecore-Workflow)

## Folder Structure
* `de.christianvr.ecore.mswitch` - plugin that includes the code generator for the mswitch
* `de.christianvr.ecore.mswitch.base` - plugin that includes classes required at runtime
* `runtime-workspace` - a demo workspace featuring both a demo model with generated code and client code that uses the switch
