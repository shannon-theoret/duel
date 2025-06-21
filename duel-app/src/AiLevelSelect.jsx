import React, { useState } from 'react';

export default function AiLevelSelect({ aiLevel, setAiLevel }) {
  return (
    <div className="ai-level-select">
      <p>Select AI Level:</p>
      <label>
        <input
          type="radio"
          value="1"
          checked={aiLevel == "1"}
          onChange={(e) => setAiLevel(e.target.value)}
        />
        Beginner
      </label>
      <label>
        <input
          type="radio"
          value="2"
          checked={aiLevel == "2"}
          onChange={(e) => setAiLevel(e.target.value)}
        />
        Intermediate
      </label>
      <label>
        <input
          type="radio"
          value="3"
          checked={aiLevel == "3"}
          onChange={(e) => setAiLevel(e.target.value)}
        />
        Expert
      </label>
    </div>
  );
}