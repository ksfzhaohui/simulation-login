package org.simulationlogin.tumblr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.simulationlogin.util.ConnectionManager;

/**
 * 下载tumblr指定用户名的视频文件
 * 
 * @author ksfzhaohui
 * 
 */
public class TumblrVideo {

	private static Log logger = LogFactory.getLog(TumblrVideo.class);

	private static String proxy_id = "127.0.0.1";
	private static int proxy_port = 1080;
	private static int init_max_page = 10;
	private static String tumblr_video = "https://vt.tumblr.com/tumblr_";

	public static List<String> getVideoUrlListOpenProxy(String userName) {
		return getVideoUrlList(userName, true, init_max_page);
	}

	public static List<String> getVideoUrlListOpenProxy(String userName,
			int endPage) {
		return getVideoUrlList(userName, true, endPage);
	}

	public static List<String> getVideoUrlListOpenProxy(String userName,
			boolean isProxy) {
		return getVideoUrlList(userName, isProxy, init_max_page);
	}

	/**
	 * 获取指定用户的videoUrl地址列表
	 * 
	 * @param userName
	 *            用户名
	 * @param isProxy
	 *            是否代理
	 * @param endPage
	 *            末尾的pageIndex
	 * @return
	 */
	public static List<String> getVideoUrlList(String userName,
			boolean isProxy, int endPage) {
		List<String> videoUrlList = new ArrayList<String>();
		DefaultHttpClient userClient = ConnectionManager.getHttpClient();
		if (isProxy) {
			HttpHost proxy = new HttpHost(proxy_id, proxy_port);
			userClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		}

		HttpGet get = null;
		try {
			for (int pageIndex = 1; pageIndex <= endPage; pageIndex++) {
				String readyUrl = "http://" + userName + ".tumblr.com/page/"
						+ pageIndex;
				logger.info("请求:" + readyUrl);
				get = new HttpGet(readyUrl);
				HttpResponse response = userClient.execute(get);
				HttpEntity entity = response.getEntity();
				String info = EntityUtils.toString(entity);

				Document doc = Jsoup.parseBodyFragment(info);
				Elements es = doc.select("#posts article");
				if (es != null && es.size() > 0) {
					for (int i = 0; i < es.size(); i++) {
						HttpGet videoGet = null;
						try {
							Element articleEle = es.get(i);
							if (articleEle.attr("class").contains("video")) {
								String videoHtmlUrl = articleEle.select(
										"iframe").attr("src");
								videoGet = new HttpGet(videoHtmlUrl);
								HttpResponse videoResponse = userClient
										.execute(videoGet);
								HttpEntity videoEntity = videoResponse
										.getEntity();
								String videoInfo = EntityUtils
										.toString(videoEntity);
								Document videoDoc = Jsoup
										.parseBodyFragment(videoInfo);
								Elements videoEs = videoDoc.select("video");
								if (videoEs != null && videoEs.size() > 0) {
									String poster = videoEs.get(0).attr(
											"poster");
									String videoId = poster.split("tumblr_")[1]
											.split("_frame")[0];
									String videoUrl = tumblr_video + videoId
											+ ".mp4";
									videoUrlList.add(videoUrl);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (videoGet != null) {
								videoGet.releaseConnection();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (get != null) {
				get.releaseConnection();
			}
		}

		return videoUrlList;
	}

}
