import DayPlanCard from './DayPlanCard';

const WeeklyPlanGrid = ({ weeklyPlan, onDayClick }) => {
  const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

  return (
    <div className="grid grid-cols-1 grid-md-cols-2 grid-lg-cols-4">
      {days.map((day) => {
        const dayPlan = weeklyPlan?.days?.find(d => d.dayName === day) || {
          dayName: day,
          breakfast: null,
          lunch: null,
          dinner: null,
          quickMeals: []
        };
        
        return (
          <DayPlanCard
            key={day}
            dayPlan={dayPlan}
            onClick={() => onDayClick(day)}
          />
        );
      })}
    </div>
  );
};

export default WeeklyPlanGrid;