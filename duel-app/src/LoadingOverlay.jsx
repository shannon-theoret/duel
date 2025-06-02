import { Spinner } from 'react-bootstrap';
import './LoadingOverlay.css';

export default function LoadingOverlay() {
    return (
        <div className="spinner-overlay">
            <Spinner animation="border" role="status"></Spinner>
            <p>Waiting for AI player...</p>
        </div>
    );
}