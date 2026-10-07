import React from 'react';
import { X, CheckCircle2, ShieldAlert, FileSpreadsheet, Clock } from 'lucide-react';
import StatusBadge from './StatusBadge';
import { formatDate } from '../utils/formatDate';
import { formatCurrency } from '../utils/formatCurrency';

export default function AuditDetailModal({ audit, onClose }) {
  if (!audit) return null;

  const isClear = audit.status === 'CLEAR';

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.625rem' }}>
            {isClear ? (
              <CheckCircle2 size={20} color="var(--success)" />
            ) : (
              <ShieldAlert size={20} color="var(--danger)" />
            )}
            <div>
              <div style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-main)' }}>
                Audit Record Details
              </div>
              <div style={{ fontSize: '0.75rem', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                Log ID: {audit.logId}
              </div>
            </div>
          </div>
          <button className="modal-close-btn" onClick={onClose} aria-label="Close modal">
            <X size={18} />
          </button>
        </div>

        <div style={{ padding: '1.25rem' }}>
          {/* Status and Timestamp Header */}
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              marginBottom: '1rem',
              padding: '0.75rem 1rem',
              background: isClear ? 'var(--success-light)' : 'var(--danger-light)',
              borderRadius: 'var(--radius-md)',
              border: `1px solid ${isClear ? 'var(--success-border)' : 'var(--danger-border)'}`,
            }}
          >
            <div>
              <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-muted)' }}>
                DETERMINATION
              </div>
              <StatusBadge status={audit.status} size="lg" />
            </div>
            <div style={{ textAlign: 'right' }}>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                <Clock size={12} />
                Audited Timestamp
              </div>
              <div style={{ fontSize: '0.8125rem', fontWeight: 600 }}>
                {formatDate(audit.auditedAt)}
              </div>
            </div>
          </div>

          {/* Discrepancy Reason Callout */}
          <div className={`reason-box ${isClear ? 'clear' : ''}`}>
            <div className="reason-label">Findings & System Rationale</div>
            <div className="reason-text">{audit.discrepancyReason}</div>
          </div>

          {/* Audit Record Summary */}
          <div className="detail-block">
            <div className="detail-block-title">
              <FileSpreadsheet size={14} color="var(--primary)" />
              <span>Invoice Specifications Audited</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">Purchase Order ID</span>
              <span className="detail-row-val">{audit.poId}</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">Item Supplied</span>
              <span className="detail-row-val">{audit.invoiceItemName || '—'}</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">Quantity Delivered</span>
              <span className="detail-row-val">{audit.quantityDelivered} units</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">Unit Price Charged</span>
              <span className="detail-row-val">{formatCurrency(audit.unitPriceCharged)}</span>
            </div>
          </div>

          <div style={{ marginTop: '1.25rem', textAlign: 'right' }}>
            <button className="btn-secondary" onClick={onClose}>
              Close Inspector
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
