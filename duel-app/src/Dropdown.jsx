export default function Dropdown({prompt, options, onChange, initialValue}) {
    return (
        <select className="form-select" onChange={onChange} value={initialValue}>
            <option value="">{prompt}</option>
            {options.map((option, index) => (
                <option key={index} value={option.value}>
                    {option.displayName}
                </option>
            ))}
        </select>
    );
}