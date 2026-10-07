import React from 'react';
import { ShieldCheck, Cpu, Database, RefreshCw } from 'lucide-react';

export default function Header({ systemStatus, onRefresh, isRefreshing }) {
  const isAiLive = systemStatus?.aiMode === 'GEMINI_LIVE';
  const isDbConnected = systemStatus?.databaseConnected ?? true;

  return (
    <header className="header-wrapper">
      <div className="header-inner">
        <div className="header-brand">
          <div className="brand-icon-wrapper">
            <ShieldCheck size={26} strokeWidth={2.3} />
          </div>
          <div>
            <div className="brand-title">
              InvoiceGuard
              <span className="brand-badge">Enterprise Audit</span>
            </div>
            <div className="brand-subtitle">
              AI-Powered Vendor Invoice Discrepancy & Audit System
            </div>
          </div>
        </div>

        <div className="system-status-group">
          <div
            className={`status-pill ${isAiLive ? 'live' : 'test-mode'}`}
            title={isAiLive ? 'Connected to live Gemini API' : 'Running deterministic extraction engine'}
          >
            <span className="status-dot pulse" />
            <Cpu size={14} />
            <span>
              {isAiLive ? 'AI: Gemini 1.5 Flash' : 'AI: Deterministic Mode'}
            </span>
          </div>

          <div
            className="status-pill live"
            title={`Connected to ${systemStatus?.databaseType || 'Relational Database'}`}
          >
            <span className="status-dot" />
            <Database size={14} />
            <span>DB: {systemStatus?.databaseType || 'PostgreSQL'}</span>
          </div>

          <button
            className="btn-secondary"
            onClick={onRefresh}
            disabled={isRefreshing}
            title="Refresh dashboard metrics & history"
            style={{ padding: '0.35rem 0.65rem' }}
          >
            <RefreshCw size={13} className={isRefreshing ? 'spinner' : ''} />
            <span>Refresh</span>
          </button>
        </div>
      </div>
    </header>
  );
}
