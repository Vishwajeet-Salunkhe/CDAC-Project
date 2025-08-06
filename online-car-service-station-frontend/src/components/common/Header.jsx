import { Link, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../../store/authSlice';
import { toast } from 'react-toastify';
import defaultProfileImage from '../../assets/default-profile.png';

const Header = () => {
    const { user, isAuthenticated } = useSelector((state) => state.auth);
    const { items: cartItems } = useSelector(state => state.cart);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    
    const isCustomer = isAuthenticated && user?.roles.includes('ROLE_CUSTOMER');
    const isAdmin = isAuthenticated && user?.roles.includes('ROLE_ADMIN');

    const handleLogout = () => {
        dispatch(logout());
        toast.info('Logged out successfully.');
        navigate('/login');
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
            <div className="container-fluid">
                <Link className="navbar-brand" to="/">
                    Car Service
                </Link>
                <div className="collapse navbar-collapse">
                    <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                        <li className="nav-item">
                            <Link className="nav-link" to="/">
                                Home
                            </Link>
                        </li>
                        {isCustomer && (
                            <li className="nav-item">
                                <Link className="nav-link" to="/services">
                                    Services
                                </Link>
                            </li>
                        )}
                        {isAdmin && (
                            <li className="nav-item">
                                <Link className="nav-link" to="/admin-dashboard">
                                    Admin Dashboard
                                </Link>
                            </li>
                        )}
                        {isCustomer && (
                            <li className="nav-item">
                                <Link className="nav-link" to="/my-bookings">
                                    My Bookings
                                </Link>
                            </li>
                        )}
                        {isAdmin && (
                            <li className="nav-item">
                                <Link className="nav-link" to="/all-bookings">
                                    All Bookings
                                </Link>
                            </li>
                        )}
                        {isAdmin && (
                            <li className="nav-item">
                                <Link className="nav-link" to="/stats">
                                    Stats
                                </Link>
                            </li>
                        )}
                         {isAuthenticated && (
                            <li className="nav-item">
                                <Link className="nav-link" to="/profile">
                                    My Profile
                                </Link>
                            </li>
                        )}
                    </ul>
                    <div className="d-flex align-items-center">
                         {isCustomer && (
                            <Link className="btn btn-outline-light me-2 position-relative" to="/booking-summary">
                                View Current Booking
                                {cartItems.length > 0 && (
                                    <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                                        {cartItems.length}
                                        <span className="visually-hidden">services in cart</span>
                                    </span>
                                )}
                            </Link>
                        )}
                        {isAuthenticated ? (
                            <>
                                {user.profileImageUrl ? (
                                    <img src={user.profileImageUrl} alt="Profile" className="rounded-circle me-2" style={{ width: '40px', height: '40px', objectFit: 'cover' }} />
                                ) : (
                                    <img src={defaultProfileImage} alt="Default Profile" className="rounded-circle me-2" style={{ width: '40px', height: '40px', objectFit: 'cover' }} />
                                )}
                                <span className="navbar-text text-white me-3">
                                    Welcome, {user.username}!
                                </span>
                                <button className="btn btn-outline-light" onClick={handleLogout}>
                                    Logout
                                </button>
                            </>
                        ) : (
                            <>
                                <Link className="btn btn-outline-light me-2" to="/login">
                                    Login
                                </Link>
                                <Link className="btn btn-outline-success" to="/register">
                                    Register
                                </Link>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default Header;