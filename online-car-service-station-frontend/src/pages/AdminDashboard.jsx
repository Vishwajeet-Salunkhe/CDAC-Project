import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import ServiceManagement from './ServiceManagement';

// Justification: This component serves as the main dashboard for the admin.
// It checks the user's role and redirects if they are not an admin.
const AdminDashboard = () => {
    const { user } = useSelector(state => state.auth);
    const navigate = useNavigate();

    useEffect(() => {
        // Justification: This check ensures that only users with ROLE_ADMIN
        // can stay on this page. If the user is not an admin, they are redirected.
        // This provides an extra layer of security and user experience.
        if (!user || !user.roles.includes("ROLE_ADMIN")) {
            navigate("/");
        }
    }, [user, navigate]);

    // Justification: Conditionally render a message if the user is not authenticated.
    if (!user) {
        return <div className="container mt-5">Loading...</div>;
    }

    return (
        <div className="container mt-5">
            <h1 className="mb-4">Admin Dashboard</h1>
            <ServiceManagement />
        </div>
    );
};

export default AdminDashboard;