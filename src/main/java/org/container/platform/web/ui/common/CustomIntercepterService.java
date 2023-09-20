package org.container.platform.web.ui.common;


import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.container.platform.web.ui.security.DashboardAuthenticationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Map;


@Service
public class CustomIntercepterService {


	 private static final Logger LOGGER = LoggerFactory.getLogger(CustomIntercepterService.class);
	 
			 
	@Value("${keycloak.oauth.client.id}")
    private String clientId;

    @Value("${keycloak.oauth.client.secret}")
    private String clientSecret;

    @Value("${keycloak.oauth.info.uri}")
    private String oauthInfoUrl;

    @Value("${keycloak.oauth.token.check.uri}")
    private String checkTokenUri;

    @Value("${keycloak.oauth.authorization.uri}")
    private String authorizationUri;

    @Value("${keycloak.oauth.token.access.uri}")
    private String accessUri;

    @Value("${keycloak.oauth.logout.url}")
    private String logoutUrl;



	/**
	 * Gets user info.
	 *
	 * @return the user info
	 */
    public ActiveStatus isActive() {

		ActiveStatus activeStatus = new ActiveStatus();

    	boolean bFlag =false;
    	
         try {
			 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			 if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Constants.AUTH_INACTIVE_USER))) {
				 activeStatus = new ActiveStatus(false, Constants.AUTH_INACTIVE_USER);
				 return activeStatus;
			 }

        	  String token = ((DashboardAuthenticationDetails) authentication.getDetails()).getTokenValue();
        	  
        	  MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        	  parameters.add("token", token);
        	  parameters.add("client_id", clientId);
        	  parameters.add("client_secret", clientSecret);
              
			Map result = send(checkTokenUri, HttpMethod.POST, parameters);
			if(result!=null) {
				
				boolean active = (boolean) result.get("active");
				
				if(active)
					bFlag =true;
				
			}
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bFlag =false;
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bFlag =false;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bFlag =false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bFlag =false;
		}

		activeStatus.setActive(bFlag);
         return activeStatus;
         
    }



	/**
	 * user logout.
	 */
	public void logout() {
		LOGGER.info("#### USER KEYCLOAK LOGOUT");
		try {
			DashboardAuthenticationDetails user = ((DashboardAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails());
			String accessToken = user.getTokenValue();
			String refreshToken =  user.getAccessToken().getRefreshToken().getValue();

			MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
			parameters.add("token", accessToken);
			parameters.add("refresh_token", refreshToken);
			parameters.add("client_id", clientId);
			parameters.add("client_secret", clientSecret);

			send(logoutUrl, HttpMethod.POST, parameters);

		} catch (Exception e) {
			LOGGER.info("keycloak logout send :: Response Type: {}", CommonUtils.loggerReplace(e.getMessage()));
		}
	}



	private Map send(String reqUrl, HttpMethod httpMethod, Object bodyObject) throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {


    	RestTemplate restTemplate = restTemplate();
    	
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        reqHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        
        HttpEntity<Object> reqEntity = new HttpEntity<>(bodyObject, reqHeaders);

        LOGGER.debug("POST >> Request: {}, {baseUrl} : {}, Content-Type: {}", CommonUtils.loggerReplace(HttpMethod.POST.toString()), CommonUtils.loggerReplace(reqUrl));
        ResponseEntity<Map> resEntity = restTemplate.exchange(reqUrl, httpMethod, reqEntity, Map.class);

		if(resEntity.getBody() != null) {
			LOGGER.debug("Map send :: Response Type: {}", CommonUtils.loggerReplace(resEntity.getBody().getClass()));
		}

        return resEntity.getBody();
    }
    
    private RestTemplate restTemplate()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
			
			SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
			                .loadTrustMaterial(null, acceptingTrustStrategy)
			                .build();
			
			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
			
			CloseableHttpClient httpClient = HttpClients.custom()
			                .setSSLSocketFactory(csf)
			                .build();
			
			HttpComponentsClientHttpRequestFactory requestFactory =
			                new HttpComponentsClientHttpRequestFactory();
			
			requestFactory.setHttpClient(httpClient);
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			
			restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		    
			return restTemplate;
}


    
    
}
