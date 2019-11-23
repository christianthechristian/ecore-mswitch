package de.christianvr.ecore.mswitch.mwe2lib.component;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.impl.GenModelImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.mwe.core.WorkflowContext;
import org.eclipse.emf.mwe.core.issues.Issues;
import org.eclipse.emf.mwe.core.lib.AbstractWorkflowComponent2;
import org.eclipse.emf.mwe.core.monitor.ProgressMonitor;
import org.eclipse.emf.mwe2.runtime.Mandatory;

import de.christianvr.ecore.mswitch.MSwitchClassGenerator;
import de.christianvr.ecore.mswitch.lib.URIToPath;


public class MswitchGenerator extends AbstractWorkflowComponent2 {
	
	private String destPath;
	private String genModel;
	
	@Mandatory
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
	
	@Mandatory
	public void setGenModel(String genModel) {
		this.genModel = genModel;
	}
	
	@Override
	protected void invokeInternal(WorkflowContext workflowContext, ProgressMonitor progressMonitor, Issues issues) {
		// ProgressMonitor is a (useless) NullProgressMonitor in the mwe2 context :(
		progressMonitor.beginTask("Creating Mswitch", 20);
		
		ResourceSet resSet = new ResourceSetImpl();
		Resource resource = resSet.getResource(URI.createURI(genModel), true);
		GenModel genModel = (GenModelImpl) resource.getContents().get(0);
		
		Path path = Paths.get(new URIToPath().convertUri(URI.createURI(destPath)));
		new MSwitchClassGenerator(genModel, path).generate();
		
		progressMonitor.done();
		
	}
	
	
	
}
