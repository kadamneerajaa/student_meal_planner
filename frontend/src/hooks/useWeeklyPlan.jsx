import { useEffect, useState } from 'react';
import { fetchWeeklyPlan, updateWeeklyPlan } from '../utils/api';

const useWeeklyPlan = () => {
  const [weeklyPlan, setWeeklyPlan] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadPlan();
  }, []);

  const loadPlan = async () => {
    try {
      setLoading(true);
      const data = await fetchWeeklyPlan();
      setWeeklyPlan(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const savePlan = async (planData) => {
    try {
      const updated = await updateWeeklyPlan(planData);
      setWeeklyPlan(updated);
      return updated;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  return {
    weeklyPlan,
    loading,
    error,
    savePlan,
    refreshPlan: loadPlan,
  };
};

export default useWeeklyPlan;