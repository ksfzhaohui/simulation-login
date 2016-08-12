package org.simulationlogin.util;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

public class ConnectionManager {
	/** 连接超时时间 */
	static final int TIMEOUT = 30000;
	/** 数据传输超时 */
	static final int SO_TIMEOUT = 30000;

	static String UA = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1"
			+ " (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1";

	private ConnectionManager() {

	}

	public static DefaultHttpClient getHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient(); // 创建默认的httpClient实例
		try {

			X509TrustManager xtm = new X509TrustManager() { // 创建TrustManager
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
				public void verify(String arg0, String[] arg1, String[] arg2)
						throws SSLException {
				}

				public void verify(String arg0, X509Certificate arg1)
						throws SSLException {
				}

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}

				@Override
				public void verify(String host, SSLSocket ssl)
						throws IOException {
				}
			};

			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { xtm }, null);
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx,
					hostnameVerifier);
			httpClient.getConnectionManager().getSchemeRegistry()
					.register(new Scheme("https", 443, socketFactory));
			
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);
			params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
			params.setParameter(CoreProtocolPNames.USER_AGENT, UA);
			httpClient.setParams(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpClient;
	}
}
