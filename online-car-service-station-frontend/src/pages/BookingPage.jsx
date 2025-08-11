import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { toast } from 'react-toastify';

const BookingPage = () => {
    const { serviceId } = useParams();
    const navigate = useNavigate();
    const [service, setService] = useState(null);
    const [bookingDateTime, setBookingDateTime] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Justification: The useEffect hook fetches the specific service details based on the ID from the URL.
    // This ensures the booking page displays the correct service information.
    useEffect(() => {
        const fetchService = async () => {
            try {
                const response = await api.get(`/services/${serviceId}`);
                setService(response.data);
            } catch (error) {
                console.error(error);
                toast.error("Service not found.");
                navigate('/services');
            }
        };
        fetchService();
    }, [serviceId, navigate]);

    // Justification: This function handles the booking submission. It calls the backend's
    // create booking endpoint with the service ID and selected date/time.
    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);
        try {
            await api.post('/bookings', {
                carServiceIds: [service.id], // Send an array of service IDs
                bookingDateTime
            });
            toast.success("Booking created successfully!");
            navigate('/my-bookings');
        } catch (error) {
            console.error(error);
            toast.error("Failed to create booking.");
        } finally {
            setIsSubmitting(false);
        }
    };

    if (!service) {
        return <div className="container mt-5 text-center">Loading service details...</div>;
    }

    return (
        <div className="container mt-5">
            <h1 className="mb-4 text-center">Book {service.name}</h1>
            <div className="row justify-content-center">
                <div className="col-md-6">
                    <div className="card">
                        <div className="card-body">
                            <h5 className="card-title">Service Details</h5>
                            <p className="card-text">{service.description}</p>
                            <p className="card-text"><strong>Price: ₹{service.price}</strong></p>
                            <form onSubmit={handleSubmit}>
                                <div className="form-group mb-3">
                                    <label htmlFor="bookingDateTime">Select Date and Time</label>
                                    <input
                                        type="datetime-local"
                                        className="form-control"
                                        id="bookingDateTime"
                                        value={bookingDateTime}
                                        onChange={(e) => setBookingDateTime(e.target.value)}
                                        required
                                    />
                                </div>
                                <button type="submit" className="btn btn-primary w-100" disabled={isSubmitting}>
                                    {isSubmitting ? 'Submitting...' : 'Confirm Booking'}
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BookingPage;