import ErrorMessage from '../common/ErrorMessage';
import Loader from '../common/Loader';
import MealCard from './MealCard';

const MealList = ({ meals, loading, error, onSelectMeal,  onDeleteMeal, onRetry }) => {
  if (loading) {
    return <Loader text="Loading meals..." />;
  }

  if (error) {
    return <ErrorMessage message={error} onRetry={onRetry} />;
  }

  if (!meals || meals.length === 0) {
    return (
      <div className="text-center" style={{ padding: '3rem 0' }}>
        <p style={{ fontSize: '1.125rem', color: '#6b7280' }}>No meals found</p>
        <p style={{ color: '#9ca3af', marginTop: '0.5rem' }}>
          Try adjusting your filters or add a new meal
        </p>
      </div>
    );
  }

  return (
    <div className="meals-grid">
      {meals.map((meal) => (
        <MealCard
          key={meal.id}
          meal={meal}
          onSelect={onSelectMeal}
          onDelete={onDeleteMeal}
        />
      ))}
    </div>
  );
};

export default MealList;