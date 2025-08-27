package com.tompang.carpool.driver_verification_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tompang.carpool.driver_verification_service.dto.VerificationResult;

import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectTextRequest;
import software.amazon.awssdk.services.rekognition.model.DetectTextResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.TextDetection;

@Service
public class VerificationService {

    private final RekognitionClient rekognitionClient;
    private final static float UPPER_CONFIDENCE_THRESHOLD = 90f;
    private final static float LOWER_CONFIDENCE_THRESHOLD = 50f;

    public VerificationService(RekognitionClient rekognitionClient) {
        this.rekognitionClient = rekognitionClient;
    }

    public VerificationResult verifyRegistrationNumber(S3Object imageObject, String expectedRegistrationNumber) {
        DetectTextRequest request = DetectTextRequest.builder()
            .image(Image.builder()
                .s3Object(imageObject)
                .build())
            .build();

        DetectTextResponse textResponse = rekognitionClient.detectText(request);
        List<TextDetection> textCollection = textResponse.textDetections();
        StringBuilder upperConfidenceTextBuilder = new StringBuilder();
        StringBuilder lowerConfidenceTextBuilder = new StringBuilder();
        for (TextDetection text: textCollection) {
            if (text.confidence() >= UPPER_CONFIDENCE_THRESHOLD) {
                upperConfidenceTextBuilder.append(text.detectedText());
                lowerConfidenceTextBuilder.append(text.detectedText());
            } else if (text.confidence() >= LOWER_CONFIDENCE_THRESHOLD) {
                lowerConfidenceTextBuilder.append(text.detectedText());
            }
        }

        if (upperConfidenceTextBuilder.toString().contains(expectedRegistrationNumber)) {
            return VerificationResult.VALID;
        }

        if (lowerConfidenceTextBuilder.toString().contains(expectedRegistrationNumber)) {
            return VerificationResult.UNSURE;
        }

        return VerificationResult.INVALID;
    }

}
