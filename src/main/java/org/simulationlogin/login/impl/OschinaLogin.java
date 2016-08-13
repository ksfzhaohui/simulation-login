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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.simulationlogin.login.AbstractLogin;
import org.simulationlogin.util.Constants;
import org.simulationlogin.util.PwEncryption;

/**
 * 模拟开源中国登陆
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
	protected void readyLogin() throws Exception {

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
			logger.info(loginRespInfoStr);
			if (loginRespInfoStr.equals("")) {
				return Constants.SUCCESS;
			}
			return Constants.FAIL;
		} catch (Exception e) {
			throw e;
		} finally {
			loginPost.releaseConnection();
		}
	}

	@Override
	protected String getReadyLoginUrl() {
		return null;
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

		Document doc = Jsoup.parseBodyFragment(info);
		Elements es = doc.select("#OSC_Userbar > em");
		if (es != null && es.size() > 0) {
			logger.info("用户名:" + es.get(0).text());
		}
	}

}
