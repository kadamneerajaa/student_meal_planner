import { useEffect, useState } from 'react';
import ErrorMessage from '../components/common/ErrorMessage';
import Loader from '../components/common/Loader';
import CostBreakdown from '../components/reports/CostBreakdown';
import DailySummaryCard from '../components/reports/DailySummaryCard';
import GoalProgress from '../components/reports/GoalProgress';
import NutritionChart from '../components/reports/NutritionChart';
import WeeklySummaryCard from '../components/reports/WeeklySummaryCard';
import { useStudent } from '../context/StudentContext';
import useReports from '../hooks/useReports';

const ReportsPage = () => {
  const { student } = useStudent();
  const { loading, error, loadDailySummary, loadWeeklySummary } = useReports();
  const [selectedDay, setSelectedDay] = useState('Monday');
  const [dailySummary, setDailySummary] = useState(null);
  const [weeklySummary, setWeeklySummary] = useState(null);

  useEffect(() => {
    console.log('📊 Loading reports for day:', selectedDay);
    const daily = loadDailySummary(selectedDay);
    const weekly = loadWeeklySummary();
    
    setDailySummary(daily);
    setWeeklySummary(weekly);
  }, [selectedDay]);

  const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

  if (loading) {
    return <Loader text="Loading reports..." />;
  }

  if (error) {
    return <ErrorMessage message={error} onRetry={() => {
      const daily = loadDailySummary(selectedDay);
      const weekly = loadWeeklySummary();
      setDailySummary(daily);
      setWeeklySummary(weekly);
    }} />;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <h1 className="text-3xl font-bold">Reports & Analytics</h1>

      <div>
        <label className="input-label">
          Select Day for Daily Summary
        </label>
        <select
          value={selectedDay}
          onChange={(e) => setSelectedDay(e.target.value)}
          className="day-selector"
        >
          {days.map((day) => (
            <option key={day} value={day}>
              {day}
            </option>
          ))}
        </select>
      </div>

      {dailySummary && (
        <>
          <DailySummaryCard summary={dailySummary} student={student} />
          <GoalProgress summary={dailySummary} student={student} />
        </>
      )}

      {weeklySummary && (
        <>
          <WeeklySummaryCard summary={weeklySummary} student={student} />
          <div className="reports-charts">
            <NutritionChart weeklySummary={weeklySummary} />
            <CostBreakdown weeklySummary={weeklySummary} />
          </div>
        </>
      )}
    </div>
  );
};

export default ReportsPage;