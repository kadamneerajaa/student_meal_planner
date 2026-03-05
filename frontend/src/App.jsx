import { BarChart3, BookOpen, Calendar, Home, ShoppingCart, User } from 'lucide-react';
import { Link, Navigate, Route, BrowserRouter as Router, Routes, useLocation } from 'react-router-dom';
import { MealProvider } from './context/MealContext';
import { PlanProvider } from './context/PlanContext';
import { StudentProvider } from './context/StudentContext';
import CreateMealPage from "./pages/CreateMealPage";
import HomePage from "./pages/HomePage";
import MealLibraryPage from "./pages/MealLibraryPage";
import ProfilePage from "./pages/ProfilePage";
import ReportsPage from "./pages/ReportsPage";
import ShoppingPage from "./pages/ShoppingPage";
import WeeklyPlanPage from "./pages/WeeklyPlanPage";

function Navigation() {
  const location = useLocation();

  const navItems = [
    { path: '/', label: 'Home', icon: Home },
    { path: '/meals', label: 'Meals', icon: BookOpen },
    { path: '/plan', label: 'Plan', icon: Calendar },
    { path: '/shopping', label: 'Shopping', icon: ShoppingCart },
    { path: '/reports', label: 'Reports', icon: BarChart3 },
    { path: '/profile', label: 'Profile', icon: User },
  ];

  return (
    <nav className="nav">
      <div className="nav-container">
        <div className="nav-content">
          <div className="nav-brand">
            <h1 className="nav-title">Student Meal Planner</h1>
          </div>
          <div className="nav-links">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path;
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`nav-link ${isActive ? 'active' : ''}`}
                >
                  <Icon size={20} />
                  <span className="nav-link-text">{item.label}</span>
                </Link>
              );
            })}
          </div>
        </div>
      </div>
    </nav>
  );
}

function AppContent() {
  return (
    <div style={{ minHeight: '100vh', backgroundColor: 'var(--bg-secondary)' }}>
      <Navigation />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/meals" element={<MealLibraryPage />} />
          <Route path="/meals/create" element={<CreateMealPage />} />
          <Route path="/plan" element={<WeeklyPlanPage />} />
          <Route path="/shopping" element={<ShoppingPage />} />
          <Route path="/reports" element={<ReportsPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      <footer className="footer">
        <div className="footer-content">
          <p>&copy; 2024 Student Meal Planner. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}

function App() {
  return (
    <StudentProvider>
      <MealProvider>
        <PlanProvider>
          <Router>
            <AppContent />
          </Router>
        </PlanProvider>
      </MealProvider>
    </StudentProvider>
  );
}

export default App;