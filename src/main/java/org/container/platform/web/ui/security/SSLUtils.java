package org.container.platform.web.ui.security;

import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/*
    resttemplate로 api 요청시 SSL 유효성 체크를 건너뜀
 */
public final class SSLUtils {
    //private static final Logger LOGGER = LoggerFactory.getLogger(SSLUtils.class);
    static {
        //for localhost testing only
        HttpsURLConnection.setDefaultHostnameVerifier(
            new HostnameVerifier(){

                public boolean verify(String hostname, SSLSession sslSession) {
                    if (hostname.equals("localhost")) {
                        return true;
                    }
                    return false;
                }
            });
    }


    public  static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

    public static void turnOnSslChecking() throws KeyManagementException, NoSuchAlgorithmException {
        // Return it to the initial state (discovered by reflection, now hardcoded)
        SSLContext.getInstance("SSL").init( null, null, null );
    }

    private SSLUtils(){
        throw new UnsupportedOperationException( "Do not instantiate libraries.");
    }
}