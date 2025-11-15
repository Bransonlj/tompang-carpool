package com.tompang.carpool.geospatial_service.onemap;

import java.time.Duration;
import java.time.Instant;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.tompang.carpool.geospatial_service.common.exception.OnemapApiException;
import com.tompang.carpool.geospatial_service.onemap.dto.OnemapAuthRequestDto;
import com.tompang.carpool.geospatial_service.onemap.dto.OnemapAuthResponseDto;
import com.tompang.carpool.geospatial_service.onemap.dto.OnemapGeocodeResponseDto;


@Service
public class OnemapService {
    private final RestClient restClient;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String AUTH_TOKEN_STRING = "onemap:authtoken";

    public OnemapService(RestClient.Builder builder, RedisTemplate<String, String> redisTemplate) {
        this.restClient = builder
                .baseUrl("https://www.onemap.gov.sg")
                .build();
        this.redisTemplate = redisTemplate;
    }

    private String authorize() throws OnemapApiException {
        try {
            String cachedString = this.redisTemplate.opsForValue().get(AUTH_TOKEN_STRING);
            if (cachedString != null) {
                return cachedString;
            }

            OnemapAuthResponseDto response = restClient.post()
                .uri("/api/auth/post/getToken")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new OnemapAuthRequestDto("bransonprojectemail@gmail.com", "onemapAPI1232!")) // TODO move to env
                .retrieve()
                .body(OnemapAuthResponseDto.class);

             // Calculate TTL
            long expiryTimestamp = Long.parseLong(response.expiryTimestamp);
            long ttlSeconds = expiryTimestamp - Instant.now().getEpochSecond();
            this.redisTemplate.opsForValue().set(AUTH_TOKEN_STRING, response.accessToken, Duration.ofSeconds(ttlSeconds));
            return response.accessToken;
        } catch (HttpClientErrorException e) {
            // 4xx errors
            throw new OnemapApiException("Client error: " + e.getStatusCode() + " -> " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            // 5xx errors
            throw new OnemapApiException("Server error: " + e.getStatusCode() + " -> " + e.getResponseBodyAsString());
        } catch (RestClientResponseException e) {
            // Any other unexpected status code
            throw new OnemapApiException("Other HTTP error: " + e.getStatusCode());
        }
    }

    public byte[] staticImage(double latitude, double longitude) throws OnemapApiException {
        try {
            byte[] image = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/staticmap/getStaticImage")
                            .queryParam("layerchosen", "default")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("zoom", 17)
                            .queryParam("width", 512)
                            .queryParam("height", 512)
                            .queryParam("points", String.format("[%f,%f]", latitude, longitude))
                            .build())
                    .exchange((request, response) -> {
                        // OneMapAPI returns 200OK even if there is an error. Error message is in body.
                        MediaType contentType = response.getHeaders().getContentType();
                        if (contentType == null || !contentType.includes(MediaType.IMAGE_PNG)) {
                            byte[] errorBytes = response.bodyTo(byte[].class);
                            String errorText = new String(errorBytes);
                            throw new OnemapApiException(
                                    "Static map API returned non-image response: " + errorText
                            );
                        }

                        return response.bodyTo(byte[].class);
                    });
            return image;
        } catch (HttpClientErrorException.TooManyRequests e) {
            // 429 error
            // TODO retry on 429
            throw new OnemapApiException("Client error: " + e.getStatusCode() + " -> " + e.getResponseBodyAsString());
        } catch (HttpClientErrorException e) {
            // 4xx errors
            throw new OnemapApiException("Client error: " + e.getStatusCode() + " -> " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            // 5xx errors
            throw new OnemapApiException("Server error: " + e.getStatusCode() + " -> " + e.getResponseBodyAsString());
        } catch (RestClientResponseException e) {
            // Any other unexpected status code
            throw new OnemapApiException("Other HTTP error: " + e.getStatusCode());
        }
    }    

    public OnemapGeocodeResponseDto reverseGeocode(double latitude, double longitude) throws OnemapApiException {
        try {
            String authToken = authorize();
            OnemapGeocodeResponseDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/public/revgeocode")
                        .queryParam("location", latitude + "," + longitude)
                        .queryParam("buffer", 50) // use a buffer of 50
                        .queryParam("addressType", "All")
                        .queryParam("otherFeatures", "N")
                        .build())
                .header("Authorization", authToken)
                .retrieve()
                .body(OnemapGeocodeResponseDto.class);
            return response;
        } catch (HttpClientErrorException.TooManyRequests e) {
            // 429 error
            // TODO retry on 429
            throw new OnemapApiException("Client error: " + e.getStatusCode() + " -> " + e.getResponseBodyAsString());
        } catch (HttpClientErrorException e) {
            // 4xx errors
            throw new OnemapApiException("Client error: " + e.getStatusCode() + " -> " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            // 5xx errors
            throw new OnemapApiException("Server error: " + e.getStatusCode() + " -> " + e.getResponseBodyAsString());
        } catch (RestClientResponseException e) {
            // Any other unexpected status code
            throw new OnemapApiException("Other HTTP error: " + e.getStatusCode());
        }

    }
}
