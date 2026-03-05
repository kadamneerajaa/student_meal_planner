import { BarChart3, BookOpen, Calendar, ShoppingCart } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from '../components/common/Card';
import { usePlan } from '../context/PlanContext';
import { useStudent } from '../context/StudentContext';
import { calculateWeeklyTotals } from '../utils/calculations';
import { formatCurrency } from '../utils/formatters';

const HomePage = () => {
  const navigate = useNavigate();
  const { student } = useStudent();
  const { weeklyPlan } = usePlan();
  const [weeklyTotals, setWeeklyTotals] = useState(null);

  useEffect(() => {
    if (weeklyPlan) {
      const totals = calculateWeeklyTotals(weeklyPlan);
      setWeeklyTotals(totals);
    }
  }, [weeklyPlan]);

  const quickActions = [
    {
      title: 'Meal Library',
      description: 'Browse and manage meals',
      icon: BookOpen,
      color: '#3b82f6',
      path: '/meals',
    },
    {
      title: 'Weekly Plan',
      description: 'Build your weekly meal plan',
      icon: Calendar,
      color: '#10b981',
      path: '/plan',
    },
    {
      title: 'Shopping List',
      description: 'View shopping list & pantry',
      icon: ShoppingCart,
      color: '#8b5cf6',
      path: '/shopping',
    },
    {
      title: 'Reports',
      description: 'View nutrition reports',
      icon: BarChart3,
      color: '#f59e0b',
      path: '/reports',
    },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div className="hero-section">
        <h1 className="hero-title">
          Welcome back, {student?.name || 'Student'}!
        </h1>
        <p className="hero-subtitle">
          Let's plan some delicious and healthy meals for this week.
        </p>
      </div>

      {weeklyTotals && (
        <div className="stats-grid">
          <Card>
            <div className="text-center">
              <p className="text-sm mb-1" style={{ color: 'var(--text-secondary)' }}>Weekly Calories</p>
              <p className="text-3xl font-bold" style={{ color: 'var(--color-primary)' }}>
                {weeklyTotals.weeklyCalories}
              </p>
            </div>
          </Card>
          <Card>
            <div className="text-center">
              <p className="text-sm mb-1" style={{ color: 'var(--text-secondary)' }}>Weekly Protein</p>
              <p className="text-3xl font-bold" style={{ color: 'var(--color-success)' }}>
                {weeklyTotals.weeklyProtein}g
              </p>
            </div>
          </Card>
          <Card>
            <div className="text-center">
              <p className="text-sm mb-1" style={{ color: 'var(--text-secondary)' }}>Weekly Cost</p>
              <p className="text-3xl font-bold" style={{ color: '#8b5cf6' }}>
                {formatCurrency(weeklyTotals.weeklyCost)}
              </p>
            </div>
          </Card>
          <Card>
            <div className="text-center">
              <p className="text-sm mb-1" style={{ color: 'var(--text-secondary)' }}>Budget Remaining</p>
              <p className="text-3xl font-bold" style={{ color: '#f59e0b' }}>
                {formatCurrency((student?.weeklyBudget || 0) - weeklyTotals.weeklyCost)}
              </p>
            </div>
          </Card>
        </div>
      )}

      <div>
        <h2 className="text-2xl font-bold mb-3">Quick Actions</h2>
        <div className="quick-actions-grid">
          {quickActions.map((action) => (
            <Card key={action.title} onClick={() => navigate(action.path)}>
              <div className="quick-action-card">
                <div className="quick-action-icon" style={{ backgroundColor: action.color }}>
                  <action.icon size={32} style={{ color: 'white' }} />
                </div>
                <h3 className="quick-action-title">{action.title}</h3>
                <p className="quick-action-description">{action.description}</p>
              </div>
            </Card>
          ))}
        </div>
      </div>

      <Card title="Your Goals">
        <div className="goals-section">
          <div>
            <h4 className="font-semibold mb-3">Daily Nutrition Goals</h4>
            <div className="goals-list">
              <div className="goals-list-item">
                <span style={{ color: 'var(--text-secondary)' }}>Calories:</span>
                <span className="font-semibold">{student?.dailyCalorieGoal || 2000}</span>
              </div>
              <div className="goals-list-item">
                <span style={{ color: 'var(--text-secondary)' }}>Protein:</span>
                <span className="font-semibold">{student?.proteinGoal || 0}g</span>
              </div>
              <div className="goals-list-item">
                <span style={{ color: 'var(--text-secondary)' }}>Carbs:</span>
                <span className="font-semibold">{student?.carbsGoal || 0}g</span>
              </div>
              <div className="goals-list-item">
                <span style={{ color: 'var(--text-secondary)' }}>Fat:</span>
                <span className="font-semibold">{student?.fatsGoal || 0}g</span>
              </div>
            </div>
          </div>
          <div>
            <h4 className="font-semibold mb-3">Budget</h4>
            <div className="goals-list">
              <div className="goals-list-item">
                <span style={{ color: 'var(--text-secondary)' }}>Weekly Budget:</span>
                <span className="font-semibold">
                  {formatCurrency(student?.weeklyBudget || 0)}
                </span>
              </div>
              <div className="goals-list-item">
                <span style={{ color: 'var(--text-secondary)' }}>Dietary Preference:</span>
                <span className="font-semibold" style={{ textTransform: 'capitalize' }}>
                  {student?.dietaryPreference || 'None'}
                </span>
              </div>
            </div>
            <button
              onClick={() => navigate('/profile')}
              className="btn btn-primary w-full mt-3"
            >
              Update Goals
            </button>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default HomePage;