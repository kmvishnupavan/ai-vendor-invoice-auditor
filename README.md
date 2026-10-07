# 🛡️ InvoiceGuard — AI-Powered Vendor Invoice Discrepancy & Audit System

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-8-646CFF?logo=vite&logoColor=white)](https://vitejs.dev/)
[![Gemini AI](https://img.shields.io/badge/Google%20Gemini-1.5%20Flash-4285F4?logo=google&logoColor=white)](https://ai.google.dev/)
[![Database](https://img.shields.io/badge/Database-PostgreSQL%20%7C%20MySQL%20%7C%20H2-336791?logo=postgresql&logoColor=white)](https://www.postgresql.org/)

An enterprise-grade, AI-assisted invoice auditing operational dashboard built for retail, e-commerce, and supply-chain operations. **InvoiceGuard** automates vendor billing verification by using AI exclusively for structured extraction and enforcing strict business logic comparisons deterministically within Java.

---

## 📌 Problem Statement

In modern procurement and supply-chain workflows:
- Vendors frequently bill higher unit prices than contracted in Purchase Orders (POs).
- Quantities shipped or invoiced often fall short of the agreed delivery commitments.
- Human auditing across thousands of unstructured vendor invoices is time-consuming, error-prone, and causes financial leakages.
- Directly relying on AI to make business pass/fail decisions introduces hallucination risks, non-determinism, and lack of auditable compliance.

**InvoiceGuard** solves this by strictly separating concerns:
1. **AI (Gemini)** acts as a pure extraction engine to parse unstructured text into structured JSON.
2. **Java (Spring Boot)** performs 100% deterministic business rule validation and generates human-readable audit explanations.
3. **Relational Database** maintains a tamper-evident, queryable audit trail.

---

## 🏗️ System Architecture

```text
React Operational Dashboard (Vite)
                │
                │ REST API (JSON)
                ▼
Spring Boot Backend (Port 8080)
    ┌───────────────────────────┬───────────────────────────┐
    │                           │                           │
    ▼                           ▼                           ▼
AI Extraction Service   Java Business Rules Engine   Relational Database
 (Google Gemini API)     • Quantity Validation        • purchase_orders
 • Unstructured Parsing  • Price Discrepancy Check    • audit_logs
 • Structured JSON       • Reason Generation          (PostgreSQL / MySQL / H2)
                         • Audit Trail Persist
```

### Architectural Responsibilities:
- **AI (Gemini)**: Extracts `poId`, `itemName`, `quantityDelivered`, and `unitPriceCharged`. Zero business logic. Single API call per audit.
- **Java (Spring Boot)**: Retrieves the PO, performs strict quantitative checks, calculates variance, assigns status (`CLEAR` / `DISCREPANCY`), and records audit logs.
- **Database (PostgreSQL / MySQL / H2)**: Persists purchase contracts and historical audits.
- **React Frontend**: Provides an enterprise single-page dashboard with real-time discrepancy visualizers, variance matrices, and history inspector.

---

## ⚡ Core Business Validation Rules

| Rule | Condition | Resulting Status | Variance |
| :--- | :--- | :--- | :--- |
| **Quantity Rule** | `quantityDelivered < expectedQuantity` | **DISCREPANCY** | Negative delivery variance (shortfall) |
| **Quantity Rule** | `quantityDelivered >= expectedQuantity` | **CLEAR** | Complete or over-delivery accepted |
| **Price Rule** | `unitPriceCharged > agreedUnitPrice` | **DISCREPANCY** | Price escalation over PO contract |
| **Price Rule** | `unitPriceCharged <= agreedUnitPrice` | **CLEAR** | Contract compliant (discounts accepted) |
| **Item Rule** | Incompatible item description | **DISCREPANCY** | Product mismatch warning |
| **PO ID Rule** | Invoiced PO ID ≠ Submitted PO ID | **VALIDATION ERROR** | Rejected prior to audit |

### Final Audit Determination:
$$\text{Status} = \begin{cases} \text{DISCREPANCY}, & \text{if any discrepancy exists} \\ \text{CLEAR}, & \text{if all checks pass} \end{cases}$$

---

## 🗄️ Database Schema

### Table 1: `purchase_orders`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Internal primary key |
| `po_id` | `VARCHAR(50)` | UNIQUE, NOT NULL | Purchase Order ID (e.g. `PO-9921`) |
| `item_name` | `VARCHAR(255)` | NOT NULL | Item name |
| `expected_quantity` | `INT` | NOT NULL | Contracted quantity |
| `agreed_unit_price` | `DECIMAL(12,2)`| NOT NULL | Contracted unit price (₹) |
| `created_at` | `TIMESTAMP` | NOT NULL | Creation timestamp |

### Table 2: `audit_logs`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | PRIMARY KEY, AUTO_INCREMENT | Internal primary key |
| `log_id` | `VARCHAR(64)` | UNIQUE, NOT NULL | Unique audit log ID (e.g. `AUD-9B8A12F0`) |
| `po_id` | `VARCHAR(50)` | NOT NULL | Purchase order ID |
| `audit_status` | `VARCHAR(20)` | NOT NULL | `CLEAR` or `DISCREPANCY` |
| `discrepancy_reason` | `TEXT` | | Detailed explanation generated by Java |
| `invoice_item_name` | `VARCHAR(255)` | | Extracted invoice item name |
| `quantity_delivered`| `INT` | | Extracted quantity |
| `unit_price_charged`| `DECIMAL(12,2)`| | Extracted price charged |
| `audited_at` | `TIMESTAMP` | NOT NULL | Audit execution timestamp |

---

## 📂 Project Directory Structure

```text
java ful stack/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/invoiceaudit/
│   │   │   │   ├── config/              # CorsConfig, DataInitializer
│   │   │   │   ├── controller/          # REST Controllers
│   │   │   │   ├── dto/                 # Request & Response DTOs
│   │   │   │   ├── entity/              # PurchaseOrder & AuditLog JPA Entities
│   │   │   │   ├── exception/           # Custom Exceptions & GlobalExceptionHandler
│   │   │   │   ├── repository/          # Spring Data JPA Repositories
│   │   │   │   ├── service/             # AIExtractionService, InvoiceAuditService, etc.
│   │   │   │   └── InvoiceAuditApplication.java
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/java/com/example/invoiceaudit/
│   │       └── InvoiceAuditServiceTest.java
│   ├── pom.xml
│   └── .env.example
├── frontend/
│   ├── src/
│   │   ├── components/                  # Header, AuditForm, ComparisonPanel, etc.
│   │   ├── hooks/                       # useAudit.js
│   │   ├── pages/                       # Dashboard.jsx
│   │   ├── services/                    # api.js (Axios)
│   │   ├── utils/                       # formatCurrency.js, formatDate.js
│   │   ├── App.jsx
│   │   ├── index.css                    # Modern Enterprise Design System
│   │   └── main.jsx
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── .env.example
├── .gitignore
├── .env.example
└── README.md
```

---

## 🚀 Getting Started & Setup Instructions

### Prerequisites
- **Java**: JDK 17 or higher (`java -version`)
- **Maven**: 3.8+ (A portable Maven installation is located at `C:\Users\kmvis\maven\apache-maven-3.9.6\bin\mvn.cmd`)
- **Node.js**: v18+ & npm (`node -v`, `npm -v`)
- **Database**: PostgreSQL or MySQL (or run directly with the zero-config built-in H2 engine)

---

### Step 1: Environment Variables Setup

Copy `.env.example` in both folders or configure your system environment:

#### Backend (`backend/.env` or system environment):
```bash
# Relational Database (Defaults to PostgreSQL-mode H2 if not provided)
DB_URL=jdbc:postgresql://localhost:5432/invoiceguard_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Google Gemini API Key (Get free key at https://aistudio.google.com/)
# If empty, InvoiceGuard uses its built-in deterministic extraction engine for offline demos
AI_API_KEY=your_gemini_api_key_here
```

#### Frontend (`frontend/.env`):
```bash
VITE_API_BASE_URL=http://localhost:8080
```

---

### Step 2: Running the Spring Boot Backend

Open a terminal in the `backend` directory:

```powershell
cd "c:\Users\kmvis\java ful stack\backend"

# Run with Maven
mvn spring-boot:run
```

*The application automatically seeds initial Purchase Orders (`PO-9921`, `PO-9922`, `PO-9923`, etc.) upon startup.*

Backend will start on: **`http://localhost:8080`**

---

### Step 3: Running the React Frontend

Open a second terminal in the `frontend` directory:

```powershell
cd "c:\Users\kmvis\java ful stack\frontend"

# Install dependencies (if not done)
npm install

# Start development server
npm run dev
```

Frontend will start on: **`http://localhost:5173`**

---

## 🧪 Sample Evaluation Test Cases

The dashboard includes **1-Click Test Case Presets** for immediate demonstration:

### Case 1: CLEAR (Exact Match)
- **PO ID**: `PO-9921`
- **Invoice**:
  ```text
  Invoice Number: INV-1023
  Purchase Order: PO-9921
  Item: Wireless Mouse Pro
  Quantity Supplied: 50
  Unit Price Charged: ₹450
  ```
- **Expected Outcome**: `CLEAR` (0 discrepancies). Reason: *"Invoice matches purchase order specifications with zero discrepancies."*

---

### Case 2: Price Discrepancy (Escalated Rate)
- **PO ID**: `PO-9921`
- **Invoice**:
  ```text
  Invoice Number: INV-1024
  Purchase Order: PO-9921
  Item: Wireless Mouse Pro
  Quantity Supplied: 50
  Unit Price Charged: ₹499
  ```
- **Expected Outcome**: `DISCREPANCY` (Price +₹49.00). Reason: *"Vendor unit price is ₹499.00 while the agreed purchase-order price is ₹450.00."*

---

### Case 3: Quantity Discrepancy (Short Delivery)
- **PO ID**: `PO-9921`
- **Invoice**:
  ```text
  Invoice Number: INV-1025
  Purchase Order: PO-9921
  Item: Wireless Mouse Pro
  Quantity Supplied: 40
  Unit Price Charged: ₹450
  ```
- **Expected Outcome**: `DISCREPANCY` (Quantity -10 units). Reason: *"Vendor delivered 40 units while the purchase order expected 50 units."*

---

### Case 4: Combined Discrepancy (Quantity + Price)
- **PO ID**: `PO-9921`
- **Invoice**:
  ```text
  Invoice Number: INV-1026
  Purchase Order: PO-9921
  Item: Wireless Mouse Pro
  Quantity Supplied: 40
  Unit Price Charged: ₹499
  ```
- **Expected Outcome**: `DISCREPANCY`. Reason: *"1. Quantity discrepancy: vendor delivered 40 units while 50 units were expected. 2. Price discrepancy: vendor charged ₹499.00 while the agreed price was ₹450.00."*

---

### Case 5: Mismatched Purchase Order ID (Edge Case)
- **Submitted PO ID**: `PO-9921`
- **Invoice**: Text containing `Purchase Order: PO-9922`
- **Expected Outcome**: HTTP 400 Bad Request with user message: *"Invoice PO ID PO-9922 does not match the submitted PO ID PO-9921."*

---

## 📡 REST API Documentation

### 1. Execute Audit
- **POST** `/api/audits`
- **Request Body**:
  ```json
  {
    "poId": "PO-9921",
    "invoiceText": "Invoice Number: INV-1023\nPurchase Order: PO-9921\nItem: Wireless Mouse Pro\nQuantity Supplied: 50\nUnit Price Charged: ₹450"
  }
  ```
- **Response**:
  ```json
  {
    "logId": "AUD-D73149A0",
    "status": "CLEAR",
    "invoice": {
      "poId": "PO-9921",
      "itemName": "Wireless Mouse Pro",
      "quantityDelivered": 50,
      "unitPriceCharged": 450.00
    },
    "purchaseOrder": {
      "poId": "PO-9921",
      "itemName": "Wireless Mouse Pro",
      "expectedQuantity": 50,
      "agreedUnitPrice": 450.00
    },
    "comparison": {
      "quantityStatus": "CLEAR",
      "priceStatus": "CLEAR",
      "itemStatus": "CLEAR",
      "quantityDifference": 0,
      "priceDifference": 0.00
    },
    "discrepancyReason": "Invoice matches purchase order specifications with zero discrepancies.",
    "auditedAt": "2026-10-07T09:30:00"
  }
  ```

### 2. Summary Statistics
- **GET** `/api/audits/summary`
- **Response**: `{ "totalAudits": 24, "clearAudits": 18, "discrepancies": 6, "discrepancyRate": 25.0 }`

### 3. Audit History
- **GET** `/api/audits` (Sorted by `auditedAt` DESC)
- **GET** `/api/audits/{logId}` (Specific audit by Log ID)

### 4. Purchase Orders
- **GET** `/api/purchase-orders` (All POs)
- **GET** `/api/purchase-orders/{poId}` (Specific PO)

### 5. System Health & AI Status
- **GET** `/api/system/status`
- **Response**: `{ "aiConnected": true, "aiMode": "GEMINI_LIVE", "aiModel": "gemini-1.5-flash", "databaseConnected": true, "databaseType": "PostgreSQL" }`

---

## 🛡️ Security & Reliability Best Practices
- **No Secrets in Frontend**: The React client has zero access to AI keys or database credentials.
- **Strict Business Logic Isolation**: The AI model is strictly prohibited from making financial or compliance decisions. All mathematical and business rule evaluations execute in verified Java code.
- **BigDecimal Precision**: All pricing calculations utilize `BigDecimal` with 2 decimal places to prevent IEEE 754 floating-point inaccuracies.
- **Global Error Handling**: Stack traces are never leaked to the client; user-friendly structured error responses are returned for all 4xx/5xx scenarios.

---

## 🔮 Future Improvements
- Automated vendor notification webhooks for detected discrepancies.
- Multi-currency conversion via live exchange rate APIs.
- Multi-line invoice item batch auditing.
- Export audit trail records as signed audit PDF / CSV reports.
