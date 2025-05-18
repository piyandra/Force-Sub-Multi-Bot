package org.telegram.forcesubmultibot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class NgrokService {

	private static final Logger log = LoggerFactory.getLogger(NgrokService.class);
	@Value("${ngrok.api.url:http://localhost:4040/api/tunnels}")
	private String ngrokApiUrl;
	private final RestTemplate restTemplate;
	@Value("${server.port}")
	private String port;

	public NgrokService() {
		this.restTemplate = new RestTemplate();
	}

	public void SetWebhook(String token) {
		try {
			String tunnelJsonInfo = getTunnelInfo();
			if (tunnelJsonInfo == null) {
				log.error("Failed to get tunnel information from ngrok");
				return;
			}
			String publicUrl = extractPublicUrl(tunnelJsonInfo);
			if (publicUrl != null) {
				setTelegramWebHook(publicUrl, token);
			}
		} catch (Exception e) {
			log.error("Error setting Telegram webhook: {}", e.getMessage());
		}
	}
	public String getTunnelInfo() {
		try {
			return restTemplate.getForObject(ngrokApiUrl, String.class);
		} catch (RestClientException e) {
			try {
				String alternateUrl = ngrokApiUrl.replace("4040", port);
				return restTemplate.getForObject(alternateUrl, String.class);
			} catch (RestClientException e2) {
				log.warn("Error getting tunnel info from ngrok: {}", e2.getMessage());
				return null;
			}
		}
	}
	private String extractPublicUrl(String tunnelJson) {
		try {
			JsonNode tunnelNode = new ObjectMapper().readTree(tunnelJson);
			JsonNode tunnels = tunnelNode.get("tunnels");

			if (tunnels != null && tunnels.isArray() && !tunnels.isEmpty()) {
				return tunnels.get(0).get("public_url").asText();
			} else {
				log.error("No tunnels found in ngrok response");
				return null;
			}
		} catch (Exception e) {
			log.error("Error extracting public URL: {}", e.getMessage());
			return null;
		}
	}
	private void setTelegramWebHook(String publicUrl, String botToken) {
		try {
			String webHookPath = "/webhook/" + botToken;
			String fullWebHookUrl = publicUrl + webHookPath;
			String setWebHookUrl = String.format("https://api.telegram.org/bot%s/setWebhook?url=%s",
					botToken, fullWebHookUrl);
			System.out.println(setWebHookUrl);

			String response = restTemplate.getForObject(setWebHookUrl, String.class);
			log.info("Webhook set response: {}", response);
		} catch (Exception e) {
			log.error("Error Get Telegram Setting Webhook: {}", e.getMessage());
		}
	}

}
