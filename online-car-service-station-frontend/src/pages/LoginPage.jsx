// Importing useState hook to manage form input state.
import { useState } from 'react';

// useDispatch is used to dispatch Redux actions like login().
import { useDispatch } from 'react-redux';

// useNavigate hook from React Router to navigate programmatically after login.
import { useNavigate } from 'react-router-dom';

// toast is used to display success or error notifications.
import { toast } from 'react-toastify';

// Importing the pre-configured axios instance with interceptors.
import api from '../services/api';

// Importing the login action from authSlice to update Redux store with user data.
import { login } from '../store/authSlice';

// Functional component for the login page.
const LoginPage = () => {
  // Initializing local component state to track username and password input values.
  const [formData, setFormData] = useState({ username: '', password: '' });

  // Initializing dispatch to send actions to the Redux store.
  const dispatch = useDispatch();

  // Initializing navigate to redirect user on successful login.
  const navigate = useNavigate();

  // Event handler to update form input values in the state.
  const handleChange = (e) => {
    // Using spread operator to update only the changed field, keeping the rest unchanged.
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // Justification: This function handles the form submission process.
  // It prevents default form behavior, sends login data to the backend,
  // updates Redux state on success, and navigates the user accordingly.
  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevents the default form submit (which would reload the page).
    try {
      // Sends a POST request to the backend with username and password.
      const response = await api.post('/auth/login', formData);

      // Dispatches the login action with user data returned from the backend.
      dispatch(login(response.data));

      // Shows a success message using toast notification.
      toast.success('Login successful!');

      // Navigates the user to the homepage after login.
      navigate('/');
    } catch (error) {
      // Error toast is already handled globally in the API interceptor.
      // This catch block just ensures that any unhandled error won't crash the app.
      console.error(error);
    }
  };

  // JSX that renders the login form UI using Bootstrap classes.
  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card">
            <div className="card-header text-center">
              <h3>Login</h3>
            </div>
            <div className="card-body">
              {/* Form submit handler attached to form element */}
              <form onSubmit={handleSubmit}>
                
                {/* Username input field */}
                <div className="form-group mb-3">
                  <label htmlFor="username">Username</label>
                  <input
                    type="text"
                    className="form-control"
                    id="username"
                    name="username"
                    value={formData.username}      // Controlled component
                    onChange={handleChange}        // Updates state on change
                    required                        // Makes field mandatory
                  />
                </div>

                {/* Password input field */}
                <div className="form-group mb-3">
                  <label htmlFor="password">Password</label>
                  <input
                    type="password"
                    className="form-control"
                    id="password"
                    name="password"
                    value={formData.password}      // Controlled component
                    onChange={handleChange}        // Updates state on change
                    required                        // Makes field mandatory
                  />
                </div>

                {/* Submit button for form */}
                <button type="submit" className="btn btn-primary w-100">
                  Login
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

// Exporting the component so it can be used in routing or other parent components.
export default LoginPage;
