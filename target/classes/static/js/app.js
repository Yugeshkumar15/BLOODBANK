// Global State
let donorsList = [];
let stockData = {};

document.addEventListener('DOMContentLoaded', () => {
  initApp();
});

function initApp() {
  setupTabs();
  checkServerHealth();
  loadStock();
  loadDonors();
  loadExpiringUnits();
  setupForms();
  setDefaultDates();
}

// Set Default Dates to Today (YYYY-MM-DD)
function setDefaultDates() {
  const today = new Date().toISOString().split('T')[0];
  const collectionDateInput = document.getElementById('collectionDate');
  const issueDateInput = document.getElementById('issueDate');

  if (collectionDateInput) {
    collectionDateInput.value = today;
    updateExpiryPreview(today);
  }
  if (issueDateInput) {
    issueDateInput.value = today;
  }
}

// Automatic Expiry Preview (+42 Days)
function updateExpiryPreview(collectionDateStr) {
  const expiryPreview = document.getElementById('expiryPreview');
  if (!collectionDateStr || !expiryPreview) return;

  const date = new Date(collectionDateStr);
  if (!isNaN(date.getTime())) {
    date.setDate(date.getDate() + 42);
    const expiryStr = date.toISOString().split('T')[0];
    expiryPreview.textContent = `Calculated Expiry Date: ${expiryStr} (42 days shelf life)`;
  }
}

// Setup Navigation Tabs
function setupTabs() {
  const tabBtns = document.querySelectorAll('.tab-btn');
  tabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      const targetTab = btn.getAttribute('data-tab');
      switchTab(targetTab);
    });
  });
}

function switchTab(tabId) {
  document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.classList.toggle('active', btn.getAttribute('data-tab') === tabId);
  });
  document.querySelectorAll('.tab-panel').forEach(panel => {
    panel.classList.toggle('active', panel.id === tabId);
  });

  // Refresh tab specific data
  if (tabId === 'tab-stock') loadStock();
  if (tabId === 'tab-donors') loadDonors();
  if (tabId === 'tab-expiring') loadExpiringUnits();
}

// Server Connection Status
async function checkServerHealth() {
  const statusDot = document.getElementById('statusDot');
  const statusText = document.getElementById('statusText');

  try {
    const res = await fetch('/api/bloodbank/stock');
    if (res.ok) {
      statusDot.classList.remove('offline');
      statusText.textContent = 'Backend Online (Port 8080)';
    } else {
      throw new Error('Server returned non-200 status');
    }
  } catch (err) {
    statusDot.classList.add('offline');
    statusText.textContent = 'Backend Offline / Connecting...';
  }
}

// 1. Fetch & Display Live Stock
async function loadStock() {
  try {
    const res = await fetch('/api/bloodbank/stock');
    if (!res.ok) throw new Error('Failed to fetch stock');
    const data = await res.json();
    
    stockData = data.stock || {};
    renderStockMetrics(data);
    renderStockGrid(stockData);
  } catch (err) {
    showToast('Failed to load stock data: ' + err.message, 'error');
  }
}

function renderStockMetrics(data) {
  const totalUnitsEl = document.getElementById('metricTotalUnits');
  if (totalUnitsEl) totalUnitsEl.textContent = data.totalAvailableUnits || 0;

  // Calculate critical stock count (groups with 0 units)
  let criticalCount = 0;
  if (data.stock) {
    Object.values(data.stock).forEach(units => {
      if (units === 0) criticalCount++;
    });
  }
  const criticalEl = document.getElementById('metricCriticalGroups');
  if (criticalEl) criticalEl.textContent = criticalCount;
}

function renderStockGrid(stock) {
  const container = document.getElementById('stockGridContainer');
  if (!container) return;

  const bloodGroups = [
    { key: 'A_POSITIVE', label: 'A+' },
    { key: 'A_NEGATIVE', label: 'A-' },
    { key: 'B_POSITIVE', label: 'B+' },
    { key: 'B_NEGATIVE', label: 'B-' },
    { key: 'AB_POSITIVE', label: 'AB+' },
    { key: 'AB_NEGATIVE', label: 'AB-' },
    { key: 'O_POSITIVE', label: 'O+' },
    { key: 'O_NEGATIVE', label: 'O-' }
  ];

  container.innerHTML = bloodGroups.map(group => {
    const units = stock[group.key] || 0;
    let badgeClass = 'high';
    let badgeText = 'Good Stock';

    if (units === 0) {
      badgeClass = 'critical';
      badgeText = 'Out of Stock';
    } else if (units < 5) {
      badgeClass = 'medium';
      badgeText = 'Low Stock';
    }

    const progressPercent = Math.min((units / 20) * 100, 100);

    return `
      <div class="stock-card">
        <div class="stock-header">
          <span class="blood-badge">${group.label}</span>
          <span class="stock-badge ${badgeClass}">${badgeText}</span>
        </div>
        <div class="stock-count">${units}</div>
        <div class="stock-unit-label">Usable Available Units</div>
        <div class="stock-progress-bar">
          <div class="stock-progress-fill" style="width: ${progressPercent}%;"></div>
        </div>
      </div>
    `;
  }).join('');
}

// 2. Fetch & Display Donors
async function loadDonors() {
  try {
    const res = await fetch('/api/donors');
    if (!res.ok) throw new Error('Failed to fetch donors');
    donorsList = await res.json();

    renderDonorsTable(donorsList);
    populateDonorSelects(donorsList);

    const activeDonorsEl = document.getElementById('metricActiveDonors');
    if (activeDonorsEl) activeDonorsEl.textContent = donorsList.length;
  } catch (err) {
    showToast('Failed to load donors: ' + err.message, 'error');
  }
}

function renderDonorsTable(donors) {
  const tbody = document.getElementById('donorsTableBody');
  if (!tbody) return;

  if (donors.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" style="text-align:center; color: var(--text-muted);">No donors registered yet. Add a donor using the form above.</td></tr>`;
    return;
  }

  tbody.innerHTML = donors.map(d => `
    <tr>
      <td><strong>#${d.id}</strong></td>
      <td>${escapeHtml(d.name)}</td>
      <td>${escapeHtml(d.phone)}</td>
      <td>${d.age} yrs</td>
      <td><span class="blood-badge" style="font-size:0.85rem; padding:0.2rem 0.5rem;">${formatBloodGroup(d.bloodGroup)}</span></td>
      <td>
        <button class="btn btn-secondary" style="padding:0.4rem 0.75rem; min-height:auto; font-size:0.85rem;" onclick="selectDonorForDonation(${d.id})">
          🩸 Donate
        </button>
      </td>
    </tr>
  `).join('');
}

function populateDonorSelects(donors) {
  const donationSelect = document.getElementById('donationDonorSelect');
  const eligibilitySelect = document.getElementById('eligibilityDonorSelect');

  const optionsHtml = '<option value="">-- Select Donor --</option>' + donors.map(d => `
    <option value="${d.id}">${escapeHtml(d.name)} (ID: ${d.id} - ${formatBloodGroup(d.bloodGroup)})</option>
  `).join('');

  if (donationSelect) donationSelect.innerHTML = optionsHtml;
  if (eligibilitySelect) eligibilitySelect.innerHTML = optionsHtml;
}

// Select Donor Shortcut
window.selectDonorForDonation = function(donorId) {
  switchTab('tab-donations');
  const donationSelect = document.getElementById('donationDonorSelect');
  if (donationSelect) {
    donationSelect.value = donorId;
    donationSelect.focus();
  }
};

// 3. Check Donor Eligibility
async function handleCheckEligibility() {
  const donorId = document.getElementById('eligibilityDonorSelect').value;
  const bannerContainer = document.getElementById('eligibilityResult');

  if (!donorId) {
    showToast('Please select a donor to check eligibility.', 'error');
    return;
  }

  try {
    const res = await fetch(`/api/bloodbank/donors/${donorId}/eligibility`);
    const data = await res.json();

    if (!res.ok) {
      throw new Error(data.message || 'Error checking eligibility');
    }

    renderEligibilityBanner(data, bannerContainer);
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function renderEligibilityBanner(data, container) {
  if (!container) return;

  const isEligible = data.eligible;
  const statusClass = isEligible ? 'eligible' : 'ineligible';
  const icon = isEligible ? '✅' : '❌';

  let lastDonationStr = data.lastDonationDate ? data.lastDonationDate : 'None';
  let daysSinceStr = data.daysSinceLastDonation !== null ? `${data.daysSinceLastDonation} days ago` : 'N/A';

  container.innerHTML = `
    <div class="eligibility-banner ${statusClass}">
      <div class="eligibility-banner-icon">${icon}</div>
      <div class="eligibility-details">
        <h4>${escapeHtml(data.message)}</h4>
        <p><strong>Last Donation Date:</strong> ${lastDonationStr} (${daysSinceStr})</p>
        <p><strong>Minimum Gap Requirement:</strong> ${data.minimumGapDays} Days</p>
      </div>
    </div>
  `;
}

// 4. Fetch & Display Expiring Units
async function loadExpiringUnits() {
  try {
    const res = await fetch('/api/bloodbank/expiring');
    if (!res.ok) throw new Error('Failed to fetch expiring units');
    const units = await res.json();

    renderExpiringTable(units);
    const metricExpiringEl = document.getElementById('metricExpiringCount');
    if (metricExpiringEl) metricExpiringEl.textContent = units.length;
  } catch (err) {
    showToast('Failed to load expiring units: ' + err.message, 'error');
  }
}

function renderExpiringTable(units) {
  const tbody = document.getElementById('expiringTableBody');
  if (!tbody) return;

  if (units.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; color: var(--text-muted);">No blood units expiring within the next 7 days. Stock is healthy!</td></tr>`;
    return;
  }

  tbody.innerHTML = units.map(u => `
    <tr>
      <td><strong>Unit #${u.id}</strong></td>
      <td><span class="blood-badge" style="font-size:0.85rem; padding:0.2rem 0.5rem;">${formatBloodGroup(u.bloodGroup)}</span></td>
      <td>${escapeHtml(u.donorName)} (Donation #${u.donationId})</td>
      <td>${u.collectionDate}</td>
      <td><strong style="color: var(--danger);">${u.expiryDate}</strong></td>
      <td><span class="stock-badge critical">${u.daysUntilExpiry} days left</span></td>
      <td><strong>${u.availableUnits} / ${u.totalUnits} units</strong></td>
    </tr>
  `).join('');
}

// Setup Event Listeners for Forms
function setupForms() {
  // Add Donor Form
  const donorForm = document.getElementById('addDonorForm');
  if (donorForm) {
    donorForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      await handleAddDonor();
    });
  }

  // Record Donation Form
  const donationForm = document.getElementById('recordDonationForm');
  if (donationForm) {
    const collectionDateInput = document.getElementById('collectionDate');
    if (collectionDateInput) {
      collectionDateInput.addEventListener('change', (e) => {
        updateExpiryPreview(e.target.value);
      });
    }

    donationForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      handleRecordDonation();
    });
  }

  // Issue Blood Form
  const issueForm = document.getElementById('issueBloodForm');
  if (issueForm) {
    issueForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      handleIssueBlood();
    });
  }

  // Eligibility Check Button
  const checkBtn = document.getElementById('btnCheckEligibility');
  if (checkBtn) {
    checkBtn.addEventListener('click', handleCheckEligibility);
  }
}

// 5. Handle Add Donor
async function handleAddDonor() {
  const name = document.getElementById('donorName').value.trim();
  const phone = document.getElementById('donorPhone').value.trim();
  const age = parseInt(document.getElementById('donorAge').value, 10);
  const bloodGroup = document.getElementById('donorBloodGroup').value;

  if (!name || !phone || !age || !bloodGroup) {
    showToast('Please fill all donor fields.', 'error');
    return;
  }

  try {
    const res = await fetch('/api/donors', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, phone, age, bloodGroup })
    });

    const data = await res.json();
    if (!res.ok) {
      throw new Error(data.message || 'Failed to create donor');
    }

    showToast(`Donor '${data.name}' added successfully! (ID: ${data.id})`, 'success');
    document.getElementById('addDonorForm').reset();
    loadDonors();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// 6. Handle Record Donation
async function handleRecordDonation() {
  const donorId = parseInt(document.getElementById('donationDonorSelect').value, 10);
  const collectionDate = document.getElementById('collectionDate').value;
  const units = parseInt(document.getElementById('donationUnits').value, 10);

  if (!donorId || !collectionDate || !units) {
    showToast('Please fill all donation fields.', 'error');
    return;
  }

  try {
    const res = await fetch('/api/bloodbank/donations', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ donorId, collectionDate, units })
    });

    const data = await res.json();
    if (!res.ok) {
      throw new Error(data.message || 'Donation rejected');
    }

    showToast(`Donation logged! ${data.units} units added for ${data.donorName}. Expiry: ${data.expiryDate}`, 'success');
    loadStock();
    loadExpiringUnits();
    handleCheckEligibility();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// 7. Handle Issue Blood
async function handleIssueBlood() {
  const bloodGroup = document.getElementById('issueBloodGroup').value;
  const patientName = document.getElementById('issuePatientName').value.trim();
  const units = parseInt(document.getElementById('issueUnits').value, 10);
  const issueDate = document.getElementById('issueDate').value;

  if (!bloodGroup || !patientName || !units) {
    showToast('Please fill all issue request fields.', 'error');
    return;
  }

  try {
    const res = await fetch('/api/bloodbank/issues', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ bloodGroup, patientName, units, issueDate })
    });

    const data = await res.json();
    if (!res.ok) {
      throw new Error(data.message || 'Failed to issue blood');
    }

    showToast(`Blood issued! ${data.totalUnitsIssued} units of ${formatBloodGroup(data.bloodGroup)} issued to ${data.patientName}.`, 'success');
    document.getElementById('issueBloodForm').reset();
    setDefaultDates();
    loadStock();
    loadExpiringUnits();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// Helper Functions
function formatBloodGroup(bg) {
  if (!bg) return '';
  return bg.replace('_POSITIVE', '+').replace('_NEGATIVE', '-');
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

// Toast Notifications
function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  const icon = type === 'success' ? '✅' : '⚠️';

  toast.innerHTML = `
    <span class="toast-icon">${icon}</span>
    <span class="toast-message">${escapeHtml(message)}</span>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease-out';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}
