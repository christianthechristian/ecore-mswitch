package demo2_user;

import demo2.Demo2Factory;
import demo2.NetworkingDevice;
import demo2.Router;
import demo2.Server;
import demo2.xutil.Demo2MSwitch;

public class SimpleMSwichtDemo {

	public static void main(String[] args) {
		Server server = Demo2Factory.eINSTANCE.createServer();
		Router router = Demo2Factory.eINSTANCE.createRouter();
		
		var sw = new Demo2MSwitch<String>()
			.when((Router r) -> "a router")
			.when((NetworkingDevice d) -> "some other networking device")
			.orElse(o -> "something else");
		
		var sw2 = new Demo2MSwitch<String>()
			.when((Router r) -> "this will be overridden")
			.merge(sw) // overrides the above case
			.when((Server s) -> "a server");
		
		System.out.println(sw.doSwitch(router));
		System.out.println(sw.doSwitch(server));
		
		System.out.println(sw2.doSwitch(router));
		System.out.println(sw2.doSwitch(server));
	}

}
