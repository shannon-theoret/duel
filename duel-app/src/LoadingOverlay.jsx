import { Spinner } from 'react-bootstrap';
import './LoadingOverlay.css';

export default function LoadingOverlay() {
    return (
        <div className="overlay loading-overlay">
            <Spinner animation="border" role="status"></Spinner>
            <p>Waiting for opponent...</p>
        </div>
    );
}