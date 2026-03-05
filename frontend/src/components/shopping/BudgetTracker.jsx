import { checkBudget } from '../../utils/calculations';
import { formatCurrency } from '../../utils/formatters';
import Card from '../common/Card';

const BudgetTracker = ({ weeklyBudget, currentSpending }) => {
  const budget = checkBudget(currentSpending, weeklyBudget);

  return (
    <Card title="Budget Tracker">
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <div className="budget-tracker-item">
          <span style={{ color: 'var(--text-secondary)' }}>Weekly Budget:</span>
          <span className="text-xl font-bold">{formatCurrency(weeklyBudget)}</span>
        </div>

        <div className="budget-tracker-item">
          <span style={{ color: 'var(--text-secondary)' }}>Current Spending:</span>
          <span className="text-xl font-bold" style={{ color: 'var(--color-primary)' }}>
            {formatCurrency(currentSpending)}
          </span>
        </div>

        <div className="budget-tracker-item">
          <span style={{ color: 'var(--text-secondary)' }}>Remaining:</span>
          <span
            className="text-xl font-bold"
            style={{ color: budget.remaining >= 0 ? 'var(--color-success)' : 'var(--color-danger)' }}
          >
            {formatCurrency(budget.remaining)}
          </span>
        </div>

        <div className="mt-2">
          <div className="flex justify-between text-sm mb-2">
            <span style={{ color: 'var(--text-secondary)' }}>Progress</span>
            <span className="font-semibold">{budget.percentageUsed.toFixed(1)}%</span>
          </div>
          <div className="budget-progress-bar">
            <div
              className="budget-progress-fill"
              style={{
                width: `${Math.min(budget.percentageUsed, 100)}%`,
                backgroundColor: 
                  budget.percentageUsed > 100
                    ? 'var(--color-danger)'
                    : budget.percentageUsed > 80
                    ? 'var(--color-warning)'
                    : 'var(--color-success)'
              }}
            ></div>
          </div>
        </div>

        {budget.percentageUsed > 100 && (
          <div className="budget-warning budget-warning-over">
            You're over budget by {formatCurrency(Math.abs(budget.remaining))}
          </div>
        )}

        {budget.percentageUsed > 80 && budget.percentageUsed <= 100 && (
          <div className="budget-warning budget-warning-near">
            You're approaching your budget limit
          </div>
        )}
      </div>
    </Card>
  );
};

export default BudgetTracker;