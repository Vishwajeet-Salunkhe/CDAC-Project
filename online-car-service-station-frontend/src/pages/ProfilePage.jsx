import { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { toast } from 'react-toastify';
import ImageUploader from '../components/common/ImageUploader';
import { logout } from '../store/authSlice';

const ProfilePage = () => {
    const { isAuthenticated, user } = useSelector(state => state.auth);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        firstName: '',
        lastName: '',
        address: '',
        phone: '',
        profileImageUrl: '',
        password: '',
    });
    const [selectedFile, setSelectedFile] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const cloudName = "drg9gqbxd";
    const unsignedUploadPreset = "imageUpload";

    useEffect(() => {
        if (!isAuthenticated) {
            navigate('/login');
            return;
        }
        fetchUserProfile();
    }, [isAuthenticated, navigate]);

    const fetchUserProfile = async () => {
        try {
            const response = await api.get('/users/me');
            const userDetails = response.data;
            setFormData({
                username: userDetails.username,
                email: userDetails.email,
                firstName: userDetails.firstName,
                lastName: userDetails.lastName,
                address: userDetails.address,
                phone: userDetails.phone,
                profileImageUrl: userDetails.profileImageUrl || '',
                password: '',
            });
        } catch (error) {
            console.error(error);
            toast.error("Failed to fetch user profile.");
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleFileSelect = (file) => {
        setSelectedFile(file);
    };

    // Justification: This single function handles all profile updates.
    // After a successful update, it logs the user out and redirects,
    // which is the most robust and secure approach.
    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        let imageUrl = formData.profileImageUrl;
        if (selectedFile) {
            const uploadFormData = new FormData();
            uploadFormData.append('file', selectedFile);
            uploadFormData.append('upload_preset', unsignedUploadPreset);

            try {
                const response = await fetch(
                    `https://api.cloudinary.com/v1_1/${cloudName}/image/upload`,
                    { method: 'POST', body: uploadFormData }
                );
                const data = await response.json();
                imageUrl = data.secure_url;
            } catch (error) {
                toast.error("Image upload failed.");
                setIsSubmitting(false);
                return;
            }
        }
        
        try {
            const isCustomer = user?.roles.includes('ROLE_CUSTOMER');
            const updateUrl = isCustomer ? '/users/me/customer' : '/users/me/admin';
            
            const updateData = { ...formData, profileImageUrl: imageUrl };
            if (updateData.password === '') {
                delete updateData.password;
            }
            
            await api.put(updateUrl, updateData);

            // Justification: CRITICAL FIX. We now consistently log the user out after
            // a successful profile update. This is the most secure and reliable way to ensure the
            // authentication state and token are always up-to-date with the backend.
            dispatch(logout());
            toast.success("Profile updated successfully! Please log in with your new details.");
            navigate('/login');

        } catch (error) {
            console.error(error);
            toast.error("Failed to update profile: " + (error.response?.data?.message || error.message));
        } finally {
            setIsSubmitting(false);
        }
    };

    if (!user) {
        return <div className="container mt-5 text-center">Loading profile...</div>;
    }

    return (
        <div className="container mt-5">
            <h2 className="text-center mb-4">Update Profile Details</h2>
            <div className="row justify-content-center">
                <div className="col-md-6">
                    <div className="card">
                        <div className="card-body">
                            <form onSubmit={handleSubmit}>
                                <div className="form-group mb-3">
                                    <label htmlFor="username">Username</label>
                                    <input type="text" className="form-control" name="username" value={formData.username} onChange={handleChange} required />
                                </div>
                                <div className="form-group mb-3">
                                    <label htmlFor="email">Email</label>
                                    <input type="email" className="form-control" name="email" value={formData.email} onChange={handleChange} required />
                                </div>
                                <div className="form-group mb-3">
                                    <label htmlFor="firstName">First Name</label>
                                    <input type="text" className="form-control" name="firstName" value={formData.firstName} onChange={handleChange} required />
                                </div>
                                <div className="form-group mb-3">
                                    <label htmlFor="lastName">Last Name</label>
                                    <input type="text" className="form-control" name="lastName" value={formData.lastName} onChange={handleChange} required />
                                </div>
                                {user?.roles.includes('ROLE_CUSTOMER') && (
                                    <>
                                        <div className="form-group mb-3">
                                            <label htmlFor="address">Address</label>
                                            <input type="text" className="form-control" name="address" value={formData.address} onChange={handleChange} />
                                        </div>
                                        <div className="form-group mb-3">
                                            <label htmlFor="phone">Phone</label>
                                            <input type="tel" className="form-control" name="phone" value={formData.phone} onChange={handleChange} />
                                        </div>
                                    </>
                                )}
                                <div className="form-group mb-3">
                                    <label htmlFor="password">New Password</label>
                                    <input type="password" className="form-control" id="password" name="password" value={formData.password} onChange={handleChange} placeholder="Leave blank to keep current password" />
                                </div>
                                <div className="form-group mb-3">
                                    <label>Current Profile Image</label>
                                    {formData.profileImageUrl ? (
                                        <img src={formData.profileImageUrl} alt="Current Profile" className="img-thumbnail d-block mb-2" style={{ maxWidth: '150px' }} />
                                    ) : (
                                        <p>No profile image uploaded.</p>
                                    )}
                                    <ImageUploader onFileSelect={handleFileSelect} />
                                </div>
                                <button type="submit" className="btn btn-primary w-100" disabled={isSubmitting}>
                                    {isSubmitting ? 'Updating...' : 'Update Profile'}
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ProfilePage;