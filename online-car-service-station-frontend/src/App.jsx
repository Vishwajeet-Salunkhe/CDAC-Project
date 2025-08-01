import { Routes, Route } from 'react-router-dom';
import Header from './components/common/Header';
import Footer from './components/common/Footer';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import AdminDashboard from './pages/AdminDashboard';
import ServicesPage from './pages/ServicesPage';
import MyBookingsPage from './pages/MyBookingsPage';
import AllBookingsPage from './pages/AllBookingsPage';
import BookingSummaryPage from './pages/BookingSummaryPage';
import StatsPage from './pages/StatsPage';
import ProfilePage from './pages/ProfilePage'; // <-- Import the new page
import { useSelector } from 'react-redux';

function App() {
    const { isAuthenticated, user } = useSelector(state => state.auth);

    return (
        <div className="d-flex flex-column min-vh-100">
            <Header />
            <main className="flex-grow-1">
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/services" element={<ServicesPage />} />
                    
                    {isAuthenticated && user?.roles.includes("ROLE_ADMIN") && (
                        <>
                            <Route path="/admin-dashboard" element={<AdminDashboard />} />
                            <Route path="/all-bookings" element={<AllBookingsPage />} />
                            <Route path="/stats" element={<StatsPage />} />
                        </>
                    )}
                    
                    {isAuthenticated && (
                        <>
                            <Route path="/booking-summary" element={<BookingSummaryPage />} />
                            <Route path="/my-bookings" element={<MyBookingsPage />} />
                            {/* Justification: Add the new route for the Profile page. */}
                            <Route path="/profile" element={<ProfilePage />} />
                        </>
                    )}
                    
                    <Route path="*" element={<div className="container mt-5"><h2>404 Not Found</h2></div>} />
                </Routes>
            </main>
            <Footer />
        </div>
    );
}

export default App;