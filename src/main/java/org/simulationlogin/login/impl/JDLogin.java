package org.simulationlogin.login.impl;

import org.simulationlogin.login.AbstractLogin;

public class JDLogin extends AbstractLogin {

	public JDLogin(String userName, String password) {
		super(userName, password);
	}

	@Override
	protected int executeLogin() throws Exception {
		return 0;
	}

	@Override
	protected void testLogin() throws Exception {

	}

	@Override
	protected String getAuthCodeImageUrl() {
		return "https://authcode.jd.com/verify/image?acid=9cba3cef-b537-4067-9ee2-d4a1fff3fde6";
	}

	@Override
	protected String getLoginUrl() {
		return null;
	}

}
