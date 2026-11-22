import React, { Component, useEffect, useState } from "react";
import api from "./api";
import { LayoutDashboard, Users, UserCog, Car, CalendarDays, Wrench, Receipt, Settings, LogOut, Menu, Bell, ChevronRight, KeyRound, X } from "lucide-react";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import UsersPage from "./pages/UsersPage";
import MechanicsPage from "./pages/MechanicsPage";
import VehiclesPage from "./pages/VehiclesPage";
import ServicesPage from "./pages/ServicesPage";
import AppointmentsPage from "./pages/AppointmentsPage";
import BillsPage from "./pages/BillsPage";
import GaragePage from "./pages/GaragePage";

const nav = [
  { key: "dashboard", label: "Dashboard", icon: LayoutDashboard },
  { key: "users", label: "Customers & Staff", icon: Users },
  { key: "mechanics", label: "Mechanics", icon: UserCog },
  { key: "vehicles", label: "Vehicles", icon: Car },
  { key: "services", label: "Services", icon: Wrench },
  { key: "appointments", label: "Appointments", icon: CalendarDays },
  { key: "bills", label: "Bills & Payments", icon: Receipt },
  { key: "garage", label: "Garage Settings", icon: Settings },
];

class ErrorBoundary extends Component {
  state = { hasError: false, message: "" };
  static getDerivedStateFromError(error) { return { hasError: true, message: error?.message || "This page could not be loaded." }; }
  componentDidCatch(error) { console.error("GarejX page error:", error); }
  render() {
    if (!this.state.hasError) return this.props.children;
    return <div className="page-error"><div className="page-error-icon">!</div><h2>Unable to display this page</h2><p>{this.state.message}</p><button className="primary" onClick={() => this.setState({ hasError: false, message: "" })}>Try again</button></div>;
  }
}

function ChangePasswordModal({ onClose }) {
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);
  async function submit(e) {
    e.preventDefault(); setError("");
    if (newPassword.length < 6) return setError("New password must be at least 6 characters.");
    if (newPassword !== confirmPassword) return setError("New password and confirmation do not match.");
    setSaving(true);
    try { await api.put("/auth/change-password", { oldPassword, newPassword }); onClose(true); }
    catch (err) { setError(err.response?.data?.message || "Unable to change password."); }
    finally { setSaving(false); }
  }
  return <div className="modal-backdrop" onMouseDown={e => e.target === e.currentTarget && onClose(false)}><div className="modal">
    <div className="modal-head"><h2>Change Password</h2><button className="icon-btn" onClick={() => onClose(false)}><X size={18}/></button></div>
    <form onSubmit={submit} className="form-grid">
      <p className="muted">Change the password for your currently signed-in ADMIN account.</p>
      <label className="field"><span>Current password</span><input required type="password" value={oldPassword} onChange={e => setOldPassword(e.target.value)} autoComplete="current-password" /></label>
      <label className="field"><span>New password</span><input required type="password" minLength="6" value={newPassword} onChange={e => setNewPassword(e.target.value)} autoComplete="new-password" /></label>
      <label className="field"><span>Confirm new password</span><input required type="password" minLength="6" value={confirmPassword} onChange={e => setConfirmPassword(e.target.value)} autoComplete="new-password" /></label>
      {error && <div className="error-box">{error}</div>}
      <div className="modal-actions"><button type="button" className="secondary" onClick={() => onClose(false)}>Cancel</button><button className="primary" disabled={saving}>{saving ? "Changing…" : "Change password"}</button></div>
    </form>
  </div></div>;
}

export default function App() {
  const [user, setUser] = useState(() => { try { return JSON.parse(localStorage.getItem("garejx_user") || "null"); } catch { return null; } });
  const [page, setPage] = useState("dashboard");
  const [mobile, setMobile] = useState(false);
  const [ready, setReady] = useState(false);
  const [accountOpen, setAccountOpen] = useState(false);
  const [passwordOpen, setPasswordOpen] = useState(false);
  const [notice, setNotice] = useState("");

  function logout() { localStorage.removeItem("garejx_token"); localStorage.removeItem("garejx_user"); setUser(null); setPage("dashboard"); }

  useEffect(() => {
    let active = true;
    async function verify() {
      if (!user) { if (active) setReady(true); return; }
      try {
        const r = await api.get("/auth/me");
        if (r.data?.user?.role !== "ADMIN") { logout(); return; }
        if (active) setUser(r.data.user);
      } catch { logout(); }
      finally { if (active) setReady(true); }
    }
    verify();
    return () => { active = false; };
  }, []);

  useEffect(() => { if (!notice) return; const t = setTimeout(() => setNotice(""), 3500); return () => clearTimeout(t); }, [notice]);

  if (!ready) return <div className="loading"><div className="spinner"/>Loading GarejX…</div>;
  if (!user) return <Login onLogin={u => { setUser(u); setPage("dashboard"); }} />;

  const Page = { dashboard: Dashboard, users: UsersPage, mechanics: MechanicsPage, vehicles: VehiclesPage, services: ServicesPage, appointments: AppointmentsPage, bills: BillsPage, garage: GaragePage }[page] || Dashboard;
  const initials = (user.name || "A").slice(0, 1).toUpperCase();

  return <div className="app-shell">
    <aside className={`sidebar ${mobile ? "open" : ""}`}>
      <div className="brand"><div className="brand-mark">GX</div><div><strong>GarejX</strong><span>Admin Console</span></div></div>
      <div className="nav-title">MANAGEMENT</div>
      <nav>{nav.map(n => { const I = n.icon; return <button className={page === n.key ? "active" : ""} key={n.key} onClick={() => { setPage(n.key); setMobile(false); }}><I size={19}/><span>{n.label}</span>{page === n.key && <ChevronRight size={16} className="nav-arrow"/>}</button>; })}</nav>
      <div className="sidebar-bottom"><div className="admin-mini"><div className="avatar">{initials}</div><div><b>{user.name}</b><small>Administrator</small></div></div><button className="account-link" onClick={() => setAccountOpen(v => !v)}><KeyRound size={17}/> Account</button><button className="logout" onClick={logout}><LogOut size={18}/>Logout</button></div>
    </aside>
    <main className="main">
      <header className="topbar"><button className="menu-btn" onClick={() => setMobile(v => !v)}><Menu/></button><div className="crumb"><span>GarejX</span><b>/</b><strong>{nav.find(x => x.key === page)?.label}</strong></div><div className="top-actions"><div className="api-pill"><span/>API Connected</div><Bell size={19}/><button className="top-avatar top-avatar-btn" onClick={() => setAccountOpen(v => !v)}>{initials}</button></div>
        {accountOpen && <div className="account-menu"><div><b>{user.name}</b><small>{user.email}</small></div><button onClick={() => { setAccountOpen(false); setPasswordOpen(true); }}><KeyRound size={16}/> Change password</button><button onClick={logout}><LogOut size={16}/> Logout</button></div>}
      </header>
      {notice && <div className="toast success-box">{notice}</div>}
      <section className="content"><ErrorBoundary key={page}><Page /></ErrorBoundary></section>
    </main>
    {passwordOpen && <ChangePasswordModal onClose={ok => { setPasswordOpen(false); if (ok) setNotice("Admin password changed successfully."); }} />}
  </div>;
}
