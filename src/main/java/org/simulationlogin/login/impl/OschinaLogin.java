package org.simulationlogin.login.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.simulationlogin.login.AbstractLogin;
import org.simulationlogin.util.Constants;
import org.simulationlogin.util.PwEncryption;

/**
 * 开源中国模拟登陆
 * 
 * @author zhaohui
 * 
 */
public class OschinaLogin extends AbstractLogin {

	private static Log logger = LogFactory.getLog(OschinaLogin.class);

	public OschinaLogin(String userName, String password) {
		super(userName, password);
	}

	@Override
	protected int executeLogin() throws Exception {
		HttpPost loginPost = null;
		try {
			String loginUrl = getLoginUrl();
			logger.info("请求login-->" + loginUrl);
			loginPost = new HttpPost(loginUrl);

			List<NameValuePair> loginNameValues = new ArrayList<NameValuePair>();
			loginNameValues.add(new BasicNameValuePair("email", getUserName()));
			loginNameValues.add(new BasicNameValuePair("pwd", PwEncryption
					.encryption("SHA1", getPassword())));
			loginNameValues.add(new BasicNameValuePair("verifyCode",
					getAuthCode()));

			loginPost.setEntity(new UrlEncodedFormEntity(loginNameValues,
					Consts.UTF_8));

			HttpResponse response = getUserClient().execute(loginPost);
			String loginRespInfoStr = EntityUtils.toString(
					response.getEntity(), Charset.forName("utf-8"));
			if (loginRespInfoStr.equals("")) {
				return Constants.SUCCESS;
			}
			logger.info(loginRespInfoStr);
			return Constants.FAIL;
		} catch (Exception e) {
			throw e;
		} finally {
			loginPost.releaseConnection();
		}
	}

	@Override
	protected String getAuthCodeImageUrl() {
		return "https://www.oschina.net/action/user/captcha";
	}

	@Override
	protected String getLoginUrl() {
		return "https://www.oschina.net/action/user/hash_login";
	}

	@Override
	protected void testLogin() throws Exception {
		String orderLoginUrl = "http://www.oschina.net/";
		logger.info("请求orderLoginUrl-->" + orderLoginUrl);
		HttpGet loginUrlGet = new HttpGet(orderLoginUrl);

		HttpResponse response = getUserClient().execute(loginUrlGet);
		String info = EntityUtils.toString(response.getEntity());
		logger.info(info);
	}

}
