import { configureStore } from '@reduxjs/toolkit';
import authReducer from './authSlice';
import cartReducer from './cartSlice'; // <-- Import the new slice

export const store = configureStore({
  reducer: {
    auth: authReducer,
    cart: cartReducer, // <-- Add the new cart reducer
  },
});