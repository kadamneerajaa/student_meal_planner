import { usePlan } from '../context/PlanContext';

const useReports = () => {
  const { weeklyPlan } = usePlan();

  const calculateDailySummary = (day) => {
    console.log('📊 Calculating daily summary for:', day);
    console.log('📊 Weekly plan:', weeklyPlan);

    if (!weeklyPlan || !weeklyPlan.days) {
      console.log('No weekly plan available');
      return {
        dayPlan: { dayName: day },
        totalCalories: 0,
        totalProtein: 0,
        totalCarbs: 0,
        totalFat: 0,
        totalCost: 0,
        calorieGoalMet: false,
        macroGoalsMet: false
      };
    }

    const dayPlan = weeklyPlan.days.find(d => d.dayName === day);
    console.log('📊 Found day plan:', dayPlan);

    if (!dayPlan) {
      return {
        dayPlan: { dayName: day },
        totalCalories: 0,
        totalProtein: 0,
        totalCarbs: 0,
        totalFat: 0,
        totalCost: 0,
        calorieGoalMet: false,
        macroGoalsMet: false
      };
    }

    let totalCalories = 0;
    let totalProtein = 0;
    let totalCarbs = 0;
    let totalFat = 0;
    let totalCost = 0;

    // Add breakfast
    if (dayPlan.breakfast) {
      totalCalories += dayPlan.breakfast.calories || 0;
      totalProtein += dayPlan.breakfast.protein || 0;
      totalCarbs += dayPlan.breakfast.carbs || 0;
      totalFat += dayPlan.breakfast.fat || 0;
      totalCost += dayPlan.breakfast.cost || 0;
    }

    // Add lunch
    if (dayPlan.lunch) {
      totalCalories += dayPlan.lunch.calories || 0;
      totalProtein += dayPlan.lunch.protein || 0;
      totalCarbs += dayPlan.lunch.carbs || 0;
      totalFat += dayPlan.lunch.fat || 0;
      totalCost += dayPlan.lunch.cost || 0;
    }

    // Add dinner
    if (dayPlan.dinner) {
      totalCalories += dayPlan.dinner.calories || 0;
      totalProtein += dayPlan.dinner.protein || 0;
      totalCarbs += dayPlan.dinner.carbs || 0;
      totalFat += dayPlan.dinner.fat || 0;
      totalCost += dayPlan.dinner.cost || 0;
    }

    // Add quick meals
    if (dayPlan.quickMeals && Array.isArray(dayPlan.quickMeals)) {
      dayPlan.quickMeals.forEach(meal => {
        totalCalories += meal.calories || 0;
        totalProtein += meal.protein || 0;
        totalCarbs += meal.carbs || 0;
        totalFat += meal.fat || 0;
        totalCost += meal.cost || 0;
      });
    }

    const summary = {
      dayPlan: { dayName: day },
      totalCalories: Math.round(totalCalories),
      totalProtein: Math.round(totalProtein),
      totalCarbs: Math.round(totalCarbs),
      totalFat: Math.round(totalFat),
      totalCost: parseFloat(totalCost.toFixed(2)),
      calorieGoalMet: totalCalories >= 1800 && totalCalories <= 2200,
      macroGoalsMet: totalProtein >= 135 && totalCarbs >= 225 && totalFat >= 58.5
    };

    console.log('Daily summary calculated:', summary);
    return summary;
  };

  const calculateWeeklySummary = () => {
    console.log('📊 Calculating weekly summary');
    console.log('📊 Weekly plan:', weeklyPlan);

    if (!weeklyPlan || !weeklyPlan.days) {
      console.log('⚠️ No weekly plan available');
      return {
        weeklyTotalCalories: 0,
        weeklyTotalProtein: 0,
        weeklyTotalCarbs: 0,
        weeklyTotalFat: 0,
        weeklyTotalCost: 0,
        budgetMet: true,
        weeklyGoalsMet: false,
        days: [],
        ingredientUsageSummary: {}
      };
    }

    let weeklyTotalCalories = 0;
    let weeklyTotalProtein = 0;
    let weeklyTotalCarbs = 0;
    let weeklyTotalFat = 0;
    let weeklyTotalCost = 0;

    const daysWithData = weeklyPlan.days.map(dayPlan => {
      let dayCalories = 0;
      let dayProtein = 0;
      let dayCarbs = 0;
      let dayFat = 0;
      let dayCost = 0;

      if (dayPlan.breakfast) {
        dayCalories += dayPlan.breakfast.calories || 0;
        dayProtein += dayPlan.breakfast.protein || 0;
        dayCarbs += dayPlan.breakfast.carbs || 0;
        dayFat += dayPlan.breakfast.fat || 0;
        dayCost += dayPlan.breakfast.cost || 0;
      }

      if (dayPlan.lunch) {
        dayCalories += dayPlan.lunch.calories || 0;
        dayProtein += dayPlan.lunch.protein || 0;
        dayCarbs += dayPlan.lunch.carbs || 0;
        dayFat += dayPlan.lunch.fat || 0;
        dayCost += dayPlan.lunch.cost || 0;
      }

      if (dayPlan.dinner) {
        dayCalories += dayPlan.dinner.calories || 0;
        dayProtein += dayPlan.dinner.protein || 0;
        dayCarbs += dayPlan.dinner.carbs || 0;
        dayFat += dayPlan.dinner.fat || 0;
        dayCost += dayPlan.dinner.cost || 0;
      }

      if (dayPlan.quickMeals && Array.isArray(dayPlan.quickMeals)) {
        dayPlan.quickMeals.forEach(meal => {
          dayCalories += meal.calories || 0;
          dayProtein += meal.protein || 0;
          dayCarbs += meal.carbs || 0;
          dayFat += meal.fat || 0;
          dayCost += meal.cost || 0;
        });
      }

      weeklyTotalCalories += dayCalories;
      weeklyTotalProtein += dayProtein;
      weeklyTotalCarbs += dayCarbs;
      weeklyTotalFat += dayFat;
      weeklyTotalCost += dayCost;

      return {
        dayName: dayPlan.dayName,
        totalCalories: dayCalories,
        totalProtein: dayProtein,
        totalCarbs: dayCarbs,
        totalFat: dayFat,
        totalCost: dayCost
      };
    });

    const weeklyBudget = 100;

    const summary = {
      weeklyTotalCalories: Math.round(weeklyTotalCalories),
      weeklyTotalProtein: Math.round(weeklyTotalProtein),
      weeklyTotalCarbs: Math.round(weeklyTotalCarbs),
      weeklyTotalFat: Math.round(weeklyTotalFat),
      weeklyTotalCost: parseFloat(weeklyTotalCost.toFixed(2)),
      budgetMet: weeklyTotalCost <= weeklyBudget,
      weeklyGoalsMet: weeklyTotalCalories >= 12600 && weeklyTotalCalories <= 15400,
      days: daysWithData,
      ingredientUsageSummary: {}
    };

    console.log('Weekly summary calculated:', summary);
    return summary;
  };

  return {
    loading: false,
    error: null,
    loadDailySummary: calculateDailySummary,
    loadWeeklySummary: calculateWeeklySummary,
  };
};

export default useReports;