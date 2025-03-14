import './Slider.css';

export default function Slider({ isChecked, label, toggleFunction }) {
  return (
    <div className="slider-container">
      <span>{label}</span>
      <label className="switch">
        <input type="checkbox" checked={isChecked} onChange={(e) => toggleFunction(e.target.checked)} />
        <span className="slider round"></span>
      </label>
    </div>
  );
};
