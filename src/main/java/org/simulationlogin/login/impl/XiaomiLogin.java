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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.simulationlogin.login.AbstractLogin;
import org.simulationlogin.util.PwEncryption;

/**
 * 模拟小米登陆
 * 
 * @author zhaohui
 * 
 */
public class XiaomiLogin extends AbstractLogin {

	private static Log logger = LogFactory.getLog(XiaomiLogin.class);

	public XiaomiLogin(String userName, String password) {
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
			loginNameValues.add(new BasicNameValuePair("user", getUserName()));
			loginNameValues.add(new BasicNameValuePair("_json", "true"));
			loginNameValues.add(new BasicNameValuePair("hash", PwEncryption
					.encryption("MD5", getPassword())));
			loginNameValues.add(new BasicNameValuePair("captCode",
					getAuthCode()));
			loginNameValues.add(new BasicNameValuePair("sid", "passport"));
			loginNameValues.add(new BasicNameValuePair("_sign",
					"2&V1_passport&wqS4omyjALxMm//3wLXcVcITjEc="));
			// loginNameValues.add(new BasicNameValuePair("qs",
			// "%3Fsid%3Dpassport"));
			loginNameValues.add(new BasicNameValuePair("serviceParam",
					"{\"checkSafePhone\":false}"));
			loginNameValues.add(new BasicNameValuePair("callback",
					"https://account.xiaomi.com"));

			loginPost.setEntity(new UrlEncodedFormEntity(loginNameValues,
					Consts.UTF_8));

			HttpResponse response = getUserClient().execute(loginPost);
			String loginRespInfoStr = EntityUtils.toString(
					response.getEntity(), Charset.forName("utf-8"));
			logger.info(loginRespInfoStr);

			return 0;
		} catch (Exception e) {
			throw e;
		} finally {
			loginPost.releaseConnection();
		}
	}

	@Override
	protected String getAuthCodeImageUrl() {
		return "https://account.xiaomi.com/pass/getCode?icodeType=login";
	}

	@Override
	protected String getLoginUrl() {
		return "https://www.account.xiaomi.com/pass/serviceLoginAuth2";
	}

	@Override
	protected void testLogin() throws Exception {
		
	}

}
