import { useEffect, useState } from 'react';
import { createMeal, deleteMeal, fetchMeals, updateMeal } from '../utils/api';

const useMeals = () => {
  const [meals, setMeals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadMeals();
  }, []);

  const loadMeals = async () => {
    try {
      setLoading(true);
      const data = await fetchMeals();
      setMeals(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const addMeal = async (mealData) => {
    try {
      const newMeal = await createMeal(mealData);
      setMeals((prev) => [...prev, newMeal]);
      return newMeal;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const editMeal = async (id, mealData) => {
    try {
      const updated = await updateMeal(id, mealData);
      setMeals((prev) => prev.map((meal) => (meal.id === id ? updated : meal)));
      return updated;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const removeMeal = async (id) => {
    try {
      await deleteMeal(id);
      setMeals((prev) => prev.filter((meal) => meal.id !== id));
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  return {
    meals,
    loading,
    error,
    addMeal,
    editMeal,
    removeMeal,
    refreshMeals: loadMeals,
  };
};

export default useMeals;