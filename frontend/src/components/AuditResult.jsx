import React from 'react';
import { CheckCircle2, AlertTriangle, FileSpreadsheet, ShieldAlert, Clock, Database } from 'lucide-react';
import StatusBadge from './StatusBadge';
import ComparisonPanel from './ComparisonPanel';
import { formatCurrency } from '../utils/formatCurrency';
import { formatDate } from '../utils/formatDate';

export default function AuditResult({ audit }) {
  if (!audit) return null;

  const isClear = audit.status === 'CLEAR';

  return (
    <div className="card-panel" style={{ borderTop: `4px solid ${isClear ? 'var(--success)' : 'var(--danger)'}` }}>
      <div className="card-header-bar">
        <div className="card-header-title">
          {isClear ? (
            <CheckCircle2 size={20} color="var(--success)" />
          ) : (
            <ShieldAlert size={20} color="var(--danger)" />
          )}
          <span>Audit Assessment Result</span>
        </div>
        <div className="card-header-action">
          <span style={{ fontSize: '0.75rem', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
            Log ID: {audit.logId}
          </span>
          <StatusBadge status={audit.status} size="lg" />
        </div>
      </div>

      <div className="card-body">
        {/* Status Banner */}
        <div className={`result-banner ${isClear ? 'clear' : 'discrepancy'}`}>
          <div className="banner-content">
            <div className="banner-icon">
              {isClear ? <CheckCircle2 size={28} /> : <AlertTriangle size={28} />}
            </div>
            <div>
              <div className="banner-title">
                {isClear ? 'Audit Verification Passed (CLEAR)' : 'Audit Discrepancy Detected'}
              </div>
              <div className="banner-subtitle">
                {isClear
                  ? 'All delivered line items match the purchase order quantity and pricing terms.'
                  : 'Vendor billing deviates from agreed purchase order contracts.'}
              </div>
            </div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <span
              style={{
                fontSize: '0.75rem',
                display: 'inline-flex',
                alignItems: 'center',
                gap: '0.3rem',
                color: 'var(--text-muted)',
              }}
            >
              <Clock size={12} />
              {formatDate(audit.auditedAt)}
            </span>
          </div>
        </div>

        {/* Discrepancy Reason Callout */}
        <div className={`reason-box ${isClear ? 'clear' : ''}`}>
          <div className="reason-label">
            {isClear ? 'Audit Conclusion' : 'Audit Findings & Discrepancy Rationale'}
          </div>
          <div className="reason-text">
            {audit.discrepancyReason}
          </div>
        </div>

        {/* Structured Comparison Panel */}
        <div style={{ marginBottom: '0.75rem' }}>
          <div style={{ fontSize: '0.8125rem', fontWeight: 700, marginBottom: '0.5rem', color: 'var(--text-main)' }}>
            Variance Matrix
          </div>
          <ComparisonPanel
            invoice={audit.invoice}
            purchaseOrder={audit.purchaseOrder}
            comparison={audit.comparison}
          />
        </div>

        {/* Side-by-Side Detailed Breakdown */}
        <div className="details-side-by-side">
          {/* AI Extracted Invoice Card */}
          <div className="detail-block">
            <div className="detail-block-title">
              <FileSpreadsheet size={14} color="var(--primary)" />
              <span>AI Extracted Invoice Data</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">Purchase Order ID</span>
              <span className="detail-row-val">{audit.invoice?.poId}</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">Item Description</span>
              <span className="detail-row-val">{audit.invoice?.itemName}</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">Quantity Delivered</span>
              <span className="detail-row-val">{audit.invoice?.quantityDelivered} units</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">Unit Price Charged</span>
              <span className="detail-row-val">{formatCurrency(audit.invoice?.unitPriceCharged)}</span>
            </div>
          </div>

          {/* Official Database PO Card */}
          <div className="detail-block">
            <div className="detail-block-title">
              <Database size={14} color="var(--primary)" />
              <span>Official Purchase Order (DB)</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">PO Identifier</span>
              <span className="detail-row-val">{audit.purchaseOrder?.poId}</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">Item Contracted</span>
              <span className="detail-row-val">{audit.purchaseOrder?.itemName}</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">Expected Quantity</span>
              <span className="detail-row-val">{audit.purchaseOrder?.expectedQuantity} units</span>
            </div>
            <div className="detail-row">
              <span className="detail-row-label">Agreed Unit Price</span>
              <span className="detail-row-val">{formatCurrency(audit.purchaseOrder?.agreedUnitPrice)}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
