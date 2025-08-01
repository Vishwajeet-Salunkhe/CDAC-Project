import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import { Provider } from 'react-redux';
import { store } from './store/store'; // Will be created in Module 2
import App from './App.jsx';
import './index.css';
import 'react-toastify/dist/ReactToastify.css';

// Justification: This is the application's entry point.
// We wrap the entire application in necessary providers.
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    {/* Justification: BrowserRouter enables client-side routing. */}
    <BrowserRouter>
      {/* Justification: Redux Provider makes the global store available to all components. */}
      <Provider store={store}>
        <App />
        {/* Justification: ToastContainer is a component that displays notifications (toasts)
            for user feedback on successful or failed actions. */}
        <ToastContainer position="bottom-right" autoClose={3000} />
      </Provider>
    </BrowserRouter>
  </React.StrictMode>
);