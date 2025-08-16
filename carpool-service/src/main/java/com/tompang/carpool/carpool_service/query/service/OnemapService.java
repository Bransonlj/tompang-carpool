package com.tompang.carpool.carpool_service.query.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.tompang.carpool.carpool_service.command.domain.LatLong;
import com.tompang.carpool.carpool_service.common.exceptions.OnemapApiException;
import com.tompang.carpool.carpool_service.query.dto.geocode.GeocodeResponse;
import com.tompang.carpool.carpool_service.query.dto.geocode.OnemapAuthRequestDto;
import com.tompang.carpool.carpool_service.query.dto.geocode.OnemapAuthResponseDto;

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

    public GeocodeResponse reverseGeocode(LatLong location) throws OnemapApiException {
        try {
            String authToken = authorize();
            GeocodeResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/public/revgeocode")
                        .queryParam("location", location.latitude + "," + location.longitude)
                        .queryParam("buffer", 50) // use a buffer of 50
                        .queryParam("addressType", "All")
                        .queryParam("otherFeatures", "N")
                        .build())
                .header("Authorization", authToken)
                .retrieve()
                .body(GeocodeResponse.class);
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
