import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Layout from './components/Layout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import ProjectHealth from './pages/ProjectHealth';
import SafetyCompliance from './pages/SafetyCompliance';
import FinancialBudget from './pages/FinancialBudget';
import ResourceWorkforce from './pages/ResourceWorkforce';
import ReportHistory from './pages/ReportHistory';
import GenerateReport from './pages/GenerateReport';
import VendorAnalytics from './pages/VendorAnalytics';
import SiteEngineerAnalytics from './pages/SiteEngineerAnalytics';
import UserAnalytics from './pages/UserAnalytics';

function ProtectedRoute({ children }) {
  const { token } = useAuth();
  return token ? children : <Navigate to="/login" />;
}

export default function App() {
  const { token } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={token ? <Navigate to="/" /> : <Login />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="project-health" element={<ProjectHealth />} />
        <Route path="safety-compliance" element={<SafetyCompliance />} />
        <Route path="financial-budget" element={<FinancialBudget />} />
        <Route path="resource-workforce" element={<ResourceWorkforce />} />
        <Route path="vendor-analytics" element={<VendorAnalytics />} />
        <Route path="site-engineer" element={<SiteEngineerAnalytics />} />
        <Route path="user-analytics" element={<UserAnalytics />} />
        <Route path="report-history" element={<ReportHistory />} />
        <Route path="generate-report" element={<GenerateReport />} />
      </Route>
    </Routes>
  );
}
