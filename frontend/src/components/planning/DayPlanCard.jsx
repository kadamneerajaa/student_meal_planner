import { Calendar, Coffee, Sandwich, Utensils } from 'lucide-react';
import { calculateDailyTotals } from '../../utils/calculations';
import { formatCurrency } from '../../utils/formatters';

const DayPlanCard = ({ dayPlan, onClick }) => {
  const totals = calculateDailyTotals(dayPlan);

  return (
    <div className="day-plan-card" onClick={onClick}>
      <div className="day-plan-card-header">
        <Calendar size={20} style={{ color: 'var(--color-primary)' }} />
        <h3 className="day-plan-card-title">{dayPlan.dayName}</h3>
      </div>

      <div className="day-plan-card-meals">
        <div className="day-plan-card-meal-item">
          <Coffee size={16} style={{ color: '#f97316' }} />
          <span>{dayPlan.breakfast ? dayPlan.breakfast.name : 'No breakfast'}</span>
        </div>

        <div className="day-plan-card-meal-item">
          <Sandwich size={16} style={{ color: '#10b981' }} />
          <span>{dayPlan.lunch ? dayPlan.lunch.name : 'No lunch'}</span>
        </div>

        <div className="day-plan-card-meal-item">
          <Utensils size={16} style={{ color: '#8b5cf6' }} />
          <span>{dayPlan.dinner ? dayPlan.dinner.name : 'No dinner'}</span>
        </div>

        {dayPlan.quickMeals && dayPlan.quickMeals.length > 0 && (
          <div className="day-plan-card-meal-item">
            <span>+ {dayPlan.quickMeals.length} quick meal(s)</span>
          </div>
        )}
      </div>

      <div className="day-plan-card-summary">
        <div className="day-plan-card-summary-item">
          <span style={{ color: 'var(--text-secondary)' }}>Calories:</span>
          <span className="font-semibold">{totals.totalCalories}</span>
        </div>
        <div className="day-plan-card-summary-item">
          <span style={{ color: 'var(--text-secondary)' }}>Cost:</span>
          <span className="font-semibold" style={{ color: 'var(--color-success)' }}>
            {formatCurrency(totals.totalCost)}
          </span>
        </div>
      </div>
    </div>
  );
};

export default DayPlanCard;