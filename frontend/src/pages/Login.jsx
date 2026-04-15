import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { BarChart3, Eye, EyeOff, Loader2, Key } from 'lucide-react';
import axios from 'axios';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [devMode, setDevMode] = useState(false);
  const [manualToken, setManualToken] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  /** Decode a JWT token and extract user info */
  const decodeAndLogin = (token) => {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const userData = {
        userId: payload.userId || 'UNKNOWN',
        name: payload.name || payload.sub || 'User',
        email: payload.sub || '',
        role: payload.role || 'ADMIN',
      };
      login(token, userData);
      navigate('/');
    } catch {
      setError('Invalid token format. Make sure you paste a valid JWT.');
    }
  };

  /** Normal login via IAM / API Gateway */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // Proxied through Vite → API Gateway → IAM service
      const res = await axios.post('/auth/login', { email, password });
      decodeAndLogin(res.data.token);
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Invalid email or password');
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError('Login failed. Make sure the API Gateway (port 8080) and IAM service are running.');
      }
    } finally {
      setLoading(false);
    }
  };

  /** Dev-mode: paste a JWT token directly */
  const handleTokenLogin = (e) => {
    e.preventDefault();
    setError('');
    if (!manualToken.trim()) {
      setError('Please paste a JWT token.');
      return;
    }
    decodeAndLogin(manualToken.trim());
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-header">
          <div className="login-logo">
            <BarChart3 size={40} />
          </div>
          <h1>BuildSmart</h1>
          <p>Reporting & Analytics Portal</p>
        </div>

        {error && <div className="login-error">{error}</div>}

        {/* Toggle between normal login and dev-mode token */}
        <div className="login-mode-toggle">
          <button
            type="button"
            className={`mode-btn ${!devMode ? 'active' : ''}`}
            onClick={() => { setDevMode(false); setError(''); }}
          >
            Credentials
          </button>
          <button
            type="button"
            className={`mode-btn ${devMode ? 'active' : ''}`}
            onClick={() => { setDevMode(true); setError(''); }}
          >
            <Key size={14} /> JWT Token
          </button>
        </div>

        {!devMode ? (
          /* Normal login form */
          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="email">Email Address</label>
              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="admin@buildsmart.com"
                required
                autoFocus
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <div className="password-wrapper">
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter your password"
                  required
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            <button type="submit" className="btn btn-primary login-btn" disabled={loading}>
              {loading ? (
                <>
                  <Loader2 size={18} className="spin" />
                  Signing in...
                </>
              ) : (
                'Sign In'
              )}
            </button>
          </form>
        ) : (
          /* Dev-mode: paste JWT token */
          <form onSubmit={handleTokenLogin} className="login-form">
            <div className="form-group">
              <label htmlFor="token">JWT Token</label>
              <textarea
                id="token"
                rows={5}
                value={manualToken}
                onChange={(e) => setManualToken(e.target.value)}
                placeholder="Paste your JWT token from the IAM service here..."
                required
                style={{ fontFamily: 'monospace', fontSize: '0.8rem' }}
              />
            </div>
            <p className="hint-text">
              Get a token from IAM: <code>POST http://localhost:8080/api/auth/login</code>
            </p>
            <button type="submit" className="btn btn-primary login-btn">
              <Key size={18} />
              Login with Token
            </button>
          </form>
        )}

        <div className="login-footer">
          <p>Authorized personnel only</p>
        </div>
      </div>
    </div>
  );
}
