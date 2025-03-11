import React, { useState, useEffect } from 'react';

export default function Collapsible({ children, label, defaultOpen}) {
  const [isOpen, setIsOpen] = useState(defaultOpen);

  const toggleCollapse = () => {
    setIsOpen(!isOpen);
  };

  useEffect(() => {
    if (defaultOpen) {
      setIsOpen(true);
    } else {
      const timer = setTimeout(() => {
        setIsOpen(false);
      }, 1500);

      return () => clearTimeout(timer);
    }
  }, [defaultOpen]);

  return (
    <div className="collapsible-container">
      <div className="collapsible-header" onClick={toggleCollapse}>
        <span>{isOpen ? '▼' : '▶'}</span>
        <span>{label}</span>
      </div>
      <div className={`collapsible-content ${isOpen ? 'open' : ''}`}>
        {children}
      </div>
    </div>
  );
};