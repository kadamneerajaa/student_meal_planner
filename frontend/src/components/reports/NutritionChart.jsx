import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import Card from '../common/Card';

const NutritionChart = ({ weeklySummary }) => {
  // Use days from weekly summary
  const days = weeklySummary?.days || [];
  
  const chartData = days.map((day) => ({
    name: day.dayName.substring(0, 3),
    Calories: Math.round(day.totalCalories) || 0,
    Protein: Math.round(day.totalProtein) || 0,
    Carbs: Math.round(day.totalCarbs) || 0,
    Fat: Math.round(day.totalFat) || 0,
  }));

  // Check if there's any data
  const hasData = chartData.some(day => 
    day.Calories > 0 || day.Protein > 0 || day.Carbs > 0 || day.Fat > 0
  );

  if (!hasData) {
    return (
      <Card title="Weekly Nutrition Overview">
        <div style={{ padding: '2rem', textAlign: 'center', color: '#9ca3af' }}>
          No meals planned yet. Add meals to your weekly plan to see nutrition data.
        </div>
      </Card>
    );
  }

  return (
    <Card title="Weekly Nutrition Overview">
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Bar dataKey="Calories" fill="#3b82f6" />
          <Bar dataKey="Protein" fill="#10b981" />
          <Bar dataKey="Carbs" fill="#8b5cf6" />
          <Bar dataKey="Fat" fill="#f59e0b" />
        </BarChart>
      </ResponsiveContainer>
    </Card>
  );
};

export default NutritionChart;