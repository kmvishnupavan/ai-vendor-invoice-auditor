import { useState, useEffect, useCallback } from 'react';
import api from '../services/api';

export function useAudit() {
  const [summary, setSummary] = useState({
    totalAudits: 0,
    clearAudits: 0,
    discrepancies: 0,
    discrepancyRate: 0,
  });
  const [history, setHistory] = useState([]);
  const [purchaseOrders, setPurchaseOrders] = useState([]);
  const [currentAudit, setCurrentAudit] = useState(null);
  const [selectedAuditDetail, setSelectedAuditDetail] = useState(null);
  const [isAuditing, setIsAuditing] = useState(false);
  const [auditStep, setAuditStep] = useState('');
  const [isLoadingHistory, setIsLoadingHistory] = useState(false);
  const [systemStatus, setSystemStatus] = useState({
    aiConnected: true,
    aiMode: 'LOCAL_INTELLIGENT_EXTRACTOR',
    aiModel: 'gemini-1.5-flash',
    databaseConnected: true,
    databaseType: 'Relational DB',
  });
  const [error, setError] = useState(null);

  const fetchSummaryAndHistory = useCallback(async () => {
    try {
      setIsLoadingHistory(true);
      const [summaryData, historyData, poData, statusData] = await Promise.allSettled([
        api.fetchSummary(),
        api.fetchAuditHistory(),
        api.fetchAllPurchaseOrders(),
        api.fetchSystemStatus(),
      ]);

      if (summaryData.status === 'fulfilled') setSummary(summaryData.value);
      if (historyData.status === 'fulfilled') setHistory(historyData.value);
      if (poData.status === 'fulfilled') setPurchaseOrders(poData.value);
      if (statusData.status === 'fulfilled') setSystemStatus(statusData.value);
    } catch (err) {
      console.error('Failed to load dashboard data:', err);
    } finally {
      setIsLoadingHistory(false);
    }
  }, []);

  useEffect(() => {
    fetchSummaryAndHistory();
  }, [fetchSummaryAndHistory]);

  const runAudit = async ({ poId, invoiceText }) => {
    setIsAuditing(true);
    setError(null);
    setAuditStep('Extracting invoice information with AI model...');

    // Progress simulation for user feedback
    const stepTimer1 = setTimeout(() => {
      setAuditStep('Validating extracted fields against Purchase Order in Java...');
    }, 700);

    const stepTimer2 = setTimeout(() => {
      setAuditStep('Applying quantity & price business rules and persisting audit...');
    }, 1400);

    try {
      const result = await api.submitAudit({ poId, invoiceText });
      setCurrentAudit(result);
      // Immediately refresh summary cards and history table without full browser reload
      await fetchSummaryAndHistory();
      return result;
    } catch (err) {
      setError(err.message || 'Audit execution failed. Please verify your input.');
      throw err;
    } finally {
      clearTimeout(stepTimer1);
      clearTimeout(stepTimer2);
      setIsAuditing(false);
      setAuditStep('');
    }
  };

  const viewAuditDetails = async (logId) => {
    try {
      const audit = await api.fetchAuditById(logId);
      setSelectedAuditDetail(audit);
    } catch (err) {
      setError(`Unable to load audit details: ${err.message}`);
    }
  };

  const closeAuditModal = () => {
    setSelectedAuditDetail(null);
  };

  const dismissError = () => {
    setError(null);
  };

  return {
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
    refreshAll: fetchSummaryAndHistory,
    clearCurrentAudit: () => setCurrentAudit(null),
  };
}
