import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { addToCart, removeFromCart } from '../store/cartSlice';
import api from '../services/api';
import { toast } from 'react-toastify';

const ServicesPage = () => {
    const [services, setServices] = useState([]);
    const { isAuthenticated, user } = useSelector(state => state.auth);
    const { items: cartItems } = useSelector(state => state.cart);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    // Justification: Helper to check if the user is a customer.
    const isCustomer = isAuthenticated && user?.roles.includes('ROLE_CUSTOMER');

    useEffect(() => {
        const fetchServices = async () => {
            try {
                const response = await api.get('/services');
                setServices(response.data);
            } catch (error) {
                console.error(error);
                toast.error("Failed to fetch services.");
            }
        };
        fetchServices();
    }, []);

    const isServiceInCart = (serviceId) => {
        return cartItems.some(item => item.id === serviceId);
    };

    const handleCartAction = (service) => {
        if (!isAuthenticated) {
            toast.warn("Please log in to add services to a booking.");
            navigate('/login');
            return;
        }

        if (isServiceInCart(service.id)) {
            dispatch(removeFromCart(service));
            toast.info(`${service.name} removed from booking cart.`);
        } else {
            dispatch(addToCart(service));
            toast.success(`${service.name} added to booking cart.`);
        }
    };

    return (
        <div className="container mt-5">
            <h1 className="mb-4 text-center">Available Car Services</h1>
            <div className="row">
                {services.length === 0 ? (
                    <p className="text-center">No services available. Please check back later!</p>
                ) : (
                    services.map(service => (
                        <div className="col-md-4 mb-4" key={service.id}>
                            <div className="card h-100">
                                <img src={service.imageUrl} className="card-img-top" alt={service.name} style={{ height: '200px', objectFit: 'cover' }} />
                                <div className="card-body d-flex flex-column">
                                    <h5 className="card-title">{service.name}</h5>
                                    <p className="card-text flex-grow-1">{service.description}</p>
                                    <p className="card-text"><strong>Price: ₹{service.price}</strong></p>
                                    {/* Justification: CRITICAL FIX. The button is only shown if the user is a customer. */}
                                    {isCustomer && (
                                        <button
                                            onClick={() => handleCartAction(service)}
                                            className={`btn mt-auto ${isServiceInCart(service.id) ? 'btn-danger' : 'btn-primary'}`}
                                        >
                                            {isServiceInCart(service.id) ? 'Remove from Booking' : 'Add to Booking'}
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default ServicesPage;