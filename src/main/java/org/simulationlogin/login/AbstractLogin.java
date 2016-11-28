package org.simulationlogin.login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.simulationlogin.util.ConnectionManager;
import org.simulationlogin.util.Constants;

public abstract class AbstractLogin {

	private static Log logger = LogFactory.getLog(AbstractLogin.class);

	/** 图片存放的根目录 */
	public final String IMG_ROOT_PATH = System.getProperty("user.dir") + File.separator;

	private DefaultHttpClient userClient;
	private ObjectMapper mapper;

	private String userName;
	private String password;
	/** 验证码 **/
	private String authCode;
	/** 登录前准备的参数 **/
	protected Map<String, String> readyParams;

	public AbstractLogin(String userName, String password) {
		this.userName = userName;
		this.password = password;

		readyParams = new HashMap<String, String>();
		userClient = ConnectionManager.getHttpClient();
		mapper = new ObjectMapper();
	}

	/**
	 * 登陆 分成4个步骤： 1.为登录准备必要的参数 2.获取验证码 3.执行登录 4.验证登录成功
	 */
	public void login() {
		try {
			readyLogin();

			getAuthCodeImage();
			getInputAuthCode();

			int result = executeLogin();
			if (result == Constants.SUCCESS) {
				testLogin();
			}
		} catch (Exception e) {
			logger.error("登陆异常", e);
		}
	}

	/**
	 * 准备登录
	 * 
	 * @throws Exception
	 */
	protected abstract void readyLogin() throws Exception;

	/**
	 * 执行登陆
	 * 
	 * @return 登陆的结果
	 */
	protected abstract int executeLogin() throws Exception;

	/**
	 * 登陆成功，进行测试
	 * 
	 * @throws Exception
	 */
	protected abstract void testLogin() throws Exception;

	/**
	 * 获取验证码图片
	 * 
	 * @throws Exception
	 */
	protected void getAuthCodeImage() throws Exception {
		HttpGet getImage = null;
		HttpResponse response = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			String imageUrl = getAuthCodeImageUrl();
			logger.info("请求login-->" + imageUrl);
			response = userClient.execute(getImage);

			String savePath = getSavePath(getSaveImageRelativePathPath());
			File picPath = new File(savePath);
			File picFile = new File(savePath + userName + "_code.jpg");
			if (!picPath.exists()) {
				picPath.mkdirs();
			}
			logger.info(Thread.currentThread().getName() + "下载图片到" + picFile);
			HttpEntity entity = response.getEntity();

			is = entity.getContent();
			os = new FileOutputStream(picFile);
			IOUtils.copy(is, os);
		} catch (Exception e) {
			throw e;
		} finally {
			if (getImage != null) {
				getImage.releaseConnection();
			}
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}
	}

	/**
	 * 获取用户输入的验证码
	 * 
	 * @throws IOException
	 */
	protected void getInputAuthCode() throws Exception {
		logger.info("请输入下载图片中的验证码[o:重新获取一张]:");
		BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
		authCode = strin.readLine();

		if (authCode.equals("o")) {
			getAuthCodeImage();
			getInputAuthCode();
		}
	}

	/**
	 * 获取准备登录地址
	 * 
	 * @return
	 */
	protected abstract String getReadyLoginUrl();

	/**
	 * 获取验证码图片地址
	 * 
	 * @return
	 */
	protected abstract String getAuthCodeImageUrl();

	/**
	 * 获取登陆地址
	 * 
	 * @return
	 */
	protected abstract String getLoginUrl();

	/**
	 * 获取图片应该保存的路径
	 * 
	 * @param imgRelativePath
	 *            图片被保存的相对路径
	 * @return
	 */
	protected String getSavePath(String imgRelativePath) {
		return IMG_ROOT_PATH + imgRelativePath + File.separator;
	}

	/**
	 * 获取图片下载存放的相对路径
	 * 
	 * @return
	 */
	protected String getSaveImageRelativePathPath() {
		return "authImage";
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getAuthCode() {
		return authCode;
	}

	public ObjectMapper getMapper() {
		return mapper;
	}

	public DefaultHttpClient getUserClient() {
		return userClient;
	}

}
