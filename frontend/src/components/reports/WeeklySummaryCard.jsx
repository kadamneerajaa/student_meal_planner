import { formatCurrency, formatNumber } from '../../utils/formatters';
import Card from '../common/Card';

const WeeklySummaryCard = ({ summary, student }) => {
  const avgCalories = summary.weeklyTotalCalories / 7;
  const weeklyBudget = student?.weeklyBudget || 0;
  const budgetUsedPercent = (summary.weeklyTotalCost / weeklyBudget) * 100;

  return (
    <Card title="Weekly Summary">
      <div className="weekly-summary-section">
        <div className="weekly-summary-totals">
          <div className="summary-stat-box" style={{ backgroundColor: '#dbeafe', textAlign: 'center' }}>
            <p className="summary-stat-label">Total Calories</p>
            <p className="summary-stat-value" style={{ color: 'var(--color-primary)' }}>
              {summary.weeklyTotalCalories}
            </p>
            <p className="summary-stat-goal">
              Avg: {formatNumber(avgCalories)}
            </p>
          </div>

          <div className="summary-stat-box" style={{ backgroundColor: '#d1fae5', textAlign: 'center' }}>
            <p className="summary-stat-label">Total Protein</p>
            <p className="summary-stat-value" style={{ color: 'var(--color-success)' }}>
              {summary.weeklyTotalProtein}g
            </p>
          </div>

          <div className="summary-stat-box" style={{ backgroundColor: '#f3e8ff', textAlign: 'center' }}>
            <p className="summary-stat-label">Total Carbs</p>
            <p className="summary-stat-value" style={{ color: '#8b5cf6' }}>
              {summary.weeklyTotalCarbs}g
            </p>
          </div>

          <div className="summary-stat-box" style={{ backgroundColor: '#fef3c7', textAlign: 'center' }}>
            <p className="summary-stat-label">Total Fat</p>
            <p className="summary-stat-value" style={{ color: '#f59e0b' }}>
              {summary.weeklyTotalFat}g
            </p>
          </div>
        </div>

        <div className="weekly-summary-budget">
          <div className="weekly-summary-budget-header">
            <span className="font-semibold">Weekly Budget</span>
            <span className="text-2xl font-bold" style={{ color: 'var(--color-success)' }}>
              {formatCurrency(summary.weeklyTotalCost)}
            </span>
          </div>
          <div className="weekly-summary-budget-info">
            <span>Budget: {formatCurrency(weeklyBudget)}</span>
            <span>
              {summary.budgetMet ? (
                <span className="font-medium" style={{ color: 'var(--color-success)' }}>✓ Under Budget</span>
              ) : (
                <span className="font-medium" style={{ color: 'var(--color-danger)' }}>✗ Over Budget</span>
              )}
            </span>
          </div>
          <div className="budget-progress-bar-container">
            <div
              className="goal-progress-bar-fill"
              style={{
                width: `${Math.min(budgetUsedPercent, 100)}%`,
                backgroundColor: budgetUsedPercent > 100 ? 'var(--color-danger)' : 'var(--color-success)'
              }}
            ></div>
          </div>
          <p className="text-xs text-right mt-1" style={{ color: '#6b7280' }}>
            {budgetUsedPercent.toFixed(1)}% used
          </p>
        </div>

        <div className="weekly-summary-goals">
          <h4 className="font-semibold mb-3">Goals Achievement</h4>
          <div className="weekly-summary-goal-item">
            <span className="text-sm">Weekly Goals Met</span>
            <span className={`font-medium ${summary.weeklyGoalsMet ? 'text-success' : 'text-warning'}`}>
              {summary.weeklyGoalsMet ? '✓ Yes' : '✗ Not Yet'}
            </span>
          </div>
          <div className="weekly-summary-goal-item">
            <span className="text-sm">Budget Goal</span>
            <span className={`font-medium ${summary.budgetMet ? 'text-success' : 'text-danger'}`}>
              {summary.budgetMet ? '✓ Met' : '✗ Exceeded'}
            </span>
          </div>
        </div>

        {summary.ingredientUsageSummary && Object.keys(summary.ingredientUsageSummary).length > 0 && (
          <div className="mt-4">
            <h4 className="font-semibold mb-3">Top Ingredients Used</h4>
            <div className="ingredient-list">
              {Object.entries(summary.ingredientUsageSummary)
                .sort((a, b) => b[1] - a[1])
                .slice(0, 5)
                .map(([ingredient, count]) => (
                  <div key={ingredient} className="ingredient-list-item">
                    <span className="text-sm" style={{ textTransform: 'capitalize' }}>{ingredient}</span>
                    <span className="text-sm font-semibold">{count} times</span>
                  </div>
                ))}
            </div>
          </div>
        )}
      </div>
    </Card>
  );
};

export default WeeklySummaryCard;