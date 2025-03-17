import './ErrorBox.css';

export default function ErrorBox({errorMessage}) {
    return (<div className="error">{errorMessage}</div>);
}