package demo2_user;

import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import demo2.*;

public class Demo2ManualSwitch<T> extends ManualSwitch<T> {
	// TODO add a compatibility mode that probes hierarchy-upwards when methods
	// return null

	private static Demo2Package modelPackage;
	
	private Function<Location, T> caseLocation;
	private Function<NetworkingDevice, T> caseNetworkingDevice;
	private Function<PhysicalDevice, T> casePhysicalDevice;
	private Function<Port, T> casePort;
	private Function<Router, T> caseRouter;
	private Function<Server, T> caseServer;
	
	public Demo2ManualSwitch() {
		if (modelPackage == null) {
			modelPackage = Demo2Package.eINSTANCE;
		}
	}

	public boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	protected T doSwitch(int classifierID, EObject eObject) throws SwitchingException {
		switch (classifierID) { // TODO: consider if instanceOf should be used in order not to depend on ids
		
		case Demo2Package.NETWORKING_DEVICE: {
			NetworkingDevice casted = (NetworkingDevice) eObject;
			if (caseNetworkingDevice != null)
				return caseNetworkingDevice.apply(casted);
			break;
		}
		case Demo2Package.PORT: {
			Port casted = (Port) eObject;
			if (casePort != null)
				return casePort.apply(casted);
			break;
		}
		case Demo2Package.ROUTER: {
			Router casted = (Router) eObject;
			if (caseRouter != null)
				return caseRouter.apply(casted);
			if (caseNetworkingDevice != null)
				return caseNetworkingDevice.apply(casted);
			if (casePhysicalDevice != null)
				return casePhysicalDevice.apply(casted);
			break;
		}
		case Demo2Package.SERVER: {
			Server casted = (Server) eObject;
			if (caseServer != null)
				return caseServer.apply(casted);
			if (caseNetworkingDevice != null)
				return caseNetworkingDevice.apply(casted);
			if (casePhysicalDevice != null)
				return casePhysicalDevice.apply(casted);
			break;
		}
		case Demo2Package.PHYSICAL_DEVICE: {
			PhysicalDevice casted = (PhysicalDevice) eObject;
			if (casePhysicalDevice != null)
				return casePhysicalDevice.apply(casted);
			break;
		}
		case Demo2Package.LOCATION: {
			Location casted = (Location) eObject;
			if (caseLocation != null)
				return caseLocation.apply(casted);
			break;
		}
		default:
			// this should not happen, because this method is only called when we are a switch for the package
			throw new Error("this type was not considered by the switch code generator");
		}
		return applyDefaultCase(eObject);
	}

	public Demo2ManualSwitch<T> caseLocation(Function<Location, T> then) {
		this.caseLocation = then;
		return this; // builder pattern
	}

	public Demo2ManualSwitch<T> caseNetworkingDevice(Function<NetworkingDevice, T> then) {
		this.caseNetworkingDevice = then;
		return this;
	}

	public Demo2ManualSwitch<T> casePhysicalDevice(Function<PhysicalDevice, T> then) {
		this.casePhysicalDevice = then;
		return this;
	}

	public Demo2ManualSwitch<T> casePort(Function<Port, T> then) {
		this.casePort = then;
		return this;
	}

	public Demo2ManualSwitch<T> caseRouter(Function<Router, T> then) {
		this.caseRouter = then;
		return this;
	}

	public Demo2ManualSwitch<T> caseServer(Function<Server, T> then) {
		this.caseServer = then;
		return this;
	}

	public Demo2ManualSwitch<T> defaultCase(Function<EObject, T> then) {
		this.defaultCase = then;
		return this;
	}
	
	// the code below is for permitting a less-verbose syntax
	public interface Case<T, S> {
		public T apply(S object);
	}
	// these non-completely-generic types need to be defined individually in order to prevent type erasure and thus enable overloading
	public interface ServerCase<T> extends Case<T, Server> {}
	public interface RouterCase<T> extends Case<T, Router> {}
	public interface NetworkingDeviceCase<T> extends Case<T, NetworkingDevice>{};
	
	public Demo2ManualSwitch<T> when(ServerCase<T> then) {
		caseServer(o -> then.apply(o));
		return this;
	}
	
	public Demo2ManualSwitch<T> when(RouterCase<T> then) {
		caseRouter(o -> then.apply(o));
		return this;
	}
	
	public Demo2ManualSwitch<T> when(NetworkingDeviceCase<T> then) {
		caseNetworkingDevice(o -> then.apply(o));
		return this;
	}
	
	

}
