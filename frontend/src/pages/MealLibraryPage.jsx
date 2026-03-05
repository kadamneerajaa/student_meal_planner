import { Plus } from 'lucide-react';
import { useState } from 'react';
import Button from '../components/common/Button';
import Modal from '../components/common/Modal';
import MealForm from '../components/meals/MealForm';
import MealList from '../components/meals/MealList';
import MealSearchBar from '../components/meals/MealSearchBar';
import MealSortDropdown from '../components/meals/MealSortDropdown';
import QuickMealFilter from '../components/meals/QuickMealFilter';
import { useMeals } from '../context/MealContext';
import { deleteMeal as deleteMealApi } from '../utils/api';

const MealLibraryPage = () => {
  const { meals, loading, error, addMeal, editMeal, refreshMeals } = useMeals();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingMeal, setEditingMeal] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState('name');
  const [quickMealTime, setQuickMealTime] = useState(null);

  console.log('MealLibraryPage: Current meals:', meals);

  const filteredMeals = meals
  .filter((meal) => {
    const matchesSearch = meal.name.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesQuickTime = !quickMealTime || 
      (meal.prepTimeCategory > 0 && meal.prepTimeCategory <= quickMealTime);
    
    return matchesSearch && matchesQuickTime;
  })
  .sort((a, b) => {
    if (sortBy === 'name') return a.name.localeCompare(b.name);
    if (sortBy === 'calories') return a.calories - b.calories;
    if (sortBy === 'cost') return a.cost - b.cost;
    if (sortBy === 'protein') return b.protein - a.protein;
    return 0;
  });

  const handleOpenModal = (meal = null) => {
    console.log("MealLibraryPage: Opening modal with meal:", meal);
    setEditingMeal(meal);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    console.log("MealLibraryPage: Closing modal");
    setEditingMeal(null);
    setIsModalOpen(false);
  };

  const handleSubmit = async (mealData) => {
    console.log('MealLibraryPage: Submitting meal data:', {
      editingMeal,
      mealData,
      isEditMode: !!editingMeal
    });
    
    try {
      if (editingMeal) {
        console.log(`MealLibraryPage: Calling editMeal for ID: ${editingMeal.id}`);
        await editMeal(editingMeal.id, mealData);
        console.log('MealLibraryPage: Edit successful');
      } else {
        console.log('MealLibraryPage: Calling addMeal');
        await addMeal(mealData);
        console.log('MealLibraryPage: Add successful');
      }
      handleCloseModal();
    } catch (err) {
      console.error('MealLibraryPage: Error saving meal:', err);
    }
  };

  const handleDeleteMeal = async (mealId) => {
    const meal = meals.find(m => m.id === mealId);
    if (!meal) return;

    const confirmDelete = window.confirm(
      `Are you sure you want to delete "${meal.name}"?\n\nThis action cannot be undone.`
    );

    if (confirmDelete) {
      try {
        console.log('Deleting meal:', mealId);
        await deleteMealApi(mealId);
        await refreshMeals();
        console.log('✅ Meal deleted successfully');
      } catch (err) {
        console.error('Error deleting meal:', err);
        alert('Failed to delete meal: ' + err.message);
      }
    }
  };

  

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <div className="page-header">
        <h1 className="text-3xl font-bold">Meal Library</h1>
        <Button onClick={() => handleOpenModal()} variant="primary">
          <Plus size={20} />
          Add New Meal
        </Button>
      </div>

      <div className="filters-section">
        <MealSearchBar onSearch={setSearchQuery} />
        <MealSortDropdown sortBy={sortBy} onSortChange={setSortBy} />
      </div>

      <QuickMealFilter selectedTime={quickMealTime} onTimeChange={setQuickMealTime} />

      <MealList
        meals={filteredMeals}
        loading={loading}
        error={error}
        onSelectMeal={handleOpenModal}
        onDeleteMeal={handleDeleteMeal}
        onRetry={refreshMeals}
      />

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={editingMeal ? 'Edit Meal' : 'Add New Meal'}
      >
        <MealForm meal={editingMeal} onSubmit={handleSubmit} onCancel={handleCloseModal} />
      </Modal>
    </div>
  );
};

export default MealLibraryPage;