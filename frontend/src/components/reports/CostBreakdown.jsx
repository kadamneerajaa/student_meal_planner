import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from 'recharts';
import { formatCurrency } from '../../utils/formatters';
import Card from '../common/Card';

const CostBreakdown = ({ weeklySummary }) => {
  // Use days from weekly summary instead of weeklyData
  const days = weeklySummary?.days || [];
  
  // Filter out days with no cost
  const data = days
    .filter(day => day.totalCost > 0)
    .map((day) => ({
      name: day.dayName,
      value: day.totalCost || 0,
    }));

  const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#14b8a6'];

  if (data.length === 0) {
    return (
      <Card title="Weekly Cost Breakdown">
        <div style={{ padding: '2rem', textAlign: 'center', color: '#9ca3af' }}>
          No meals planned yet. Add meals to your weekly plan to see the cost breakdown.
        </div>
      </Card>
    );
  }

  return (
    <Card title="Weekly Cost Breakdown">
      <ResponsiveContainer width="100%" height={300}>
        <PieChart>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
            outerRadius={80}
            fill="#8884d8"
            dataKey="value"
          >
            {data.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip formatter={(value) => formatCurrency(value)} />
        </PieChart>
      </ResponsiveContainer>

      <div className="mt-4" style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
        {data.map((day, index) => (
          <div key={day.name} className="flex items-center justify-between">
            <div className="flex items-center" style={{ gap: '0.5rem' }}>
              <div
                style={{
                  width: '1rem',
                  height: '1rem',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: COLORS[index % COLORS.length]
                }}
              ></div>
              <span className="text-sm">{day.name}</span>
            </div>
            <span className="font-semibold">{formatCurrency(day.value)}</span>
          </div>
        ))}
      </div>
    </Card>
  );
};

export default CostBreakdown;