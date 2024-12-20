import './Button.css';

export default function Button({onClick, text}) {

    return <button className="button-6" role="button" onClick={onClick}>{text}</button>;
}