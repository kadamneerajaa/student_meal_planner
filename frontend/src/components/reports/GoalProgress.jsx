import { checkGoalAchievement } from '../../utils/calculations';
import Card from '../common/Card';

const GoalProgress = ({ summary, student }) => {
  const goals = [
    {
      name: 'Daily Calories',
      actual: summary.totalCalories,
      target: student?.dailyCalorieGoal || 2000,
      unit: 'cal',
    },
    {
      name: 'Protein',
      actual: summary.totalProtein,
      target: student?.proteinGoal || 0,
      unit: 'g',
    },
    {
      name: 'Carbs',
      actual: summary.totalCarbs,
      target: student?.carbsGoal || 0,
      unit: 'g',
    },
    {
      name: 'Fat',
      actual: summary.totalFat,
      target: student?.fatsGoal || 0,
      unit: 'g',
    },
  ];

  return (
    <Card title="Goal Progress">
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        {goals.map((goal) => {
          const achievement = checkGoalAchievement(goal.actual, goal.target);
          
          return (
            <div key={goal.name} className="goal-progress-item">
              <div className="goal-progress-header">
                <span className="font-medium">{goal.name}</span>
                <span className="text-sm" style={{ color: 'var(--text-secondary)' }}>
                  {goal.actual} / {goal.target} {goal.unit}
                </span>
              </div>
              <div className="goal-progress-bar-container">
                <div
                  className="goal-progress-bar-fill"
                  style={{
                    width: `${Math.min(achievement.percentage, 100)}%`,
                    backgroundColor: achievement.achieved
                      ? 'var(--color-success)'
                      : achievement.percentage > 100
                      ? 'var(--color-danger)'
                      : 'var(--color-warning)'
                  }}
                ></div>
              </div>
              <div className="goal-progress-footer">
                <span className="text-xs" style={{ color: '#9ca3af' }}>
                  {achievement.percentage.toFixed(1)}%
                </span>
                {achievement.achieved ? (
                  <span className="text-xs font-medium" style={{ color: 'var(--color-success)' }}>
                    ✓ Goal Met
                  </span>
                ) : (
                  <span className="text-xs" style={{ color: '#9ca3af' }}>
                    {achievement.difference > 0 ? '+' : ''}
                    {achievement.difference} {goal.unit}
                  </span>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </Card>
  );
};

export default GoalProgress;