package org.simulationlogin;

import org.simulationlogin.login.AbstractLogin;
import org.simulationlogin.login.impl.JDLogin;

public class App {

	public static void main(String[] args) {
		//AbstractLogin login = new OschinaLogin("ksfzhaohui@126.com", "623468zhaohui");
		AbstractLogin login = new JDLogin("ksfzhaohui@126.com", "623468zhaohui");
		login.login();
	}
}
