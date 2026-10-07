import React from 'react';
import { FileText, CheckCircle2, AlertTriangle, Percent } from 'lucide-react';

export default function SummaryCards({ summary }) {
  const { totalAudits = 0, clearAudits = 0, discrepancies = 0, discrepancyRate = 0 } = summary || {};

  return (
    <div className="metrics-grid">
      <div className="metric-card">
        <div>
          <div className="metric-label">Total Audits</div>
          <div className="metric-value">{totalAudits}</div>
          <div className="metric-subtext">Processed vendor invoices</div>
        </div>
        <div className="metric-icon-box blue">
          <FileText size={22} />
        </div>
      </div>

      <div className="metric-card">
        <div>
          <div className="metric-label">Clear Audits</div>
          <div className="metric-value" style={{ color: 'var(--success-text)' }}>
            {clearAudits}
          </div>
          <div className="metric-subtext">Matched PO terms 100%</div>
        </div>
        <div className="metric-icon-box green">
          <CheckCircle2 size={22} />
        </div>
      </div>

      <div className="metric-card">
        <div>
          <div className="metric-label">Discrepancies</div>
          <div className="metric-value" style={{ color: 'var(--danger-text)' }}>
            {discrepancies}
          </div>
          <div className="metric-subtext">Flagged for price or quantity diff</div>
        </div>
        <div className="metric-icon-box red">
          <AlertTriangle size={22} />
        </div>
      </div>

      <div className="metric-card">
        <div>
          <div className="metric-label">Discrepancy Rate</div>
          <div className="metric-value" style={{ color: discrepancies > 0 ? 'var(--warning-text)' : 'inherit' }}>
            {discrepancyRate}%
          </div>
          <div className="metric-subtext">Non-compliant invoice ratio</div>
        </div>
        <div className="metric-icon-box amber">
          <Percent size={22} />
        </div>
      </div>
    </div>
  );
}
