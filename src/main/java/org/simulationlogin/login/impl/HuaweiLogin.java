package org.simulationlogin.login.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.simulationlogin.login.AbstractLogin;
import org.simulationlogin.util.Constants;

/**
 * 模拟华为商城登录
 * 
 * @author ksfzhaohui
 * 
 */
public class HuaweiLogin extends AbstractLogin {

	private static Log logger = LogFactory.getLog(HuaweiLogin.class);

	public HuaweiLogin(String userName, String password) {
		super(userName, password);
	}

	@Override
	protected void readyLogin() throws Exception {
		HttpGet readyGet = null;
		try {
			String readyUrl = getReadyLoginUrl();
			readyGet = new HttpGet(readyUrl);
			HttpResponse response = getUserClient().execute(readyGet);
			HttpEntity entity = response.getEntity();
			String info = EntityUtils.toString(entity);
			String array[] = info.split("pageToken:\"");
			if (array != null && array.length > 1) {
				String params[] = array[1].split("\",");
				if (params != null && params.length > 0) {
					readyParams.put("pageToken", params[0]);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (readyGet != null) {
				readyGet.releaseConnection();
			}
		}
	}

	@Override
	protected int executeLogin() throws Exception {
		HttpPost loginPost = null;
		try {
			String loginUrl = getLoginUrl();
			logger.info("请求login-->" + loginUrl);
			loginPost = new HttpPost(loginUrl);

			loginPost.setHeader("Host", "hwid1.vmall.com");
			loginPost.setHeader("Origin", "https://hwid1.vmall.com");
			loginPost.setHeader("Referer",
					"https://hwid1.vmall.com/CAS/portal/login.html");

			List<NameValuePair> loginNameValues = new ArrayList<NameValuePair>();
			loginNameValues.add(new BasicNameValuePair("userAccount",
					getUserName()));
			loginNameValues.add(new BasicNameValuePair("password",
					getPassword()));
			loginNameValues.add(new BasicNameValuePair("authcode",
					getAuthCode()));
			loginNameValues.add(new BasicNameValuePair("pageToken", readyParams
					.get("pageToken")));
			loginNameValues.add(new BasicNameValuePair("reqClientType", "26"));

			loginPost.setEntity(new UrlEncodedFormEntity(loginNameValues,
					Consts.UTF_8));

			HttpResponse response = getUserClient().execute(loginPost);
			String loginRespInfoStr = EntityUtils.toString(
					response.getEntity(), Charset.forName("utf-8"));
			logger.info(loginRespInfoStr);
			if (loginRespInfoStr.contains("callbackURL")) {
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
	protected void testLogin() throws Exception {

	}

	@Override
	protected String getReadyLoginUrl() {
		return "https://hwid1.vmall.com/CAS/portal/login.html";
	}

	@Override
	protected String getAuthCodeImageUrl() {
		return "https://hwid1.vmall.com/CAS/authCodeImage?session_code_key=login_session_ramdom_code_key";
	}

	@Override
	protected String getLoginUrl() {
		return "https://hwid1.vmall.com/CAS/ajaxHandler/remoteLogin";
	}

}
