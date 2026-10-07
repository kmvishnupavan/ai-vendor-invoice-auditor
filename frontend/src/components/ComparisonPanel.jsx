import React from 'react';
import { formatCurrency } from '../utils/formatCurrency';
import StatusBadge from './StatusBadge';

export default function ComparisonPanel({ invoice, purchaseOrder, comparison }) {
  if (!invoice || !purchaseOrder || !comparison) return null;

  const isQtyDiscrepant = comparison.quantityStatus === 'DISCREPANCY';
  const isPriceDiscrepant = comparison.priceStatus === 'DISCREPANCY';
  const isItemDiscrepant = comparison.itemStatus === 'DISCREPANCY';

  return (
    <div className="comparison-table-wrapper">
      <table className="comparison-table">
        <thead>
          <tr>
            <th style={{ width: '25%' }}>Parameter</th>
            <th style={{ width: '28%' }}>Invoice (Vendor)</th>
            <th style={{ width: '28%' }}>Purchase Order (Agreed)</th>
            <th style={{ width: '19%' }}>Variance / Result</th>
          </tr>
        </thead>
        <tbody>
          {/* Quantity Row */}
          <tr className={isQtyDiscrepant ? 'discrepant-row' : ''}>
            <td>
              <span className="param-name">Quantity</span>
              <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                Units delivered vs expected
              </div>
            </td>
            <td>
              <span className="param-value">{invoice.quantityDelivered} units</span>
            </td>
            <td>
              <span className="param-value">{purchaseOrder.expectedQuantity} units</span>
            </td>
            <td>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
                <StatusBadge status={comparison.quantityStatus} />
                {comparison.quantityDifference !== 0 && (
                  <span className={`diff-tag ${isQtyDiscrepant ? 'danger' : 'success'}`}>
                    {comparison.quantityDifference > 0 ? `+${comparison.quantityDifference}` : comparison.quantityDifference} units
                  </span>
                )}
              </div>
            </td>
          </tr>

          {/* Unit Price Row */}
          <tr className={isPriceDiscrepant ? 'discrepant-row' : ''}>
            <td>
              <span className="param-name">Unit Price</span>
              <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                Billed rate vs negotiated price
              </div>
            </td>
            <td>
              <span className="param-value">{formatCurrency(invoice.unitPriceCharged)}</span>
            </td>
            <td>
              <span className="param-value">{formatCurrency(purchaseOrder.agreedUnitPrice)}</span>
            </td>
            <td>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
                <StatusBadge status={comparison.priceStatus} />
                {comparison.priceDifference && Number(comparison.priceDifference) > 0 && (
                  <span className="diff-tag danger">
                    +{formatCurrency(comparison.priceDifference)}
                  </span>
                )}
              </div>
            </td>
          </tr>

          {/* Item Row */}
          <tr className={isItemDiscrepant ? 'discrepant-row' : ''}>
            <td>
              <span className="param-name">Item Description</span>
              <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                Product specification match
              </div>
            </td>
            <td>
              <span style={{ fontWeight: 600 }}>{invoice.itemName || '—'}</span>
            </td>
            <td>
              <span style={{ fontWeight: 600 }}>{purchaseOrder.itemName || '—'}</span>
            </td>
            <td>
              <StatusBadge status={comparison.itemStatus || 'CLEAR'} />
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}
