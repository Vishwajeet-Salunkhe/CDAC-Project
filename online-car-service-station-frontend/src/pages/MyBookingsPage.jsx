import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { toast } from 'react-toastify';
import { format } from 'date-fns';
import FeedbackModal from '../components/common/FeedbackModal';

const loadScript = (src) => {
    return new Promise((resolve) => {
        const script = document.createElement('script');
        script.src = src;
        script.onload = () => resolve(true);
        script.onerror = () => resolve(false);
        document.body.appendChild(script);
    });
};

const MyBookingsPage = () => {
    const [bookings, setBookings] = useState([]);
    const [showFeedbackModal, setShowFeedbackModal] = useState(false);
    const [selectedBooking, setSelectedBooking] = useState(null);
    const { isAuthenticated, user } = useSelector(state => state.auth);
    const navigate = useNavigate();

    useEffect(() => {
        if (!isAuthenticated) {
            navigate('/login');
            return;
        }
        fetchBookings();
    }, [isAuthenticated, navigate]);

    const fetchBookings = async () => {
        try {
            const response = await api.get('/bookings/my-bookings');
            setBookings(response.data);
        } catch (error) {
            console.error(error);
            toast.error("Failed to fetch your bookings.");
        }
    };

    const handlePayment = async (booking) => {
        try {
            const response = await api.post('/payments/create-order', {
                bookingId: booking.bookingId,
                amount: booking.totalAmount
            });
            const { orderId, keyId, amount } = response.data;

            const res = await loadScript("https://checkout.razorpay.com/v1/checkout.js");
            if (!res) {
                toast.error("Razorpay SDK failed to load. Are you connected to the internet?");
                return;
            }

            const options = {
                key: keyId,
                amount: amount * 100,
                currency: "INR",
                name: "Car Service Station",
                description: "Booking Payment",
                order_id: orderId,
                handler: async function (response) {
                    try {
                        await api.post('/payments/verify-payment', {
                            razorpayOrderId: response.razorpay_order_id,
                            razorpayPaymentId: response.razorpay_payment_id,
                            razorpaySignature: response.razorpay_signature,
                            bookingId: booking.bookingId
                        });
                        toast.success("Payment confirmed successfully!");
                        fetchBookings();
                    } catch (error) {
                        console.error(error);
                    }
                },
                prefill: {
                    name: user.username,
                    email: user.email,
                },
                theme: {
                    color: "#3399cc"
                }
            };
            const paymentObject = new window.Razorpay(options);
            paymentObject.open();

        } catch (error) {
            toast.error(error.response?.data || "Failed to initiate payment.");
            console.error(error);
        }
    };

    const handleLeaveFeedback = (booking) => {
        setSelectedBooking(booking);
        setShowFeedbackModal(true);
    };

    const handleCloseModal = () => {
        setShowFeedbackModal(false);
        setSelectedBooking(null);
    };

    const currentBookings = bookings.filter(b => b.paymentStatus === 'PENDING');
    const pastBookings = bookings.filter(b => b.paymentStatus === 'PAID');

    if (!user) {
        return <div className="container mt-5 text-center">Loading...</div>;
    }

    return (
        <div className="container mt-5">
            <h1 className="mb-4 text-center">My Bookings</h1>
            
            <h3 className="mb-3">Current Bookings ({currentBookings.length})</h3>
            <div className="table-responsive mb-5">
                <table className="table table-striped">
                    <thead>
                        <tr>
                            <th>Booking ID</th>
                            <th>Services</th>
                            <th>Date & Time</th>
                            <th>Status</th>
                            <th>Total Amount</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {currentBookings.length === 0 ? (
                            <tr><td colSpan="6" className="text-center">No current bookings.</td></tr>
                        ) : (
                            currentBookings.map(booking => (
                                <tr key={booking.bookingId}>
                                    <td>{booking.bookingId}</td>
                                    <td>
                                        {booking.bookedServices.map(service => (
                                            <div key={service.id}>{service.name} (₹{service.price})</div>
                                        ))}
                                    </td>
                                    <td>{format(new Date(booking.bookingDateTime), 'PPP p')}</td>
                                    <td>{booking.status}</td>
                                    <td>₹{booking.totalAmount}</td>
                                    <td>
                                        <button 
                                            className="btn btn-success btn-sm" 
                                            onClick={() => handlePayment(booking)}
                                        >
                                            Pay Now
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            <h3 className="mb-3">Past Bookings ({pastBookings.length})</h3>
            <div className="table-responsive">
                <table className="table table-striped">
                    <thead>
                        <tr>
                            <th>Booking ID</th>
                            <th>Services</th>
                            <th>Date & Time</th>
                            <th>Status</th>
                            <th>Payment Status</th>
                            <th>Total Amount</th>
                            <th>Feedback</th>
                        </tr>
                    </thead>
                    <tbody>
                        {pastBookings.length === 0 ? (
                            <tr><td colSpan="7" className="text-center">No past bookings.</td></tr>
                        ) : (
                            pastBookings.map(booking => (
                                <tr key={booking.bookingId}>
                                    <td>{booking.bookingId}</td>
                                    <td>
                                        {booking.bookedServices.map(service => (
                                            <div key={service.id}>{service.name} (₹{service.price})</div>
                                        ))}
                                    </td>
                                    <td>{format(new Date(booking.bookingDateTime), 'PPP p')}</td>
                                    <td>{booking.status}</td>
                                    <td>{booking.paymentStatus}</td>
                                    <td>₹{booking.totalAmount}</td>
                                    <td>
                                        {/* Justification: Conditionally render the feedback button. It's only
                                            visible for completed bookings that have a paid payment. */}
                                        {booking.status === 'COMPLETED' && !booking.rating && (
                                            <button className="btn btn-primary btn-sm" onClick={() => handleLeaveFeedback(booking)}>
                                                Leave Feedback
                                            </button>
                                        )}
                                        {/* Justification: Display the rating if feedback has been given, with a tooltip. */}
                                        {booking.rating && (
                                            <div title={booking.comment}>
                                                {'⭐'.repeat(booking.rating)}
                                            </div>
                                        )}
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
            {showFeedbackModal && (
                <FeedbackModal 
                    booking={selectedBooking} 
                    onClose={handleCloseModal}
                    onFeedbackSubmitted={fetchBookings}
                />
            )}
        </div>
    );
};

export default MyBookingsPage;