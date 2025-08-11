import { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { toast } from 'react-toastify';

const StatsPage = () => {
    const [stats, setStats] = useState(null);
    const { isAuthenticated, user } = useSelector(state => state.auth);
    const navigate = useNavigate();
    
    useEffect(() => {
        if (!isAuthenticated || !user?.roles.includes('ROLE_ADMIN')) {
            navigate('/');
            toast.error('Access Denied');
        } else {
            fetchStats();
        }
    }, [isAuthenticated, user, navigate]);

    // Justification: Asynchronous function to fetch the total revenue and stats from the backend.
    const fetchStats = async () => {
        try {
            const response = await api.get('/bookings/stats');
            setStats(response.data);
        } catch (error) {
            console.error(error);
            toast.error("Failed to fetch stats.");
        }
    };

    if (!stats) {
        return <div className="container mt-5 text-center">Loading stats...</div>;
    }

    return (
        <div className="container mt-5">
            <h1 className="mb-4 text-center">Revenue & Stats</h1>
            <div className="row justify-content-center">
                <div className="col-md-6">
                    <div className="card text-center text-white bg-primary mb-3">
                        <div className="card-header">Total Revenue (Completed & Paid)</div>
                        <div className="card-body">
                            <h2 className="card-title">₹{stats.totalRevenue.toFixed(2)}</h2>
                        </div>
                    </div>
                </div>
                <div className="col-md-6">
                    <div className="card text-center text-white bg-success mb-3">
                        <div className="card-header">Total Completed & Paid Bookings</div>
                        <div className="card-body">
                            <h2 className="card-title">{stats.totalCompletedBookings}</h2>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default StatsPage;

