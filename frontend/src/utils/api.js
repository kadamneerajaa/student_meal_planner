import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Meals API
export const fetchMeals = async () => {
  const response = await api.get('/meals');
  return response.data;
};

export const fetchMealById = async (id) => {
  const response = await api.get(`/meals/${id}`);
  return response.data;
};

export const createMeal = async (mealData) => {
  console.log('API: Creating meal with data:', mealData);
  const response = await api.post('/meals', mealData);
  console.log('API: Create response:', response.data);
  return response.data;
};

export const updateMeal = async (id, mealData) => {
  console.log('API: Updating meal', id, 'with data:', mealData);
  try {
    const response = await api.put(`/meals/${id}`, mealData);
    console.log('API: Update response:', response.data);
    return response.data;
  } catch (error) {
    console.error('API: Update error:', {
      status: error.response?.status,
      data: error.response?.data,
      message: error.message
    });
    throw error;
  }
};

export const deleteMeal = async (id) => {
  const response = await api.delete(`/meals/${id}`);
  return response.data;
};

export const searchMeals = async (name) => {
  const response = await api.get(`/meals/search?name=${encodeURIComponent(name)}`);
  return response.data;
};

export const fetchFavoriteMeals = async () => {
  const response = await api.get('/meals/favorites');
  return response.data;
};

export const fetchQuickMeals = async (prepTime) => {
  const response = await api.get(`/meals/quick?prepTime=${prepTime}`);
  return response.data;
};

// Plans API
export const fetchWeeklyPlan = async () => {
  const response = await api.get('/plans');
  return response.data;
};

export const updateWeeklyPlan = async (planData) => {
  console.log('📤 Sending plan to API:', planData);
  const response = await api.post('/plans', planData);
  console.log('✅ Plan saved successfully:', response.data);
  return response.data;
};

export const fetchDayPlan = async (day) => {
  const response = await api.get(`/plans/day/${day}`);
  return response.data;
};

export const updateDayPlan = async (day, dayPlanData) => {
  const response = await api.put(`/plans/day/${day}`, dayPlanData);
  return response.data;
};

// Shopping API
export const fetchShoppingList = async () => {
  const response = await api.get('/shopping/list');
  return response.data;
};

export const fetchPantry = async () => {
  const response = await api.get('/shopping/pantry');
  return response.data;
};

export const addPantryItem = async (itemData) => {
  console.log(' Adding pantry item:', itemData);
  
  const params = new URLSearchParams({
    name: itemData.name,
    unit: itemData.unit,
    quantity: itemData.quantity,
    costPerUnit: itemData.costPerUnit
  });
  
  const url = `${API_BASE_URL}/shopping/pantry?${params}`;
  console.log(' Full URL:', url);
  
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    }
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to add pantry item');
  }

  return await response.json();
};

export const updatePantryItem = async (id, itemData) => {
  console.log(' Updating pantry item:', id, itemData);
  
  const encodedName = encodeURIComponent(id);
  
  const params = new URLSearchParams({
    unit: itemData.unit,
    quantity: itemData.quantity,
    costPerUnit: itemData.costPerUnit
  });
  
  const url = `${API_BASE_URL}/shopping/pantry/${encodedName}?${params}`;
  
  const response = await fetch(url, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    }
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to update pantry item');
  }

  return await response.json();
};

export const deletePantryItem = async (id) => {
  console.log(' Deleting pantry item:', id);
  
  const encodedName = encodeURIComponent(id);
  const url = `${API_BASE_URL}/shopping/pantry/${encodedName}`;
  
  const response = await fetch(url, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    }
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.error || 'Failed to delete pantry item');
  }

  return await response.json();
};

// Reports API
export const fetchDailySummary = async (day) => {
  const response = await api.get(`/reports/daily?day=${day}`);
  return response.data;
};

export const fetchWeeklySummary = async () => {
  const response = await api.get('/reports/weekly');
  return response.data;
};

// Student API
export const fetchStudent = async () => {
  const response = await api.get('/student');
  return response.data;
};

export const updateStudent = async (studentData) => {
  const response = await api.put('/student', studentData);
  return response.data;
};

export default api;