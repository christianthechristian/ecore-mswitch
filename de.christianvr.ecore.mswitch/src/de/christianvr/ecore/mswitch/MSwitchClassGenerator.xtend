package de.christianvr.ecore.mswitch

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.eclipse.emf.codegen.ecore.genmodel.GenModel
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage
import org.eclipse.emf.codegen.ecore.genmodel.GenClass

class MSwitchClassGenerator {
	GenModel genModel
	Path outPath
	
	new(GenModel genModel, Path outPath) {
		this.genModel = genModel
		this.outPath = outPath
	}
	
	def void generate(){
		this.genModel.genPackages.forEach[generatePackage]
	}
	
	private def generatePackage(GenPackage genPackage) {
		val gen = new DirtyGenerator(genPackage)
		val content = gen.content
		val outfilePath = outPath
			.resolve(Paths.get("", gen.packageName.split("\\.")))
			.resolve(gen.className + ".java");
		
		Files.createDirectories(outfilePath.parent)
		Files.writeString(outfilePath, content)
	}
	
}

class DirtyGenerator {
	GenPackage genPackage
	
	new(GenPackage genPackage) {
		this.genPackage = genPackage
	}
	
	def String getClassName() {
		genPackage.switchClassName.replaceFirst("Switch$", "MSwitch")
	}
	
	def String getPackageName() {
		genPackage.packageName + ".xutil"
	}
	
	def String getContent() {
		'''
		package «packageName»;
		
		import java.util.function.Function;
		
		import org.eclipse.emf.ecore.EPackage;
		import org.eclipse.emf.ecore.EObject;
		
		import de.christianvr.ecore.mswitch.base.MSwitch;
		
		public class «className»<T> extends MSwitch<T> {
			private static «genPackage.importedPackageInterfaceName» modelPackage;
			«FOR c:genPackage.genClasses»
			private Function<«c.importedInterfaceName»,T> «caseName(c)»;
			«ENDFOR»
			
			public «className»() {
				if (modelPackage == null) {
					modelPackage = «genPackage.importedPackageInterfaceName».eINSTANCE;
				}
			}
			
			public boolean isSwitchFor(EPackage ePackage) {
				return ePackage == modelPackage;
			}
			
			protected T doSwitch(int classifierID, EObject eObject) throws MSwitch.SwitchingException {
				switch(classifierID) {
					«FOR c : genPackage.genClasses»
					case «genPackage.importedPackageInterfaceName».«genPackage.getClassifierID(c)»: {
						«c.importedInterfaceName» casted = («c.importedInterfaceName») eObject;
						if («caseName(c)» != null) return «caseName(c)».apply(casted);
						«FOR alternative:c.switchGenClasses»
						if («caseName(alternative)» != null) return «caseName(alternative)».apply(casted);
						«ENDFOR»
						break;
					}
					«ENDFOR»
					default:
						throw new Error("type " + eObject.eClass() + " was not considered by the mswitch code generator");
				}
				return applyDefaultCase(eObject);
			}
			
			public «className»<T> merge(«className»<T> other) {
				«FOR field: genPackage.genClasses.map[caseName]»
				if (other.«field» != null) this.«field» = other.«field»;
				«ENDFOR»
				return this;
			} 
			
			«FOR c : genPackage.genClasses»
			public interface «interfaceName(c)»<T> extends Function<«c.importedInterfaceName»,T> {}
			«ENDFOR»
			
			«FOR c : genPackage.genClasses»
			public «className»<T> when(«interfaceName(c)»<T> then) {
				this.«caseName(c)» = then;
				return this;
			}
			«ENDFOR»
			public «className»<T> orElse(Function<EObject, T> defaultCase) {
				this.defaultCase = defaultCase;
				return this;
			}
		}
		'''
	}
	
	private def caseName(GenClass c) {
		"case" + genPackage.getClassUniqueName(c)
	}
	
	private def interfaceName(GenClass c) {
		"When" + genPackage.getClassUniqueName(c)
	}
}