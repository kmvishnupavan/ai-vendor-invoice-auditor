import React from 'react';
import Header from '../components/Header';
import SummaryCards from '../components/SummaryCards';
import AuditForm from '../components/AuditForm';
import AuditResult from '../components/AuditResult';
import AuditHistory from '../components/AuditHistory';
import AuditDetailModal from '../components/AuditDetailModal';
import ErrorAlert from '../components/ErrorAlert';
import { useAudit } from '../hooks/useAudit';
import { Sparkles, FileSearch } from 'lucide-react';

export default function Dashboard() {
  const {
    summary,
    history,
    purchaseOrders,
    currentAudit,
    selectedAuditDetail,
    isAuditing,
    auditStep,
    isLoadingHistory,
    systemStatus,
    error,
    runAudit,
    viewAuditDetails,
    closeAuditModal,
    dismissError,
    refreshAll,
  } = useAudit();

  return (
    <div className="app-container">
      <Header
        systemStatus={systemStatus}
        onRefresh={refreshAll}
        isRefreshing={isLoadingHistory}
      />

      <main className="main-content">
        {error && <ErrorAlert message={error} onDismiss={dismissError} />}

        {/* Top Summary Metrics Cards */}
        <SummaryCards summary={summary} />

        {/* Operational Grid: Form & Assessment Results */}
        <div className="dashboard-grid">
          <div>
            <AuditForm
              onSubmit={runAudit}
              isAuditing={isAuditing}
              auditStep={auditStep}
              purchaseOrders={purchaseOrders}
            />
          </div>

          <div>
            {currentAudit ? (
              <AuditResult audit={currentAudit} />
            ) : (
              <div className="card-panel" style={{ height: '100%', minHeight: '360px', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
                <div className="empty-state" style={{ padding: '3rem 2rem' }}>
                  <div
                    style={{
                      width: 56,
                      height: 56,
                      borderRadius: '50%',
                      background: 'var(--primary-light)',
                      color: 'var(--primary)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      margin: '0 auto 1rem',
                    }}
                  >
                    <FileSearch size={28} />
                  </div>
                  <div className="empty-title" style={{ fontSize: '1.125rem' }}>
                    Ready for Invoice Evaluation
                  </div>
                  <div className="empty-desc" style={{ marginTop: '0.5rem', lineHeight: 1.5 }}>
                    Select a preset test case or enter a Purchase Order ID with vendor invoice text on the left, then click <strong>"Run AI Audit"</strong> to view real-time discrepancy comparison.
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Audit History Log */}
        <AuditHistory
          history={history}
          onSelectAudit={viewAuditDetails}
          onRefresh={refreshAll}
          isLoading={isLoadingHistory}
        />
      </main>

      {/* Historical Audit Detail Modal */}
      {selectedAuditDetail && (
        <AuditDetailModal
          audit={selectedAuditDetail}
          onClose={closeAuditModal}
        />
      )}
    </div>
  );
}
