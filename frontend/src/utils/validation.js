export const validateMeal = (meal) => {
  const errors = {};

  if (!meal.name || meal.name.trim() === '') {
    errors.name = 'Meal name is required';
  }

  if (!meal.mealType) {
    errors.mealType = 'Meal type is required';
  }

  if (meal.calories < 0) {
    errors.calories = 'Calories cannot be negative';
  }

  if (meal.protein < 0) {
    errors.protein = 'Protein cannot be negative';
  }

  if (meal.carbs < 0) {
    errors.carbs = 'Carbs cannot be negative';
  }

  if (meal.fat < 0) {
    errors.fat = 'Fat cannot be negative';
  }

  if (meal.cost < 0) {
    errors.cost = 'Cost cannot be negative';
  }

  return errors;
};

export const validateStudent = (student) => {
  const errors = {};

  if (!student.name || student.name.trim() === '') {
    errors.name = 'Name is required';
  }

  if (student.dailyCalorieGoal < 0) {
    errors.dailyCalorieGoal = 'Daily calorie goal must be positive';
  }

  if (student.weeklyBudget < 0) {
    errors.weeklyBudget = 'Weekly budget must be positive';
  }

  return errors;
};

export const validatePantryItem = (item) => {
  const errors = {};

  if (!item.name || item.name.trim() === '') {
    errors.name = 'Item name is required';
  }

  if (!item.unit || item.unit.trim() === '') {
    errors.unit = 'Unit is required';
  }

  if (item.quantity < 0) {
    errors.quantity = 'Quantity cannot be negative';
  }

  return errors;
};