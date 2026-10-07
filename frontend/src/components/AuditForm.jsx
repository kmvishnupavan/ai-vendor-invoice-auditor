import React, { useState } from 'react';
import { Send, Sparkles, FileText, Check, AlertCircle, Bookmark } from 'lucide-react';

const TEST_CASES = [
  {
    id: 'test-1',
    label: 'Test 1: CLEAR (PO-9921)',
    poId: 'PO-9921',
    description: 'Exact match: 50 units @ ₹450',
    invoiceText: `Invoice Number: INV-1023
Purchase Order: PO-9921
Item: Wireless Mouse Pro
Quantity Supplied: 50
Unit Price Charged: ₹450`,
  },
  {
    id: 'test-2',
    label: 'Test 2: Price Discrepancy (PO-9921)',
    poId: 'PO-9921',
    description: 'Price markup: 50 units @ ₹499 (+₹49)',
    invoiceText: `Invoice Number: INV-1024
Purchase Order: PO-9921
Item: Wireless Mouse Pro
Quantity Supplied: 50
Unit Price Charged: ₹499`,
  },
  {
    id: 'test-3',
    label: 'Test 3: Qty Discrepancy (PO-9921)',
    poId: 'PO-9921',
    description: 'Short delivery: 40 units (-10 units)',
    invoiceText: `Invoice Number: INV-1025
Purchase Order: PO-9921
Item: Wireless Mouse Pro
Quantity Supplied: 40
Unit Price Charged: ₹450`,
  },
  {
    id: 'test-4',
    label: 'Test 4: Combined Discrepancy (PO-9921)',
    poId: 'PO-9921',
    description: 'Short delivery + Price increase',
    invoiceText: `Invoice Number: INV-1026
Purchase Order: PO-9921
Item: Wireless Mouse Pro
Quantity Supplied: 40
Unit Price Charged: ₹499`,
  },
  {
    id: 'test-5',
    label: 'Test 5: PO-9922 (Keyboard)',
    poId: 'PO-9922',
    description: 'Mechanical Keyboard RGB audit',
    invoiceText: `Invoice Number: INV-2005
Purchase Order: PO-9922
Item: Mechanical Keyboard RGB
Quantity Supplied: 20
Unit Price Charged: ₹1800`,
  },
];

export default function AuditForm({ onSubmit, isAuditing, auditStep, purchaseOrders }) {
  const [poId, setPoId] = useState('PO-9921');
  const [invoiceText, setInvoiceText] = useState(TEST_CASES[1].invoiceText);
  const [validationError, setValidationError] = useState('');
  const [activeTestId, setActiveTestId] = useState('test-2');

  const handleSelectTestCase = (testCase) => {
    setActiveTestId(testCase.id);
    setPoId(testCase.poId);
    setInvoiceText(testCase.invoiceText);
    setValidationError('');
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!poId.trim()) {
      setValidationError('Purchase Order ID is required.');
      return;
    }

    if (!invoiceText.trim() || invoiceText.trim().length < 10) {
      setValidationError('Please paste the vendor invoice text (minimum 10 characters).');
      return;
    }

    setValidationError('');
    onSubmit({ poId: poId.trim(), invoiceText: invoiceText.trim() });
  };

  return (
    <div className="card-panel">
      <div className="card-header-bar">
        <div className="card-header-title">
          <Sparkles size={18} color="var(--primary)" />
          <span>Audit New Invoice</span>
        </div>
        <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
          Step 1: Enter details
        </span>
      </div>

      <div className="card-body">
        {/* Quick Test Case Selector */}
        <div className="quick-tests-container">
          <div className="quick-tests-title">
            <Bookmark size={13} />
            <span>Preset Evaluation Test Cases</span>
          </div>
          <div className="quick-test-chips">
            {TEST_CASES.map((tc) => (
              <button
                key={tc.id}
                type="button"
                className={`chip-btn ${activeTestId === tc.id ? 'active' : ''}`}
                onClick={() => handleSelectTestCase(tc)}
                title={tc.description}
              >
                {tc.label}
              </button>
            ))}
          </div>
        </div>

        <form onSubmit={handleSubmit}>
          {/* PO ID Input */}
          <div className="form-group">
            <label htmlFor="poId" className="form-label">
              Purchase Order ID <span className="required">*</span>
            </label>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <input
                id="poId"
                type="text"
                className="form-input"
                placeholder="e.g. PO-9921"
                value={poId}
                onChange={(e) => {
                  setPoId(e.target.value);
                  setActiveTestId(null);
                }}
                disabled={isAuditing}
              />
              {purchaseOrders?.length > 0 && (
                <select
                  className="form-select"
                  style={{ width: 'auto', minWidth: '150px' }}
                  value={poId}
                  onChange={(e) => {
                    setPoId(e.target.value);
                    setActiveTestId(null);
                  }}
                  disabled={isAuditing}
                  aria-label="Select existing Purchase Order"
                >
                  <option value="">Choose Seeded PO</option>
                  {purchaseOrders.map((po) => (
                    <option key={po.poId} value={po.poId}>
                      {po.poId} ({po.itemName})
                    </option>
                  ))}
                </select>
              )}
            </div>
            <div className="form-hint">
              <span>Must match an active Purchase Order in the database.</span>
            </div>
          </div>

          {/* Invoice Text Area */}
          <div className="form-group">
            <label htmlFor="invoiceText" className="form-label">
              Vendor Invoice Text <span className="required">*</span>
            </label>
            <textarea
              id="invoiceText"
              rows={7}
              className="form-textarea"
              placeholder={`Invoice Number: INV-1023\nPurchase Order: PO-9921\nItem: Wireless Mouse Pro\nQuantity Supplied: 50\nUnit Price Charged: ₹499`}
              value={invoiceText}
              onChange={(e) => {
                setInvoiceText(e.target.value);
                setActiveTestId(null);
              }}
              disabled={isAuditing}
            />
            <div className="form-hint">
              <span>AI extracts PO ID, item name, quantity, and charged unit price.</span>
              <span>{invoiceText.length} chars</span>
            </div>
          </div>

          {validationError && (
            <div
              style={{
                color: 'var(--danger-text)',
                background: 'var(--danger-light)',
                border: '1px solid var(--danger-border)',
                padding: '0.5rem 0.75rem',
                borderRadius: 'var(--radius-sm)',
                fontSize: '0.8125rem',
                marginBottom: '1rem',
                display: 'flex',
                alignItems: 'center',
                gap: '0.35rem',
              }}
            >
              <AlertCircle size={14} />
              <span>{validationError}</span>
            </div>
          )}

          {/* Run Button */}
          <button
            type="submit"
            className="btn-primary"
            disabled={isAuditing}
            id="run-ai-audit-btn"
          >
            {isAuditing ? (
              <>
                <div className="status-dot pulse" style={{ width: 10, height: 10, background: '#ffffff' }} />
                <span>{auditStep || 'Processing Audit...'}</span>
              </>
            ) : (
              <>
                <Send size={16} />
                <span>Run AI Audit</span>
              </>
            )}
          </button>
        </form>
      </div>
    </div>
  );
}
