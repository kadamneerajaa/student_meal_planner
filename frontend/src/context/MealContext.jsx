import { createContext, useContext, useEffect, useState } from 'react';
import { createMeal, fetchMeals, updateMeal } from '../utils/api';

const MealContext = createContext();

export const useMeals = () => {
  const context = useContext(MealContext);
  if (!context) {
    throw new Error('useMeals must be used within MealProvider');
  }
  return context;
};

export const MealProvider = ({ children }) => {
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
      console.log('MealContext: Loaded meals:', data);
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
      console.log('MealContext: Adding meal:', mealData);
      const newMeal = await createMeal(mealData);
      console.log('MealContext: Meal created:', newMeal);
      
      // Update local state immediately with backend response
      setMeals(prevMeals => [...prevMeals, newMeal]);
      
      return newMeal;
    } catch (err) {
      console.error('MealContext: Error adding meal:', err);
      throw err;
    }
  };

  const editMeal = async (mealId, mealData) => {
    try {
      console.log('MealContext: Updating meal:', mealId, mealData);
      const updatedMeal = await updateMeal(mealId, mealData);
      console.log('MealContext: Meal updated:', updatedMeal);
      
      // Update local state immediately with backend response
      setMeals(prevMeals => 
        prevMeals.map(m => m.id === mealId ? updatedMeal : m)
      );
      
      return updatedMeal;
    } catch (err) {
      console.error('MealContext: Error updating meal:', err);
      throw err;
    }
  };

  const value = {
    meals,
    loading,
    error,
    addMeal,
    editMeal,
    refreshMeals: loadMeals,
  };

  return <MealContext.Provider value={value}>{children}</MealContext.Provider>;
};