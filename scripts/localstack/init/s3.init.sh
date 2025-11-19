#!/bin/bash
echo "Creating S3 buckets..."

awslocal s3 mb s3://tompang-carpool
awslocal s3api put-bucket-cors --bucket tompang-carpool --cors-configuration '{
  "CORSRules": [
    {
      "AllowedHeaders": ["*"],
      "AllowedMethods":  ["GET", "PUT", "POST", "DELETE"],
      "AllowedOrigins": ["*"],
      "ExposeHeaders": ["ETag"]
    }
  ]
}'
echo "Done creating buckets."
