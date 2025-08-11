import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { toast } from 'react-toastify';
import { format } from 'date-fns';

const AllBookingsPage = () => {
    const [bookings, setBookings] = useState([]);
    const { isAuthenticated, user } = useSelector(state => state.auth);
    const navigate = useNavigate();

    useEffect(() => {
        if (!isAuthenticated || !user?.roles.includes('ROLE_ADMIN')) {
            navigate('/');
            toast.error('Access Denied');
        } else {
            fetchAllBookings();
        }
    }, [isAuthenticated, user, navigate]);

    const fetchAllBookings = async () => {
        try {
            const response = await api.get('/bookings');
            setBookings(response.data);
        } catch (error) {
            console.error(error);
            toast.error('Failed to fetch all bookings.');
        }
    };

    const handleUpdateStatus = async (bookingId, newStatus) => {
        try {
            const requestBody = { status: newStatus };
            await api.put(`/bookings/${bookingId}/status`, requestBody);
            toast.success('Booking status updated successfully!');
            fetchAllBookings();
        } catch (error) {
            console.error(error);
            toast.error('Failed to update booking status.');
        }
    };

    const handleDelete = async (bookingId) => {
        if (window.confirm("Are you sure you want to delete this completed booking?")) {
            try {
                await api.delete(`/bookings/${bookingId}`);
                toast.success('Booking deleted successfully!');
                fetchAllBookings();
            } catch (error) {
                console.error(error);
                toast.error("Failed to delete booking.");
            }
        }
    };

    const renderBookingTable = (bookingList, showActions = true) => (
        <div className="table-responsive">
            <table className="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Customer</th>
                        <th>Services</th>
                        <th>Date & Time</th>
                        <th>Status</th>
                        <th>Payment</th>
                        <th>Total</th>
                        <th>Feedback</th>
                        {showActions && <th>Actions</th>}
                    </tr>
                </thead>
                <tbody>
                    {bookingList.map(booking => (
                        <tr key={booking.bookingId}>
                            <td>{booking.bookingId}</td>
                            <td>{booking.customerUsername}</td>
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
                                {booking.rating && (
                                    <div title={booking.comment}>
                                        {'⭐'.repeat(booking.rating)}
                                    </div>
                                )}
                            </td>
                            {showActions && (
                                <td>
                                    {booking.status === 'PENDING' && (
                                        <button 
                                            className="btn btn-success btn-sm" 
                                            onClick={() => handleUpdateStatus(booking.bookingId, 'CONFIRMED')}
                                        >
                                            Confirm
                                        </button>
                                    )}
                                    {booking.status === 'CONFIRMED' && (
                                        <button 
                                            className="btn btn-warning btn-sm" 
                                            onClick={() => handleUpdateStatus(booking.bookingId, 'IN_PROGRESS')}
                                        >
                                            Start Service
                                        </button>
                                    )}
                                    {booking.status === 'IN_PROGRESS' && (
                                        <button 
                                            className="btn btn-info btn-sm" 
                                            onClick={() => handleUpdateStatus(booking.bookingId, 'COMPLETED')}
                                        >
                                            Mark as Complete
                                        </button>
                                    )}
                                    {booking.status === 'COMPLETED' && (
                                        <button
                                            className="btn btn-danger btn-sm"
                                            onClick={() => handleDelete(booking.bookingId)}
                                        >
                                            Delete
                                        </button>
                                    )}
                                </td>
                            )}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );

    const pendingBookings = bookings.filter(b => b.status === 'PENDING');
    const confirmedBookings = bookings.filter(b => b.status === 'CONFIRMED');
    const inProgressBookings = bookings.filter(b => b.status === 'IN_PROGRESS');
    const completedBookings = bookings.filter(b => b.status === 'COMPLETED');
    const cancelledBookings = bookings.filter(b => b.status === 'CANCELLED');
    
    if (!user || !user?.roles.includes('ROLE_ADMIN')) {
        return <div className="container mt-5 text-center"><p>Access Denied</p></div>;
    }

    if (bookings.length === 0) {
        return <div className="container mt-5 text-center"><p>No bookings found.</p></div>;
    }

    return (
        <div className="container mt-5">
            <h1 className="mb-4 text-center">All Bookings</h1>
            
            {pendingBookings.length > 0 && (
                <div className="card mb-4 border-info">
                    <div className="card-header bg-info text-white">
                        <h4 className="my-0">Pending Bookings ({pendingBookings.length})</h4>
                    </div>
                    <div className="card-body">
                        {renderBookingTable(pendingBookings)}
                    </div>
                </div>
            )}
            
            {confirmedBookings.length > 0 && (
                <div className="card mb-4 border-success">
                    <div className="card-header bg-success text-white">
                        <h4 className="my-0">Confirmed Bookings ({confirmedBookings.length})</h4>
                    </div>
                    <div className="card-body">
                        {renderBookingTable(confirmedBookings)}
                    </div>
                </div>
            )}
            
            {inProgressBookings.length > 0 && (
                <div className="card mb-4 border-warning">
                    <div className="card-header bg-warning text-dark">
                        <h4 className="my-0">In Progress Bookings ({inProgressBookings.length})</h4>
                    </div>
                    <div className="card-body">
                        {renderBookingTable(inProgressBookings)}
                    </div>
                </div>
            )}

            {completedBookings.length > 0 && (
                <div className="card mb-4 border-secondary">
                    <div className="card-header bg-secondary text-white">
                        <h4 className="my-0">Completed Bookings ({completedBookings.length})</h4>
                    </div>
                    <div className="card-body">
                        {renderBookingTable(completedBookings, true)}
                    </div>
                </div>
            )}
            
            {cancelledBookings.length > 0 && (
                <div className="card mb-4 border-danger">
                    <div className="card-header bg-danger text-white">
                        <h4 className="my-0">Cancelled Bookings ({cancelledBookings.length})</h4>
                    </div>
                    <div className="card-body">
                        {renderBookingTable(cancelledBookings, false)}
                    </div>
                </div>
            )}
        </div>
    );
};

export default AllBookingsPage;