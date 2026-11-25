import React, { useState } from "react";
import api, { API_URL } from "../api";
import { LockKeyhole, Mail, ArrowRight } from "lucide-react";
import { ErrorBox } from "../components/UI";

export default function Login({ onLogin }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fillDemoAdmin = () => {
    setEmail("admin@garejx.com");
    setPassword("password@100");
  };

  async function submit(e) {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const r = await api.post("/auth/login", { email, password });
      if (r.data.user.role !== "ADMIN") {
        throw new Error("This panel is restricted to ADMIN accounts.");
      }
      localStorage.setItem("garejx_token", r.data.token);
      localStorage.setItem("garejx_user", JSON.stringify(r.data.user));
      onLogin(r.data.user);
    } catch (e) {
      if (e.message === "Network Error" || !e.response) {
        setError(`Network Error: Unable to reach backend API at ${API_URL}. Please ensure your network has access to the cloud server.`);
      } else {
        setError(e.response?.data?.message || e.message || "Login failed");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-visual">
        <div className="login-glow" />
        <div className="login-brand">
          <div className="brand-mark big">GX</div>
          <div>
            <b>GarejX</b>
            <span>Garage Management System</span>
          </div>
        </div>
        <div className="login-copy">
          <span className="eyebrow">ADMIN OPERATIONS</span>
          <h1>
            Run your garage
            <br />
            <em>smarter.</em>
          </h1>
          <p>One clean command center for customers, mechanics, vehicles, appointments and payments.</p>
          <div className="feature-row">
            <span>● Live operations</span>
            <span>● Secure API</span>
            <span>● Full control</span>
          </div>
        </div>
      </div>
      <div className="login-card">
        <div className="mobile-logo">
          <div className="brand-mark">GX</div>
          <b>GarejX</b>
        </div>
        <span className="eyebrow">WELCOME BACK</span>
        <h2>Admin sign in</h2>
        <p className="muted">Use your GarejX ADMIN account to continue.</p>

        <div style={{ background: "#F6EFE9", padding: "10px 14px", borderRadius: "8px", marginBottom: "16px", fontSize: "12px", border: "1px solid #E5D6C8", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <div>
            <strong style={{ color: "#6B1D2F", display: "block" }}>Demo Admin Credentials:</strong>
            <span style={{ color: "#555" }}>admin@garejx.com / password@100</span>
          </div>
          <button type="button" onClick={fillDemoAdmin} style={{ background: "#6B1D2F", color: "#fff", border: "none", padding: "4px 8px", borderRadius: "4px", cursor: "pointer", fontSize: "11px" }}>
            Auto Fill
          </button>
        </div>

        <form onSubmit={submit}>
          <label className="field">
            <span>Email</span>
            <div className="input-icon">
              <Mail size={18} />
              <input
                required
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="admin@garejx.com"
              />
            </div>
          </label>
          <label className="field">
            <span>Password</span>
            <div className="input-icon">
              <LockKeyhole size={18} />
              <input
                required
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
              />
            </div>
          </label>
          <ErrorBox error={error} />
          <button className="primary full" disabled={loading}>
            {loading ? "Signing in…" : <>Sign in <ArrowRight size={18} /></>}
          </button>
        </form>
        <small className="login-foot">Protected by your live GarejX backend authentication.</small>
      </div>
    </div>
  );
}
