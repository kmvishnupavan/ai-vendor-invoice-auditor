import React from 'react';
import { CheckCircle2, AlertTriangle } from 'lucide-react';

export default function StatusBadge({ status, size = 'sm' }) {
  const isClear = status === 'CLEAR';

  return (
    <span className={`badge ${isClear ? 'clear' : 'discrepancy'} ${size === 'lg' ? 'lg' : ''}`}>
      {isClear ? (
        <>
          <CheckCircle2 size={size === 'lg' ? 18 : 13} strokeWidth={2.5} />
          CLEAR
        </>
      ) : (
        <>
          <AlertTriangle size={size === 'lg' ? 18 : 13} strokeWidth={2.5} />
          DISCREPANCY
        </>
      )}
    </span>
  );
}
