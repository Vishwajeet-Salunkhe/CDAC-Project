import { useState } from 'react';
import { toast } from 'react-toastify';
import api from '../../services/api';

const FeedbackModal = ({ booking, onClose, onFeedbackSubmitted }) => {
    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    // Justification: This function submits the feedback to the backend.
    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            const feedbackData = {
                bookingId: booking.bookingId,
                rating,
                comment
            };
            await api.post('/bookings/feedback', feedbackData);
            toast.success("Feedback submitted successfully!");
            onFeedbackSubmitted(); // Call the parent to refresh data
            onClose(); // Close the modal
        } catch (error) {
            console.error(error);
            toast.error("Failed to submit feedback.");
        } finally {
            setIsSubmitting(false);
        }
    };

    // Justification: A simple UI for a star rating.
    const renderStarRating = () => {
        const stars = [];
        for (let i = 1; i <= 5; i++) {
            stars.push(
                <span
                    key={i}
                    onClick={() => setRating(i)}
                    style={{ cursor: 'pointer', color: i <= rating ? 'gold' : 'gray', fontSize: '24px' }}
                >
                    &#9733;
                </span>
            );
        }
        return <div>{stars}</div>;
    };

    return (
        <div className="modal fade show" style={{ display: 'block' }} tabIndex="-1">
            <div className="modal-dialog">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Leave Feedback for Booking #{booking.bookingId}</h5>
                        <button type="button" className="btn-close" onClick={onClose}></button>
                    </div>
                    <div className="modal-body">
                        <form onSubmit={handleSubmit}>
                            <div className="form-group mb-3">
                                <label>Rating</label>
                                {renderStarRating()}
                                {rating > 0 && <small className="d-block">You rated: {rating} stars</small>}
                            </div>
                            <div className="form-group mb-3">
                                <label htmlFor="comment">Comment</label>
                                <textarea className="form-control" id="comment" value={comment} onChange={(e) => setComment(e.target.value)} required></textarea>
                            </div>
                            <button type="submit" className="btn btn-primary w-100" disabled={isSubmitting}>
                                {isSubmitting ? 'Submitting...' : 'Submit Feedback'}
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default FeedbackModal;