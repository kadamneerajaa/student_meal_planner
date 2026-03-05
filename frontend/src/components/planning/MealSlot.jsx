import { Plus, X } from 'lucide-react';
import { formatCurrency } from '../../utils/formatters';

const MealSlot = ({ label, meal, onAdd, onRemove, icon: Icon }) => {
  return (
    <div className="meal-slot">
      <div className="meal-slot-header">
        <div className="meal-slot-title-wrapper">
          {Icon && <Icon size={20} style={{ color: 'var(--color-primary)' }} />}
          <h4 className="meal-slot-title">{label}</h4>
        </div>
        {meal && (
          <button onClick={onRemove} className="meal-slot-remove-btn">
            <X size={18} />
          </button>
        )}
      </div>

      {meal ? (
        <div className="meal-slot-content">
          <p className="meal-slot-meal-name">{meal.name}</p>
          <div className="meal-slot-macros">
            <div>
              <span className="font-medium">Calories:</span> {meal.calories}
            </div>
            <div>
              <span className="font-medium">Protein:</span> {meal.protein}g
            </div>
            <div>
              <span className="font-medium">Carbs:</span> {meal.carbs}g
            </div>
            <div>
              <span className="font-medium">Fat:</span> {meal.fat}g
            </div>
          </div>
          <p className="font-semibold mt-2" style={{ color: 'var(--color-success)' }}>
            {formatCurrency(meal.cost)}
          </p>
        </div>
      ) : (
        <button 
          onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            console.log('Add Meal button clicked for:', label);
            if (onAdd) {
              onAdd();
            } else {
              console.error('onAdd function is not provided');
            }
          }} 
          className="meal-slot-empty"
          style={{ 
            cursor: 'pointer',
            width: '100%',
            padding: '20px',
            border: '2px dashed #d1d5db',
            borderRadius: '8px',
            background: 'transparent',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '8px',
            color: '#6b7280',
            transition: 'all 0.2s'
          }}
          onMouseOver={(e) => e.currentTarget.style.borderColor = '#3b82f6'}
          onMouseOut={(e) => e.currentTarget.style.borderColor = '#d1d5db'}
        >
          <Plus size={20} />
          <span>Add Meal</span>
        </button>
      )}
    </div>
  );
};

export default MealSlot;