package org.simulationlogin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class ConnectionUtil {

	private static Log logger = LogFactory.getLog(ConnectionUtil.class);

	/** 图片存放的根目录 */
	public final static String IMG_ROOT_PATH = System.getProperty("user.dir")
			+ File.separator;

	/**
	 * 下载验证码图片
	 * @param userName
	 * @param response
	 */
	public static void downloadCode(String userName, HttpResponse response) {
		HttpEntity entity = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			String savePath = getSavePath("code");
			File picPath = new File(savePath);
			File picFile = new File(savePath + userName + ".jpg");
			if (!picPath.exists()) {
				picPath.mkdirs();
			}
			logger.info("下载验证码图片到" + picFile);
			entity = response.getEntity();
			is = entity.getContent();
			os = new FileOutputStream(picFile);
			IOUtils.copy(is, os);
		} catch (Exception e) {
			logger.error(e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 获取返回的消息
	 * @param response
	 * @return
	 */
	public static String getReturnMessage(HttpResponse response) {
		HttpEntity entity = null;
		InputStream is = null;
		StringWriter sw = new StringWriter();
		try {
			entity = response.getEntity();
			if (entity != null) {
				is = entity.getContent();
				IOUtils.copy(is, sw, "utf-8");
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(sw);
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
			}
		}
		return sw.toString();
	}

	/**
	 * 获取图片应该保存的路径
	 * 
	 * @param imgRelativePath
	 *            图片被保存的相对路径
	 * @return
	 */
	private static String getSavePath(String imgRelativePath) {
		return IMG_ROOT_PATH + imgRelativePath + File.separator;
	}

}
