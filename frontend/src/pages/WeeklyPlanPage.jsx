import { useEffect, useState } from 'react';
import Button from '../components/common/Button';
import ErrorMessage from '../components/common/ErrorMessage';
import Loader from '../components/common/Loader';
import DailyView from '../components/planning/DailyView';
import WeeklyPlanGrid from '../components/planning/WeeklyPlanGrid';
import { usePlan } from '../context/PlanContext';
import { fetchMeals } from '../utils/api';

const WeeklyPlanPage = () => {
  const { weeklyPlan, loading, error, savePlan, refreshPlan } = usePlan();
  const [selectedDay, setSelectedDay] = useState(null);
  const [isMealSelectorOpen, setIsMealSelectorOpen] = useState(false);
  const [currentMealSlot, setCurrentMealSlot] = useState(null);
  const [availableMeals, setAvailableMeals] = useState([]);
  const [mealsLoading, setMealsLoading] = useState(false);

  // Load meals directly from API
  useEffect(() => {
  const loadMeals = async () => {
    setMealsLoading(true);
    try {
      console.log('Loading meals directly from API...');
      const mealsData = await fetchMeals();
      console.log('Direct API meals loaded:', mealsData);
      
      // ADD THIS DEBUG
      if (mealsData && mealsData.length > 0) {
        console.log('🔍 First meal sample:', mealsData[0]);
        console.log('🔍 First meal has ingredients?', mealsData[0].ingredients);
        console.log('🔍 Ingredients array:', mealsData[0].ingredients);
      }
      
      setAvailableMeals(mealsData);
    } catch (error) {
      console.error('Failed to load meals:', error);
    } finally {
      setMealsLoading(false);
    }
  };
  
  loadMeals();
}, []);

  const handleDayClick = (day) => {
    console.log('Day clicked:', day);
    setSelectedDay(day);
  };

  const handleBackToWeek = () => {
    console.log('Going back to week view');
    setSelectedDay(null);
  };

  const handleAddMeal = (mealSlot) => {
    console.log('Adding meal to slot:', mealSlot, 'for day:', selectedDay);
    
    if (!selectedDay) {
      console.error('No day selected! Cannot add meal.');
      alert('Please select a day first');
      return;
    }
    
    setCurrentMealSlot(mealSlot);
    setIsMealSelectorOpen(true);
    console.log('Modal should open now');
  };

  const handleSelectMeal = async (meal) => {
    console.log('=== handleSelectMeal START ===');
    console.log('Meal selected:', meal);
    console.log('Current meal slot:', currentMealSlot);
    console.log('Selected day:', selectedDay);
    
    if (!selectedDay || !currentMealSlot) {
      console.error('No day or meal slot selected');
      alert('Please select a day and meal slot first');
      return;
    }

    if (!meal || !meal.id || !meal.name) {
      console.error('Invalid meal object:', meal);
      alert('Selected meal is invalid');
      return;
    }

    console.log('Current weekly plan:', weeklyPlan);
    const currentDays = weeklyPlan?.days || [];
    console.log('Current days array:', currentDays);

    let dayPlan = currentDays.find((d) => d.dayName === selectedDay);
    console.log('Found day plan:', dayPlan);
    
    if (!dayPlan) {
      dayPlan = {
        dayName: selectedDay,
        breakfast: null,
        lunch: null,
        dinner: null,
        quickMeals: [],
      };
      console.log('Created new day plan');
    }

    const updatedDayPlan = { ...dayPlan };
    console.log('Day plan before update:', updatedDayPlan);

    // Store only essential meal data
    const mealData = {
      id: meal.id,
      name: meal.name,
      mealType: meal.mealType,
      calories: meal.calories,
      protein: meal.protein,
      carbs: meal.carbs,
      fat: meal.fat,
      cost: meal.cost,
      ingredients: meal.ingredients || []
    };

    if (currentMealSlot === 'quickMeal') {
      updatedDayPlan.quickMeals = [...(updatedDayPlan.quickMeals || []), mealData];
      console.log('Added to quick meals:', updatedDayPlan.quickMeals);
    } else {
      updatedDayPlan[currentMealSlot] = mealData;
      console.log('Set', currentMealSlot, 'to:', mealData);
    }

    console.log('Day plan after update:', updatedDayPlan);

    const updatedDays = currentDays.map((d) =>
      d.dayName === selectedDay ? updatedDayPlan : d
    );
    
    if (!currentDays.find(d => d.dayName === selectedDay)) {
      updatedDays.push(updatedDayPlan);
      console.log('Added new day to array');
    }

    console.log('Updated days array:', updatedDays);

    const updatedWeeklyPlan = { 
      studentId: weeklyPlan?.studentId || "S001",
      studentName: weeklyPlan?.studentName || "Test Student",
      days: updatedDays
    };

    console.log('Final updated weekly plan:', updatedWeeklyPlan);
    
    try {
      console.log('Calling savePlan...');
      await savePlan(updatedWeeklyPlan);
      console.log('✅ Plan saved successfully!');
      
      // Close modal
      setIsMealSelectorOpen(false);
      setCurrentMealSlot(null);
      
    } catch (err) {
      console.error('❌ Error saving plan:', err);
      alert('Failed to save meal: ' + err.message);
    }
    
    console.log('=== handleSelectMeal END ===');
  };

  const handleRemoveMeal = async (mealSlot, index = null) => {
    console.log('Removing meal from slot:', mealSlot, 'index:', index, 'on day:', selectedDay);
    
    if (!selectedDay) {
      console.error('No day selected');
      return;
    }

    const currentDays = weeklyPlan?.days || [];
    const dayPlan = currentDays.find((d) => d.dayName === selectedDay);
    if (!dayPlan) {
      console.error('Day plan not found for:', selectedDay);
      return;
    }

    const updatedDayPlan = { ...dayPlan };

    if (mealSlot === 'quickMeal' && index !== null) {
      updatedDayPlan.quickMeals = (updatedDayPlan.quickMeals || []).filter((_, i) => i !== index);
    } else {
      updatedDayPlan[mealSlot] = null;
    }

    console.log('Updated day plan after removal:', updatedDayPlan);

    const updatedDays = currentDays.map((d) =>
      d.dayName === selectedDay ? updatedDayPlan : d
    );

    const updatedWeeklyPlan = { 
      ...weeklyPlan, 
      days: updatedDays 
    };

    console.log('Saving plan after removal:', updatedWeeklyPlan);
    
    try {
      await savePlan(updatedWeeklyPlan);
      console.log('Meal removed successfully');
    } catch (err) {
      console.error('Error removing meal:', err);
    }
  };

  if (loading) {
    return <Loader text="Loading weekly plan..." />;
  }

  if (error && !weeklyPlan) {
    return <ErrorMessage message={error} onRetry={refreshPlan} />;
  }

  const currentDayPlan = selectedDay
    ? weeklyPlan?.days?.find((d) => d.dayName === selectedDay) || {
        dayName: selectedDay,
        breakfast: null,
        lunch: null,
        dinner: null,
        quickMeals: [],
      }
    : null;

  console.log('Current day plan for', selectedDay, ':', currentDayPlan);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      {!selectedDay ? (
        <>
          <div className="page-header">
            <h1 className="text-3xl font-bold">Weekly Meal Plan</h1>
            <Button onClick={refreshPlan} variant="secondary">
              Refresh Plan
            </Button>
          </div>
          {weeklyPlan && weeklyPlan.days ? (
            <WeeklyPlanGrid weeklyPlan={weeklyPlan} onDayClick={handleDayClick} />
          ) : (
            <div className="text-center py-8">
              <p>No weekly plan data available</p>
              <Button onClick={refreshPlan} variant="primary" className="mt-4">
                Try Again
              </Button>
            </div>
          )}
        </>
      ) : (
        <>
          <div className="flex items-center" style={{ gap: '1rem' }}>
            <Button onClick={handleBackToWeek} variant="secondary">
              ← Back to Week
            </Button>
            <h1 className="text-3xl font-bold">{selectedDay}</h1>
          </div>
          {currentDayPlan && (
            <DailyView
              dayPlan={currentDayPlan}
              onAddMeal={handleAddMeal}
              onRemoveMeal={handleRemoveMeal}
            />
          )}
        </>
      )}

      {/* Meal Selection Modal */}
      {isMealSelectorOpen && (
        <div 
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.5)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000
          }} 
          onClick={(e) => {
            // Only close if clicking the backdrop, not the modal content
            if (e.target === e.currentTarget) {
              setIsMealSelectorOpen(false);
            }
          }}
        >
          <div 
            style={{
              backgroundColor: 'white',
              padding: '20px',
              borderRadius: '8px',
              width: '80%',
              maxWidth: '500px',
              maxHeight: '80vh',
              overflow: 'auto'
            }}
          >
            <h2 style={{ marginBottom: '20px' }}>Select a Meal</h2>
            
            <div style={{ marginBottom: '20px', padding: '10px', background: '#f3f4f6', borderRadius: '6px' }}>
              <p><strong>Selected Day:</strong> {selectedDay}</p>
              <p><strong>Meal Slot:</strong> {currentMealSlot}</p>
            </div>
            
            {mealsLoading ? (
              <div style={{ textAlign: 'center', padding: '20px' }}>
                Loading meals...
              </div>
            ) : availableMeals.length > 0 ? (
              <div>
                {availableMeals.map(meal => (
                  <button
                    key={meal.id}
                    onClick={() => {
                      console.log('🎯 Modal button clicked for meal:', meal);
                      handleSelectMeal(meal);
                    }}
                    style={{
                      display: 'block',
                      width: '100%',
                      padding: '15px',
                      marginBottom: '10px',
                      textAlign: 'left',
                      border: '2px solid #e5e7eb',
                      borderRadius: '6px',
                      background: 'white',
                      cursor: 'pointer',
                      transition: 'all 0.2s'
                    }}
                    onMouseOver={(e) => {
                      e.currentTarget.style.borderColor = '#3b82f6';
                      e.currentTarget.style.backgroundColor = '#f0f9ff';
                    }}
                    onMouseOut={(e) => {
                      e.currentTarget.style.borderColor = '#e5e7eb';
                      e.currentTarget.style.backgroundColor = 'white';
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <strong style={{ fontSize: '1.1em' }}>{meal.name}</strong>
                      <span style={{ 
                        backgroundColor: '#10b981', 
                        color: 'white', 
                        padding: '2px 8px', 
                        borderRadius: '12px',
                        fontSize: '0.9em'
                      }}>
                        ${meal.cost}
                      </span>
                    </div>
                    <div style={{ fontSize: '0.9em', color: '#6b7280', marginTop: '5px' }}>
                      <span style={{ marginRight: '15px' }}>🔥 {meal.calories} cal</span>
                      <span style={{ marginRight: '15px' }}>💪 {meal.protein}g protein</span>
                      <span>{meal.mealType || 'Regular'}</span>
                    </div>
                    <div style={{ 
                      fontSize: '0.8em', 
                      color: '#9ca3af', 
                      marginTop: '5px',
                      fontStyle: 'italic'
                    }}>
                      Click to select for {currentMealSlot}
                    </div>
                  </button>
                ))}
              </div>
            ) : (
              <div style={{ textAlign: 'center', padding: '20px', color: '#6b7280' }}>
                No meals available. Check if backend is running.
                <div style={{ marginTop: '10px' }}>
                  <button 
                    onClick={() => window.location.href = '/meals'}
                    style={{
                      padding: '8px 16px',
                      backgroundColor: '#3b82f6',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer'
                    }}
                  >
                    Go to Meals Page
                  </button>
                </div>
              </div>
            )}
            
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '20px' }}>
              <button
                onClick={() => setIsMealSelectorOpen(false)}
                style={{
                  padding: '10px 20px',
                  backgroundColor: '#6b7280',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer'
                }}
              >
                Cancel
              </button>
              <div style={{ fontSize: '0.9em', color: '#6b7280', display: 'flex', alignItems: 'center' }}>
                {availableMeals.length} meals available
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default WeeklyPlanPage;