// Importing `createSlice` from Redux Toolkit, which simplifies slice creation including reducers and actions.
import { createSlice } from '@reduxjs/toolkit';

// Retrieve the user data (if any) from localStorage and parse it into an object.
// This allows the app to persist login state across page reloads.
const user = JSON.parse(localStorage.getItem('user'));

// Define the initial state for authentication.
// If user exists in localStorage, initialize the state accordingly.
const initialState = {
  user: user ? user : null,               // Stores user object if logged in, else null.
  isAuthenticated: user ? true : false,   // Boolean indicating whether the user is authenticated.
};

// Creating the authentication slice using `createSlice`.
const authSlice = createSlice({
  name: 'auth',           // Slice name used in the Redux state tree.
  initialState,           // Set the slice's initial state.
  reducers: {             // Define reducer functions to handle actions.

    // The `login` reducer sets user info and authentication status upon successful login.
    login: (state, action) => {
      state.user = action.payload;        // Save the user data from the action payload to state.
      state.isAuthenticated = true;       // Set the authenticated flag to true.
      localStorage.setItem('user', JSON.stringify(action.payload)); // Persist user data in localStorage for session continuity.
    },

    // The `logout` reducer clears user data from state and localStorage on logout.
    logout: (state) => {
      state.user = null;                  // Remove user from state.
      state.isAuthenticated = false;      // Set the authenticated flag to false.
      localStorage.removeItem('user');    // Clear user data from localStorage to prevent auto-login on refresh.
    },
  },
});

// Exporting the generated action creators (`login` and `logout`) so they can be dispatched from components.
export const { login, logout } = authSlice.actions;

// Exporting the reducer to be added to the Redux store.
export default authSlice.reducer;
