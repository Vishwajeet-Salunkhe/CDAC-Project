import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import api from '../services/api';
import { toast } from 'react-toastify';

const HomePage = () => {
    const [services, setServices] = useState([]);
    const { isAuthenticated, user } = useSelector(state => state.auth);
    const navigate = useNavigate();
    
    // Justification: Helper to check if the user is a customer or not authenticated.
    const isCustomerOrGuest = !isAuthenticated || (isAuthenticated && user?.roles.includes('ROLE_CUSTOMER'));

    useEffect(() => {
        const fetchServices = async () => {
            try {
                const response = await api.get('/services');
                setServices(response.data.slice(0, 3)); // Show the first 3 services as featured
            } catch (error) {
                console.error(error);
                toast.error("Failed to fetch services.");
            }
        };
        fetchServices();
    }, []);

    return (
        <div className="container mt-5">
            <header className="hero-section text-center bg-light p-5 rounded">
                <h1 className="display-4">Your Trusted Car Service Partner</h1>
                <p className="lead">Experience hassle-free car maintenance and repairs with our expert technicians.</p>
                {/* Justification: CRITICAL FIX. The button is only shown to customers and guests. */}
                {isCustomerOrGuest && (
                    <button 
                        className="btn btn-primary btn-lg mt-3"
                        onClick={() => navigate('/services')}
                    >
                        Explore Our Services ( All )
                    </button>
                )}
            </header>

            <section className="featured-services mt-5">
                <h2 className="text-center mb-4">Top 3 Featured Services</h2>
                <div className="row justify-content-center">
                    {services.length === 0 ? (
                        <p className="text-center">Loading featured services...</p>
                    ) : (
                        services.map(service => (
                            <div className="col-md-4 mb-4" key={service.id}>
                                <div className="card h-100">
                                    <img src={service.imageUrl} className="card-img-top" alt={service.name} style={{ height: '200px', objectFit: 'cover' }} />
                                    <div className="card-body">
                                        <h5 className="card-title">{service.name}</h5>
                                        <p className="card-text">{service.description.substring(0, 100)}...</p>
                                        <p className="card-text"><strong>Price: ₹{service.price}</strong></p>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </section>
            
            <section className="about-us mt-5 p-5 bg-light rounded">
                <h2 className="text-center mb-4">About Us</h2>
                <p className="text-center">
                    We are dedicated to providing the highest quality car service and maintenance. Our team of certified professionals ensures your vehicle is in top condition, giving you peace of mind on the road.
                </p>
            </section>
        </div>
    );
};

export default HomePage;