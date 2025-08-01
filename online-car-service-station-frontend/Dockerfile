# Justification: Multi-stage build. Stage 1 is for building the React app.
# Use the Node.js base image to install dependencies and build the project.
FROM node:18-alpine as builder

# Set the working directory inside the container.
WORKDIR /app

# Copy the project files.
COPY package.json .
COPY . .

# Justification: Install dependencies and build the React application for production.
# The `npm run build` command creates the 'dist' folder with static files.
RUN npm install
RUN npm run build

# Stage 2: Serve the static files using a lightweight NGINX server.
# Use the official NGINX image.
FROM nginx:alpine

# Copy the built files from the 'builder' stage into the NGINX web root.
# The 'dist' folder contains the final, optimized build of your React app.
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port 80 for NGINX.
EXPOSE 80

# Justification: This command runs NGINX in the foreground.
CMD ["nginx", "-g", "daemon off;"]