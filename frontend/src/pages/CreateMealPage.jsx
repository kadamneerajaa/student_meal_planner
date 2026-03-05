import { useNavigate } from 'react-router-dom';
import Card from '../components/common/Card';
import MealForm from '../components/meals/MealForm';
import { useMeals } from '../context/MealContext';

const CreateMealPage = () => {
  const navigate = useNavigate();
  const { addMeal } = useMeals();

  const handleSubmit = async (mealData) => {
    try {
      await addMeal(mealData);
      navigate('/meals');
    } catch (err) {
      console.error('Error creating meal:', err);
    }
  };

  const handleCancel = () => {
    navigate('/meals');
  };

  return (
    <div style={{ maxWidth: '48rem', margin: '0 auto' }}>
      <h1 className="text-3xl font-bold mb-4">Create New Meal</h1>
      <Card>
        <MealForm onSubmit={handleSubmit} onCancel={handleCancel} />
      </Card>
    </div>
  );
};

export default CreateMealPage;