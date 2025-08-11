import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import api from '../services/api';
import ImageUploader from '../components/common/ImageUploader';

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    profileImageUrl: null, // We'll hold the final URL here
  });
  const [selectedFile, setSelectedFile] = useState(null); // State to hold the file object
  const [isUploading, setIsUploading] = useState(false); // State for loading indicator
  const navigate = useNavigate();

  // Justification: Cloudinary credentials from your account.
  const cloudName = "drg9gqbxd";
  const unsignedUploadPreset = "imageUpload";

  // Justification: A simple state handler for all form inputs.
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };
  
  // Justification: A callback from the ImageUploader component to receive the selected file.
  const handleFileSelect = (file) => {
    setSelectedFile(file);
  };

  // Justification: This single function handles the entire form submission.
  // It checks for a file, uploads it to Cloudinary, and then registers the user.
  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsUploading(true);

    let imageUrl = '';
    // Justification: If a file is selected, upload it to Cloudinary first.
    if (selectedFile) {
      const formData = new FormData();
      formData.append('file', selectedFile);
      formData.append('upload_preset', unsignedUploadPreset);

      try {
        const response = await fetch(
          `https://api.cloudinary.com/v1_1/${cloudName}/image/upload`,
          {
            method: 'POST',
            body: formData,
          }
        );
        const data = await response.json();
        imageUrl = data.secure_url;
      } catch (error) {
        toast.error("Image upload failed.");
        setIsUploading(false);
        return; // Stop the registration process
      }
    }

    try {
      // Justification: Send the user data, including the image URL (if uploaded), to the backend.
      await api.post('/auth/register/customer', { ...formData, profileImageUrl: imageUrl });
      toast.success('Registration successful! Please log in.');
      navigate('/login');
    } catch (error) {
      console.error(error);
    } finally {
      setIsUploading(false); // Always reset loading state
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card">
            <div className="card-header text-center">
              <h3>Register</h3>
            </div>
            <div className="card-body">
              <form onSubmit={handleSubmit}>
                <div className="form-group mb-3">
                  <label htmlFor="firstName">First Name</label>
                  <input type="text" className="form-control" id="firstName" name="firstName" value={formData.firstName} onChange={handleChange} required />
                </div>
                <div className="form-group mb-3">
                  <label htmlFor="lastName">Last Name</label>
                  <input type="text" className="form-control" id="lastName" name="lastName" value={formData.lastName} onChange={handleChange} required />
                </div>
                <div className="form-group mb-3">
                  <label htmlFor="username">Username</label>
                  <input type="text" className="form-control" id="username" name="username" value={formData.username} onChange={handleChange} required />
                </div>
                <div className="form-group mb-3">
                  <label htmlFor="email">Email</label>
                  <input type="email" className="form-control" id="email" name="email" value={formData.email} onChange={handleChange} required />
                </div>
                <div className="form-group mb-3">
                  <label htmlFor="password">Password</label>
                  <input type="password" className="form-control" id="password" name="password" value={formData.password} onChange={handleChange} required />
                </div>
                <div className="form-group mb-3">
                  <label htmlFor="address">Address</label>
                  <input type="text" className="form-control" id="address" name="address" value={formData.address} onChange={handleChange} />
                </div>
                 <div className="form-group mb-3">
                                    <label htmlFor="phone">Phone</label>
                                    <input
                                        type="tel"
                                        className="form-control"
                                        id="phone"
                                        name="phone"
                                        value={formData.phone}
                                        onChange={handleChange}
                                        // Justification: This pattern attribute is a client-side validation
                                        // that enforces a 10-digit number. It provides immediate feedback
                                        // to the user. The 'title' attribute provides a helpful tooltip.
                                        pattern="\d{10}"
                                        title="Phone number must be a 10-digit number."
                                    />
                                </div>

                <ImageUploader onFileSelect={handleFileSelect} />
                
                <button type="submit" className="btn btn-primary w-100" disabled={isUploading}>
                  {isUploading ? 'Registering...' : 'Register'}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;