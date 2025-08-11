import { useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { toast } from 'react-toastify';
import { clearCart } from '../store/cartSlice';

const BookingSummaryPage = () => {
    const { items: cartItems } = useSelector(state => state.cart);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const [bookingDateTime, setBookingDateTime] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    const totalAmount = cartItems.reduce((acc, item) => acc + item.price, 0);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (cartItems.length === 0) {
            toast.error("Your booking cart is empty.");
            return;
        }

        setIsSubmitting(true);
        try {
            const carServiceIds = cartItems.map(item => item.id);
            await api.post('/bookings', {
                carServiceIds,
                bookingDateTime
            });
            toast.success("Booking created successfully!");
            dispatch(clearCart());
            navigate('/my-bookings');
        } catch (error) {
            console.error(error);
            toast.error("Failed to create booking.");
        } finally {
            setIsSubmitting(false);
        }
    };

    if (cartItems.length === 0) {
        // Justification: The correct message is now shown for an empty cart.
        return (
            <div className="container mt-5 text-center">
                <h2>Your booking cart is empty.</h2>
                <p>Please go to the <a href="/services">services page</a> to add services.</p>
            </div>
        );
    }

    return (
        <div className="container mt-5">
            <h1 className="mb-4 text-center">Booking Summary</h1>
            <div className="row justify-content-center">
                <div className="col-md-8">
                    <div className="card">
                        <div className="card-body">
                            <h5 className="card-title">Selected Services</h5>
                            <ul className="list-group list-group-flush mb-4">
                                {cartItems.map(item => (
                                    <li key={item.id} className="list-group-item d-flex justify-content-between align-items-center">
                                        {item.name}
                                        <span className="badge bg-primary rounded-pill">₹{item.price}</span>
                                    </li>
                                ))}
                            </ul>
                            <div className="text-end mb-4">
                                <h4>Total Amount: ₹{totalAmount.toFixed(2)}</h4>
                            </div>
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
                                <button type="submit" className="btn btn-success w-100" disabled={isSubmitting}>
                                    {isSubmitting ? 'Confirming...' : 'Confirm Booking'}
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BookingSummaryPage;