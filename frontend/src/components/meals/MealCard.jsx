import { Trash2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useMeals } from '../../context/MealContext';
import { formatCurrency } from '../../utils/formatters';
import Card from '../common/Card';

const MealCard = ({ meal, onSelect, onDelete }) => {
  const navigate = useNavigate();
  const { toggleFavorite } = useMeals();

  const handleDeleteClick = async (e) => {
    e.stopPropagation();
    if (onDelete) {
      onDelete(meal.id);
    }
  };

  const handleCardClick = () => {
    if (onSelect) {
      onSelect(meal);
    } else {
      navigate(`/meals/${meal.id}`);
    }
  };

  return (
    <Card className="meal-card" onClick={handleCardClick}>
      <div className="meal-card-header">
        <div>
          <h3 className="meal-card-title">{meal.name}</h3>
          <div className="meal-card-meta">
            <span className="meal-type-badge">{meal.mealType || 'REGULAR'}</span>
            {meal.prepTimeCategory && (
              <span className="meal-prep-time">⏱️ {meal.prepTimeCategory > 0 ? `${meal.prepTimeCategory} min` : ''} min </span>
            )}
          </div>
        </div>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          
          {onDelete && (
            <button
              onClick={handleDeleteClick}
              style={{
                background: 'none',
                border: 'none',
                cursor: 'pointer',
                padding: '0.5rem',
                color: '#ef4444'
              }}
              title="Delete meal"
            >
              <Trash2 size={20} />
            </button>
          )}
        </div>
      </div>

      <div className="meal-card-nutrition">
        <div className="nutrition-item">
          <span className="nutrition-label">Calories</span>
          <span className="nutrition-value">{meal.calories}</span>
        </div>
        <div className="nutrition-item">
          <span className="nutrition-label">Protein</span>
          <span className="nutrition-value">{meal.protein}g</span>
        </div>
        <div className="nutrition-item">
          <span className="nutrition-label">Carbs</span>
          <span className="nutrition-value">{meal.carbs}g</span>
        </div>
        <div className="nutrition-item">
          <span className="nutrition-label">Fat</span>
          <span className="nutrition-value">{meal.fat}g</span>
        </div>
      </div>

      <div className="meal-card-footer">
        <span className="meal-cost">{formatCurrency(meal.cost)}</span>
        {onSelect && (
          <button className="btn btn-primary btn-sm">Select</button>
        )}
      </div>
    </Card>
  );
};

export default MealCard;