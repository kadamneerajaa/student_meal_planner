export const formatCurrency = (amount) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount);
};

export const formatNumber = (number, decimals = 1) => {
  return number.toFixed(decimals);
};

export const formatDate = (date) => {
  return new Date(date).toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
};

export const capitalizeFirst = (string) => {
  return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
};

export const getDayName = (dayIndex) => {
  const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
  return days[dayIndex] || 'Monday';
};