import React, { useState } from 'react';
import './MessageBubble.css';
const MAX_LENGTH = 200;

const MessageBubble = ({ sender, text }) => {
  const [expanded, setExpanded] = useState(false);

  const isLong = text.length > MAX_LENGTH;
  const displayText = expanded ? text : text.slice(0, MAX_LENGTH);
  const toggleExpand = () => setExpanded(prev => !prev);

  return (
    <div className={`message-bubble ${sender === 'user' ? 'user' : 'bot'}`}>
      <div
        className="bubble-content"
        dangerouslySetInnerHTML={{ __html: displayText }}
      />
      {isLong && (
        <div className="toggle-expand" onClick={toggleExpand}>
          {expanded ? '간단히 보기' : '더 보기'}
        </div>
      )}
    </div>
  );
  
};

export default MessageBubble;
