package demo2_user;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.ecore.EObject;

import demo2.Demo2Factory;
import demo2.Location;
import demo2.NetworkingDevice;
import demo2.PhysicalDevice;
import demo2.Port;
import demo2.Router;
import demo2.Server;
import demo2.util.Demo2Switch;
import demo2_user.ManualSwitch.SwitchingException;

public class Main {

	public static void main(String[] args) {
		Map<String, EObject> eobjects = createSomeEObjects();
		
		List<DemoCase<String>> demoCases = new LinkedList<>();
		demoCases.add(new ParentFallback());
		demoCases.add(new EasySyntax());
		demoCases.add(new NullBehaviour());
		demoCases.add(new EmptySwitch());

		demoCases.forEach((demo) -> {			
			demo.printEvaluation(eobjects);
			System.out.println("##############################################################################");
		});

	}

	private static Map<String, EObject> createSomeEObjects() {
		Demo2Factory factory = Demo2Factory.eINSTANCE;
		
		Location serverRoom = factory.createLocation();
		serverRoom.setName("Server Room");
		
		Router centralRouter = factory.createRouter();
		centralRouter.setLocation(serverRoom);
		
		Port routerPort = factory.createPort();
		centralRouter.getPorts().add(routerPort);
		
		Server someServer = factory.createServer();
		someServer.setName("Some Server");
		
		Port serverPort = factory.createPort();
		someServer.getPorts().add(serverPort);
				
		routerPort.setConnected_to(serverPort);
		serverPort.setConnected_to(routerPort);
		
		Map<String, EObject> tests = new HashMap<>();
		tests.put("serverRoom", serverRoom);
		tests.put("centralRouter", centralRouter);
		tests.put("routerPort", routerPort);
		tests.put("someServer", someServer);
		tests.put("serverPort", serverPort);
		return tests;
	}
	
	private static class ParentFallback extends DemoCase<String> {
		
		public ParentFallback() {
			super("Both switches work themselves upwards through the hierarchy until a match is found.");
		}
		
		@Override
		protected Demo2Switch<String> buildOldSwitch() {
			return new Demo2Switch<>() {
				public String caseRouter(Router object) {return "ein Router";};
				public String caseNetworkingDevice(NetworkingDevice object) {return "ein Networking Device";};
				public String defaultCase(EObject object) {return "etwas anderes";};
			};
		}

		@Override
		protected Demo2ManualSwitch<String> buildNewSwitch() {
			return new Demo2ManualSwitch<String>()
					.caseRouter(r -> "ein Router")
					.caseNetworkingDevice(s -> "ein Networking Device")
					.defaultCase(x -> "etwas anderes");
		}
		
		@Override
		protected boolean test() {
			EObject probe = Demo2Factory.eINSTANCE.createServer();
			return buildNewSwitch().doSwitch(probe).equals("ein Networking Device");
		}
	}
	
	private static class NullBehaviour extends DemoCase<String>{
		
		public NullBehaviour() {
			super("The new switch returns null when a case is defined and returns null.");
		}
		
		@Override
		protected Demo2Switch<String> buildOldSwitch() {
			return new Demo2Switch<>() {
				public String caseLocation(Location l) {return null;}
				public String caseNetworkingDevice(NetworkingDevice nd) {return null;}
				public String casePhysicalDevice(PhysicalDevice pd) {return null;}
				public String casePort(Port p) {return null;}
				public String caseRouter(Router r) {return null;}
				public String defaultCase(EObject object) {return "default";}
			};
		}

		@Override
		protected Demo2ManualSwitch<String> buildNewSwitch() {
			return new Demo2ManualSwitch<String>()
					.caseLocation(l -> null)
					.caseNetworkingDevice(s -> null)
					.casePhysicalDevice(pd -> null)
					.casePort(p -> null)
					.caseRouter(r -> null)
					.caseServer(s -> null)
					.defaultCase(x -> "default");
		}
		
		@Override
		protected boolean test() {
			EObject probe = Demo2Factory.eINSTANCE.createServer();
			return buildNewSwitch().doSwitch(probe) == null && buildOldSwitch().doSwitch(probe) != null;
		}
		
	}
	
	private static class EmptySwitch extends DemoCase<String> {
		
		public EmptySwitch() {
			super("The new switch raises an exception when no default case is given on falling through all cases.");
		}
		
		@Override
		protected Demo2Switch<String> buildOldSwitch() {
			return new Demo2Switch<>();
		}

		@Override
		protected Demo2ManualSwitch<String> buildNewSwitch() {
			return new Demo2ManualSwitch<>();
		}
		
		@Override
		protected boolean test() {
			EObject probe = Demo2Factory.eINSTANCE.createLocation();
			try {
				buildNewSwitch().doSwitch(probe);
				return false;
			} catch(SwitchingException e) {
				return true;
			}
		}
		
	}
	
	private static class EasySyntax extends DemoCase<String> {

		public EasySyntax() {
			super("The when syntax allows for building a switch without a lot of boilerplate code.");
		}

		@Override
		protected Demo2Switch<String> buildOldSwitch() {
			return new Demo2Switch<>() {
				public String caseNetworkingDevice(NetworkingDevice object) {return "device";};
				public String caseRouter(Router object) {return "router";};
			};
		}

		@Override
		protected Demo2ManualSwitch<String> buildNewSwitch() {
			return new Demo2ManualSwitch<String>()
				.when((NetworkingDevice dev) -> "device")
				.when((Router r) -> "router")
				.defaultCase(obj -> null);
		}
		
		@Override
		protected boolean test() {
			EObject probe = Demo2Factory.eINSTANCE.createRouter();
			return buildNewSwitch().doSwitch(probe).contentEquals("router");
		}
		
	}
	
	private abstract static class DemoCase<T> {
		private String description;
		
		public DemoCase(String description) {
			this.description = description;
		}
		
		public void printEvaluation(Map<String, EObject> eobjects) {
			Demo2ManualSwitch<T> newSwitch = buildNewSwitch();
			Demo2Switch<T> oldSwitch = buildOldSwitch();
			
			System.out.println(getDescription());
			boolean[] equivalent = {true};
			
			eobjects.forEach((varName, val) -> {
				Object standardEvaluation = oldSwitch.doSwitch(val);
				Object newEvaluation;
				
				try {
					newEvaluation = newSwitch.doSwitch(val);
				} catch (demo2_user.ManualSwitch.SwitchingException e) {
					newEvaluation = "<exception>";
				}
				
				if (!Objects.equals(newEvaluation, standardEvaluation)) {
					equivalent[0] = false;
				}
				
				System.out.println("  " + varName);
				System.out.println("    std: " + standardEvaluation);
				System.out.println("    new: " + newEvaluation);
			});
			System.out.println("    " + (equivalent[0] ? "(equivalent)" : "(different)"));
			System.out.println("    " + (test() ? "(test successful)" : "(test failed)"));
			
		}
		protected abstract Demo2Switch<T> buildOldSwitch();
		protected abstract Demo2ManualSwitch<T> buildNewSwitch();
		protected abstract boolean test();
		public String getDescription() {
			return description;
		}

	}

}
