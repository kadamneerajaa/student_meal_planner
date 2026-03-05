export const calculateDailyTotals = (dayPlan) => {
  let totalCalories = 0;
  let totalProtein = 0;
  let totalCarbs = 0;
  let totalFat = 0;
  let totalCost = 0;

  if (dayPlan.breakfast) {
    totalCalories += dayPlan.breakfast.calories || 0;
    totalProtein += dayPlan.breakfast.protein || 0;
    totalCarbs += dayPlan.breakfast.carbs || 0;
    totalFat += dayPlan.breakfast.fat || 0;
    totalCost += dayPlan.breakfast.cost || 0;
  }

  if (dayPlan.lunch) {
    totalCalories += dayPlan.lunch.calories || 0;
    totalProtein += dayPlan.lunch.protein || 0;
    totalCarbs += dayPlan.lunch.carbs || 0;
    totalFat += dayPlan.lunch.fat || 0;
    totalCost += dayPlan.lunch.cost || 0;
  }

  if (dayPlan.dinner) {
    totalCalories += dayPlan.dinner.calories || 0;
    totalProtein += dayPlan.dinner.protein || 0;
    totalCarbs += dayPlan.dinner.carbs || 0;
    totalFat += dayPlan.dinner.fat || 0;
    totalCost += dayPlan.dinner.cost || 0;
  }

  if (dayPlan.quickMeals && Array.isArray(dayPlan.quickMeals)) {
    dayPlan.quickMeals.forEach(meal => {
      totalCalories += meal.calories || 0;
      totalProtein += meal.protein || 0;
      totalCarbs += meal.carbs || 0;
      totalFat += meal.fat || 0;
      totalCost += meal.cost || 0;
    });
  }

  return {
    totalCalories,
    totalProtein,
    totalCarbs,
    totalFat,
    totalCost,
  };
};

export const calculateWeeklyTotals = (weeklyPlan) => {
  let weeklyCalories = 0;
  let weeklyProtein = 0;
  let weeklyCarbs = 0;
  let weeklyFat = 0;
  let weeklyCost = 0;

  if (weeklyPlan.days && Array.isArray(weeklyPlan.days)) {
    weeklyPlan.days.forEach(dayPlan => {
      const dailyTotals = calculateDailyTotals(dayPlan);
      weeklyCalories += dailyTotals.totalCalories;
      weeklyProtein += dailyTotals.totalProtein;
      weeklyCarbs += dailyTotals.totalCarbs;
      weeklyFat += dailyTotals.totalFat;
      weeklyCost += dailyTotals.totalCost;
    });
  }

  return {
    weeklyCalories,
    weeklyProtein,
    weeklyCarbs,
    weeklyFat,
    weeklyCost,
  };
};

export const checkBudget = (totalCost, weeklyBudget) => {
  return {
    isWithinBudget: totalCost <= weeklyBudget,
    remaining: weeklyBudget - totalCost,
    percentageUsed: (totalCost / weeklyBudget) * 100,
  };
};

export const checkGoalAchievement = (actual, goal) => {
  const percentage = (actual / goal) * 100;
  return {
    achieved: actual >= goal * 0.9 && actual <= goal * 1.1, // Within 10%
    percentage,
    difference: actual - goal,
  };
};