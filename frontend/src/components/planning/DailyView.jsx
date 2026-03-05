import { Clock, Coffee, Plus, Sandwich, Utensils, X } from 'lucide-react';
import { calculateDailyTotals } from '../../utils/calculations';
import { formatCurrency } from '../../utils/formatters';
import Card from '../common/Card';
import MealSlot from './MealSlot';

const DailyView = ({ dayPlan, onAddMeal, onRemoveMeal }) => {
  const totals = calculateDailyTotals(dayPlan);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold">{dayPlan.dayName}</h2>
      </div>

      <div className="grid grid-cols-1 grid-md-cols-3">
        <MealSlot
          label="Breakfast"
          meal={dayPlan.breakfast}
          onAdd={() => onAddMeal('breakfast')}
          onRemove={() => onRemoveMeal('breakfast')}
          icon={Coffee}
        />
        <MealSlot
          label="Lunch"
          meal={dayPlan.lunch}
          onAdd={() => onAddMeal('lunch')}
          onRemove={() => onRemoveMeal('lunch')}
          icon={Sandwich}
        />
        <MealSlot
          label="Dinner"
          meal={dayPlan.dinner}
          onAdd={() => onAddMeal('dinner')}
          onRemove={() => onRemoveMeal('dinner')}
          icon={Utensils}
        />
      </div>

      <Card title="Quick Meals & Snacks">
        <div className="grid grid-cols-1 grid-md-cols-2">
          {dayPlan.quickMeals && dayPlan.quickMeals.length > 0 ? (
            dayPlan.quickMeals.map((meal, index) => (
              <div key={index} className="meal-slot">
                <div className="meal-slot-header">
                  <div className="meal-slot-title-wrapper">
                    <Clock size={16} style={{ color: 'var(--color-primary)' }} />
                    <span className="font-semibold">{meal.name}</span>
                  </div>
                  <button
                    onClick={() => onRemoveMeal('quickMeal', index)}
                    className="meal-slot-remove-btn"
                  >
                    <X size={18} />
                  </button>
                </div>
                <div className="text-sm" style={{ color: 'var(--text-secondary)' }}>
                  {meal.calories} cal • {formatCurrency(meal.cost)}
                </div>
              </div>
            ))
          ) : (
            <div style={{ gridColumn: '1 / -1', textAlign: 'center', padding: '1.5rem', color: '#9ca3af' }}>
              No quick meals added
            </div>
          )}
        </div>
        <button
          onClick={() => onAddMeal('quickMeal')}
          className="meal-slot-empty mt-3"
        >
          <Plus size={20} />
          Add Quick Meal
        </button>
      </Card>

      <Card title="Daily Summary">
        <div className="grid grid-cols-2 grid-md-cols-5">
          <div>
            <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>Total Calories</p>
            <p className="text-xl font-bold">{totals.totalCalories}</p>
          </div>
          <div>
            <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>Protein</p>
            <p className="text-xl font-bold">{totals.totalProtein}g</p>
          </div>
          <div>
            <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>Carbs</p>
            <p className="text-xl font-bold">{totals.totalCarbs}g</p>
          </div>
          <div>
            <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>Fat</p>
            <p className="text-xl font-bold">{totals.totalFat}g</p>
          </div>
          <div>
            <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>Cost</p>
            <p className="text-xl font-bold" style={{ color: 'var(--color-success)' }}>
              {formatCurrency(totals.totalCost)}
            </p>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default DailyView;