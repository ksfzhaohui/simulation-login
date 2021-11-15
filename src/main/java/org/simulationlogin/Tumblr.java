package org.simulationlogin;

import org.simulationlogin.tumblr.TumblrVideo;

//test
public class Tumblr {

	public static void main(String[] args) throws Exception {
		String userName = "sevencuo";
		if (args != null && args.length > 0) {
			userName = args[0];
		}

		TumblrVideo.getVideoUrlListOpenProxy(userName, 1);
	}
}
