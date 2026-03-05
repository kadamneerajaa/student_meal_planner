import { checkGoalAchievement } from '../../utils/calculations';
import { formatCurrency } from '../../utils/formatters';
import Card from '../common/Card';

const DailySummaryCard = ({ summary, student }) => {
  const calorieGoal = checkGoalAchievement(
    summary.totalCalories,
    student?.dailyCalorieGoal || 2000
  );

  return (
    <Card title={`${summary.dayPlan?.dayName} Summary`}>
      <div className="summary-card-stats">
        <div className="summary-stat-box" style={{ backgroundColor: '#dbeafe' }}>
          <p className="summary-stat-label">Calories</p>
          <p className="summary-stat-value" style={{ color: 'var(--color-primary)' }}>
            {summary.totalCalories}
          </p>
          <p className="summary-stat-goal">
            Goal: {student?.dailyCalorieGoal || 2000}
          </p>
          <div className="summary-stat-status">
            {calorieGoal.achieved ? (
              <span style={{ color: 'var(--color-success)' }}>✓ Goal Met</span>
            ) : (
              <span style={{ color: '#f97316' }}>
                {calorieGoal.percentage.toFixed(0)}% of goal
              </span>
            )}
          </div>
        </div>

        <div className="summary-stat-box" style={{ backgroundColor: '#d1fae5' }}>
          <p className="summary-stat-label">Protein</p>
          <p className="summary-stat-value" style={{ color: 'var(--color-success)' }}>
            {summary.totalProtein}g
          </p>
          <p className="summary-stat-goal">
            Goal: {student?.proteinGoal || 0}g
          </p>
        </div>

        <div className="summary-stat-box" style={{ backgroundColor: '#f3e8ff' }}>
          <p className="summary-stat-label">Carbs</p>
          <p className="summary-stat-value" style={{ color: '#8b5cf6' }}>
            {summary.totalCarbs}g
          </p>
          <p className="summary-stat-goal">
            Goal: {student?.carbsGoal || 0}g
          </p>
        </div>

        <div className="summary-stat-box" style={{ backgroundColor: '#fef3c7' }}>
          <p className="summary-stat-label">Fat</p>
          <p className="summary-stat-value" style={{ color: '#f59e0b' }}>
            {summary.totalFat}g
          </p>
          <p className="summary-stat-goal">
            Goal: {student?.fatsGoal || 0}g
          </p>
        </div>

        <div className="summary-stat-box" style={{ backgroundColor: '#fce7f3' }}>
          <p className="summary-stat-label">Daily Cost</p>
          <p className="summary-stat-value" style={{ color: '#ec4899' }}>
            {formatCurrency(summary.totalCost)}
          </p>
        </div>

        <div className="summary-stat-box" style={{ backgroundColor: '#e0e7ff' }}>
          <p className="summary-stat-label">Macro Goals</p>
          <p className="text-sm font-semibold mt-2">
            {summary.macroGoalsMet ? (
              <span style={{ color: 'var(--color-success)' }}>✓ Met</span>
            ) : (
              <span style={{ color: '#f97316' }}>Not Met</span>
            )}
          </p>
          <p className="text-sm font-semibold">
            Calories: {summary.calorieGoalMet ? (
              <span style={{ color: 'var(--color-success)' }}>✓</span>
            ) : (
              <span style={{ color: '#f97316' }}>✗</span>
            )}
          </p>
        </div>
      </div>
    </Card>
  );
};

export default DailySummaryCard;