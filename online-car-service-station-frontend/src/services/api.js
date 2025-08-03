// Importing axios for HTTP requests.
import axios from 'axios';

// Importing toast notifications to display user-friendly error messages.
import { toast } from 'react-toastify';

// Defining the base URL of the backend API.
// This helps centralize the URL, so it can be reused throughout the application.
const API_URL = 'http://34.30.3.212:8080/api';

// Creating an axios instance with default configuration.
const api = axios.create({
  baseURL: API_URL, // Justification: Sets the root URL for all API calls made with this instance.
  headers: {
    'Content-Type': 'application/json', // Justification: Ensures JSON is used for both request and response bodies.
  },
});

// ------------------------
// Response Interceptor
// ------------------------
// Justification: This interceptor globally handles errors from all API responses.
// It allows centralized error handling and displays backend validation errors in toasts.
api.interceptors.response.use(
  (response) => response, // If the response is successful, just return it as-is.
  
  (error) => {
    // Define a fallback message in case no specific message is found.
    const defaultMessage = 'An unexpected error occurred.';
    let errorMessage = defaultMessage;

    // Check if the error came with a response from the backend.
    if (error.response) {
      // Case 1: The response contains a plain string error.
      if (typeof error.response.data === 'string') {
        errorMessage = error.response.data;

      // Case 2: The response has a `message` field (commonly used format).
      } else if (error.response.data.message) {
        errorMessage = error.response.data.message;

      // Case 3: The response contains validation errors in object format.
      // e.g., { "email": "Email is required", "password": "Must be at least 6 characters" }
      } else if (Object.values(error.response.data).length > 0) {
        const firstError = Object.values(error.response.data)[0]; // Get the first validation message.
        errorMessage = firstError;

      // Fallback: Display the HTTP status and status text as the error message.
      } else {
        errorMessage = `Server Error: ${error.response.status} - ${error.response.statusText}`;
      }
    }

    // Show the error message in a toast notification to inform the user.
    toast.error(errorMessage);

    // Reject the promise to allow further error handling downstream if needed.
    return Promise.reject(error);
  }
);

// ------------------------
// Request Interceptor
// ------------------------
// Justification: This interceptor adds the Authorization token (if present)
// to the headers of every outgoing request, enabling protected API access.
api.interceptors.request.use(
  (config) => {
    // Try to retrieve the logged-in user's token from localStorage.
    const user = JSON.parse(localStorage.getItem('user'));

    // If a user exists and has a token, attach it to the Authorization header.
    if (user && user.token) {
      config.headers.Authorization = `Bearer ${user.token}`; // Bearer Token authentication format.
    }

    return config; // Return the modified request config.
  },
  (error) => Promise.reject(error) // In case of request errors (e.g., config issues), reject the promise.
);

// Export the configured axios instance so it can be used across the app.
export default api;
