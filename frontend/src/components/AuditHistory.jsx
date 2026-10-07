import React, { useState, useMemo } from 'react';
import { History, Search, Filter, RefreshCw, Eye, AlertCircle, FileQuestion } from 'lucide-react';
import StatusBadge from './StatusBadge';
import { formatDate, formatTimeAgo } from '../utils/formatDate';
import { formatCurrency } from '../utils/formatCurrency';

export default function AuditHistory({ history, onSelectAudit, onRefresh, isLoading }) {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  const filteredHistory = useMemo(() => {
    if (!history) return [];
    return history.filter((item) => {
      const matchesSearch =
        item.poId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.logId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.discrepancyReason?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.invoiceItemName?.toLowerCase().includes(searchTerm.toLowerCase());

      const matchesStatus =
        statusFilter === 'ALL' || item.status === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [history, searchTerm, statusFilter]);

  return (
    <div className="card-panel">
      <div className="card-header-bar">
        <div className="card-header-title">
          <History size={18} color="var(--primary)" />
          <span>Recent Audit History</span>
          <span
            style={{
              fontSize: '0.75rem',
              fontWeight: 600,
              background: 'var(--bg-card-subtle)',
              padding: '0.15rem 0.5rem',
              borderRadius: '9999px',
              border: '1px solid var(--border-light)',
            }}
          >
            {filteredHistory.length} records
          </span>
        </div>

        <button
          className="btn-secondary"
          onClick={onRefresh}
          disabled={isLoading}
          title="Refresh History Table"
        >
          <RefreshCw size={13} className={isLoading ? 'spinner' : ''} />
          <span>Refresh</span>
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="table-filter-bar">
        <div className="search-box">
          <Search size={15} className="search-icon" />
          <input
            type="text"
            className="search-input"
            placeholder="Search by PO ID, Log ID, or reason..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        <div className="filter-tabs">
          <button
            type="button"
            className={`filter-tab-btn ${statusFilter === 'ALL' ? 'active' : ''}`}
            onClick={() => setStatusFilter('ALL')}
          >
            All ({history?.length || 0})
          </button>
          <button
            type="button"
            className={`filter-tab-btn ${statusFilter === 'CLEAR' ? 'active' : ''}`}
            onClick={() => setStatusFilter('CLEAR')}
          >
            Clear ({history?.filter((h) => h.status === 'CLEAR').length || 0})
          </button>
          <button
            type="button"
            className={`filter-tab-btn ${statusFilter === 'DISCREPANCY' ? 'active' : ''}`}
            onClick={() => setStatusFilter('DISCREPANCY')}
          >
            Discrepancy ({history?.filter((h) => h.status === 'DISCREPANCY').length || 0})
          </button>
        </div>
      </div>

      {/* History Data Table */}
      <div className="history-table-container">
        {filteredHistory.length === 0 ? (
          <div className="empty-state">
            <FileQuestion className="empty-icon" />
            <div className="empty-title">No audit records found</div>
            <div className="empty-desc">
              {searchTerm || statusFilter !== 'ALL'
                ? 'Try adjusting your search query or filter parameters.'
                : 'Run your first invoice audit above to view audit history and tracking records.'}
            </div>
          </div>
        ) : (
          <table className="history-table">
            <thead>
              <tr>
                <th style={{ width: '13%' }}>Log ID</th>
                <th style={{ width: '12%' }}>PO ID</th>
                <th style={{ width: '14%' }}>Audit Status</th>
                <th style={{ width: '33%' }}>Discrepancy Findings</th>
                <th style={{ width: '16%' }}>Audited At</th>
                <th style={{ width: '12%', textAlign: 'right' }}>Action</th>
              </tr>
            </thead>
            <tbody>
              {filteredHistory.map((item) => (
                <tr key={item.logId}>
                  <td>
                    <span style={{ fontFamily: 'var(--font-mono)', fontWeight: 600, fontSize: '0.8125rem' }}>
                      {item.logId}
                    </span>
                  </td>
                  <td>
                    <span style={{ fontWeight: 700, color: 'var(--primary)' }}>
                      {item.poId}
                    </span>
                  </td>
                  <td>
                    <StatusBadge status={item.status} />
                  </td>
                  <td>
                    <div
                      style={{
                        fontSize: '0.8125rem',
                        color: item.status === 'DISCREPANCY' ? 'var(--text-main)' : 'var(--text-muted)',
                        fontWeight: item.status === 'DISCREPANCY' ? 500 : 400,
                        maxLines: 2,
                        lineHeight: 1.4,
                      }}
                    >
                      {item.discrepancyReason || '—'}
                    </div>
                    {item.invoiceItemName && (
                      <div style={{ fontSize: '0.72rem', color: 'var(--text-subtle)', marginTop: '0.2rem' }}>
                        Item: {item.invoiceItemName} ({item.quantityDelivered} @ {formatCurrency(item.unitPriceCharged)})
                      </div>
                    )}
                  </td>
                  <td>
                    <div style={{ fontSize: '0.8125rem' }}>{formatDate(item.auditedAt)}</div>
                    <div style={{ fontSize: '0.72rem', color: 'var(--text-subtle)' }}>
                      {formatTimeAgo(item.auditedAt)}
                    </div>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <button
                      className="btn-secondary"
                      onClick={() => onSelectAudit(item.logId)}
                      style={{ padding: '0.35rem 0.65rem' }}
                      title="View audit record details"
                    >
                      <Eye size={13} />
                      <span>View</span>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
