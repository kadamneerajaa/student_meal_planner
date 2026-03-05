import { createContext, useContext, useEffect, useState } from 'react';
import { fetchDayPlan, fetchWeeklyPlan, updateDayPlan, updateWeeklyPlan } from '../utils/api';

const PlanContext = createContext();

export const usePlan = () => {
  const context = useContext(PlanContext);
  if (!context) {
    throw new Error('usePlan must be used within PlanProvider');
  }
  return context;
};

export const PlanProvider = ({ children }) => {
  const [weeklyPlan, setWeeklyPlan] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadWeeklyPlan();
  }, []);

  const loadWeeklyPlan = async () => {
    try {
      setLoading(true);
      console.log('Loading weekly plan...');
      
      const data = await fetchWeeklyPlan();
      console.log('API response for weekly plan:', data);
      
      if (!data || data.error) {
        console.log('API returned error or empty, using mock data');
        const mockWeeklyPlan = createMockWeeklyPlan();
        setWeeklyPlan(mockWeeklyPlan);
      } else {
        setWeeklyPlan(data);
      }
      setError(null);
    } catch (err) {
      console.error('Error loading weekly plan from API, using mock data:', err.message);
      const mockWeeklyPlan = createMockWeeklyPlan();
      setWeeklyPlan(mockWeeklyPlan);
      setError(null);
    } finally {
      setLoading(false);
    }
  };

  const createMockWeeklyPlan = () => {
    const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    return {
      studentId: "S001",
      studentName: "Test Student",
      days: days.map(day => ({
        dayName: day,
        breakfast: null,
        lunch: null,
        dinner: null,
        quickMeals: []
      }))
    };
  };

  const savePlan = async (planData) => {
  try {
    console.log('=== savePlan START ===');
    console.log('Saving plan with', planData.days.length, 'days');
    
    // Update local state FIRST
    setWeeklyPlan(planData);
    console.log('Local state updated immediately');
    
    // Then try to save to backend (but don't use its response)
    try {
      console.log('Attempting to save to backend...');
      await updateWeeklyPlan(planData);
      console.log('Backend save successful (but keeping local state)');
      console.log('=== savePlan END (SUCCESS) ===');
      return planData;
      
    } catch (apiErr) {
      console.error(' Backend save failed, but local state is preserved:', apiErr.message);
      console.log('=== savePlan END (PARTIAL SUCCESS) ===');
      return planData;
    }
    
  } catch (err) {
    console.error(' Error in savePlan:', err);
    setError(err.message);
    console.log('=== savePlan END (FAILED) ===');
    throw err;
  }
};

  const loadDayPlan = async (day) => {
    try {
      console.log('Loading day plan for:', day);
      const dayPlanData = await fetchDayPlan(day);
      console.log('Day plan data:', dayPlanData);
      return dayPlanData;
    } catch (err) {
      console.error('Error loading day plan, creating empty:', err.message);
      return {
        dayName: day,
        breakfast: null,
        lunch: null,
        dinner: null,
        quickMeals: []
      };
    }
  };

  const saveDayPlan = async (day, dayPlanData) => {
    try {
      console.log('Saving day plan for', day, ':', dayPlanData);
      
      // Update local state first
      if (weeklyPlan && weeklyPlan.days) {
        const updatedDays = weeklyPlan.days.map(d => 
          d.dayName === day ? dayPlanData : d
        );
        const newWeeklyPlan = { ...weeklyPlan, days: updatedDays };
        setWeeklyPlan(newWeeklyPlan);
        console.log('Local day plan updated immediately');
      }
      
      // Then save to backend
      try {
        const updated = await updateDayPlan(day, dayPlanData);
        console.log('Day plan saved to API:', updated);
        return updated || dayPlanData;
      } catch (apiErr) {
        console.log('API save failed, but local state is already updated:', apiErr.message);
        return dayPlanData;
      }
    } catch (err) {
      console.error('Error saving day plan:', err);
      setError(err.message);
      throw err;
    }
  };

  const value = {
    weeklyPlan,
    loading,
    error,
    savePlan,
    loadDayPlan,
    saveDayPlan,
    refreshPlan: loadWeeklyPlan,
  };

  return <PlanContext.Provider value={value}>{children}</PlanContext.Provider>;
};