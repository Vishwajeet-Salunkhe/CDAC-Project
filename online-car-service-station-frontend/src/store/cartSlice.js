// Justification: A new slice to manage the customer's selected services (the "cart").
// This is a perfect use case for Redux as this state needs to be accessed by multiple components.
import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  items: [], // Array of selected service objects
};

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    // Justification: Adds a service to the cart. It prevents adding duplicates.
    addToCart: (state, action) => {
      const service = action.payload;
      const existingItem = state.items.find(item => item.id === service.id);
      if (!existingItem) {
        state.items.push(service);
      }
    },
    // Justification: Removes a service from the cart.
    removeFromCart: (state, action) => {
      state.items = state.items.filter(item => item.id !== action.payload.id);
    },
    // Justification: Clears the entire cart after a booking is confirmed.
    clearCart: (state) => {
      state.items = [];
    },
  },
});

export const { addToCart, removeFromCart, clearCart } = cartSlice.actions;

export default cartSlice.reducer;

