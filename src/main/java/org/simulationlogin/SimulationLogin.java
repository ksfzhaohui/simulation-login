package org.simulationlogin;

import org.simulationlogin.login.AbstractLogin;
import org.simulationlogin.login.impl.HuaweiLogin;

public class SimulationLogin {

	public static void main(String[] args) {
		AbstractLogin login = new HuaweiLogin("", "");
		login.login();
	}
}
