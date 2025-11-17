# Use the official Node.js image as the base image
FROM node:23-alpine

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy package.json and package-lock.json to the working directory
COPY notification-service/package*.json ./notification-service/

# Install dependencies (layer will be cached unless package.json changes)
WORKDIR /usr/src/app/notification-service
RUN npm install

WORKDIR /usr/src/app
COPY notification-service ./notification-service
COPY schemas ./schemas

# Build the NestJS application
WORKDIR /usr/src/app/notification-service
RUN npm run build

# Command to run the application
CMD ["node", "dist/main"]