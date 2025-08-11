import { useState, useEffect } from 'react';
import api from '../services/api';
import { toast } from 'react-toastify';
import ImageUploader from '../components/common/ImageUploader';

// Justification: This component provides the full administrative interface for managing services.
// It uses useState to manage the list of services and the state of the form.
const ServiceManagement = () => {
    const [services, setServices] = useState([]);
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        price: '',
        imageUrl: ''
    });
    const [editingServiceId, setEditingServiceId] = useState(null);
    const [selectedFile, setSelectedFile] = useState(null);
    const [isUploading, setIsUploading] = useState(false);

    const cloudName = "drg9gqbxd"; // Replace with your Cloudinary cloud name
    const unsignedUploadPreset = "imageUpload"; // Replace with your unsigned upload preset

    // Justification: The useEffect hook fetches all services from the backend when the component
    // is first rendered. This keeps the list of services up-to-date.
    useEffect(() => {
        fetchServices();
    }, []);

    // Justification: Asynchronous function to get all services from the backend.
    const fetchServices = async () => {
        try {
            const response = await api.get('/services');
            setServices(response.data);
        } catch (error) {
            console.error(error);
            toast.error("Failed to fetch services.");
        }
    };

    // Justification: Handles changes to the form fields.
    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // Justification: Handles file selection from the ImageUploader.
    const handleFileSelect = (file) => {
        setSelectedFile(file);
    };

    // Justification: Handles form submission for adding or editing a service.
    // This is a complex function that handles both image upload and API calls atomically.
    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsUploading(true);

        let imageUrl = formData.imageUrl;
        // Justification: If a new image file is selected, upload it to Cloudinary first.
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
                setIsUploading(false);
                return;
            }
        }
        
        try {
            const serviceData = { ...formData, imageUrl, price: parseFloat(formData.price) };
            if (editingServiceId) {
                // Justification: If we are editing, send a PUT request.
                await api.put(`/services/${editingServiceId}`, serviceData);
                toast.success("Service updated successfully!");
            } else {
                // Justification: Otherwise, send a POST request to create a new service.
                await api.post('/services', serviceData);
                toast.success("Service created successfully!");
            }
            // Reset the form and refresh the service list.
            setFormData({ name: '', description: '', price: '', imageUrl: '' });
            setEditingServiceId(null);
            setSelectedFile(null);
            fetchServices();
        } catch (error) {
            console.error(error);
            toast.error("Operation failed: " + (error.response?.data || error.message));
        } finally {
            setIsUploading(false);
        }
    };
    
    // Justification: Sets the form data for editing an existing service.
    const handleEdit = (service) => {
        setFormData({
            name: service.name,
            description: service.description,
            price: service.price.toString(),
            imageUrl: service.imageUrl
        });
        setEditingServiceId(service.id);
    };

    // Justification: Deletes a service by its ID.
    const handleDelete = async (id) => {
        try {
            await api.delete(`/services/${id}`);
            toast.success("Service deleted successfully!");
            fetchServices();
        } catch (error) {
            console.error(error);
            toast.error("Failed to delete service.");
        }
    };

    return (
        <div className="container mt-5">
            <h2 className="text-center mb-4">Manage Car Services</h2>
            
            {/* Service Form for adding/editing */}
            <div className="card mb-4">
                <div className="card-header">
                    <h3>{editingServiceId ? 'Edit Service' : 'Add New Service'}</h3>
                </div>
                <div className="card-body">
                    <form onSubmit={handleSubmit}>
                        <div className="form-group mb-3">
                            <label htmlFor="name">Service Name</label>
                            <input type="text" className="form-control" name="name" value={formData.name} onChange={handleChange} required />
                        </div>
                        <div className="form-group mb-3">
                            <label htmlFor="description">Description</label>
                            <textarea className="form-control" name="description" value={formData.description} onChange={handleChange} required />
                        </div>
                        <div className="form-group mb-3">
                            <label htmlFor="price">Price</label>
                            <input type="number" step="0.01" className="form-control" name="price" value={formData.price} onChange={handleChange} required />
                        </div>
                        <ImageUploader onFileSelect={handleFileSelect} />
                        {formData.imageUrl && (
                            <div className="my-2">
                                <img src={formData.imageUrl} alt="Current" style={{ maxWidth: '100px' }} />
                                <small className="d-block">Current Image</small>
                            </div>
                        )}
                        <button type="submit" className="btn btn-primary w-100" disabled={isUploading}>
                            {isUploading ? 'Saving...' : (editingServiceId ? 'Update Service' : 'Add Service')}
                        </button>
                    </form>
                </div>
            </div>

            {/* Services Table */}
            <div className="card">
                <div className="card-header">
                    <h3>Current Services</h3>
                </div>
                <div className="card-body">
                    <div className="table-responsive">
                        <table className="table table-striped">
                            <thead>
                                <tr>
                                    <th>Image</th>
                                    <th>Name</th>
                                    <th>Description</th>
                                    <th>Price</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {services.map(service => (
                                    <tr key={service.id}>
                                        <td>
                                            {service.imageUrl && (
                                                <img src={service.imageUrl} alt={service.name} style={{ width: '50px', height: '50px', objectFit: 'cover' }} />
                                            )}
                                        </td>
                                        <td>{service.name}</td>
                                        <td>{service.description}</td>
                                        <td>₹{service.price.toFixed(2)}</td>
                                        <td>
                                            <button className="btn btn-warning btn-sm me-2" onClick={() => handleEdit(service)}>Edit</button>
                                            <button className="btn btn-danger btn-sm" onClick={() => handleDelete(service.id)}>Delete</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ServiceManagement;