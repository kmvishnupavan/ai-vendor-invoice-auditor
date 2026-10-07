import React from 'react';
import { AlertTriangle, X } from 'lucide-react';

export default function ErrorAlert({ message, onDismiss }) {
  if (!message) return null;

  return (
    <div className="error-alert-banner" role="alert">
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.625rem' }}>
        <AlertTriangle size={18} style={{ flexShrink: 0 }} />
        <span>{message}</span>
      </div>
      {onDismiss && (
        <button
          className="error-close-btn"
          onClick={onDismiss}
          aria-label="Dismiss error notification"
        >
          <X size={16} />
        </button>
      )}
    </div>
  );
}
