import { useState } from 'react';
import ErrorMessage from '../components/common/ErrorMessage';
import Loader from '../components/common/Loader';
import BudgetTracker from '../components/shopping/BudgetTracker';
import PantryManager from '../components/shopping/PantryManager';
import ShoppingList from '../components/shopping/ShoppingList';
import { useStudent } from '../context/StudentContext';
import useShopping from '../hooks/useShopping';
import { addPantryItem, deletePantryItem, updatePantryItem } from '../utils/api';

const ShoppingPage = () => {
  const { student } = useStudent();
  const { shoppingList, pantry, loading, error, refreshShopping } = useShopping();
  const [checkedItems, setCheckedItems] = useState(new Set());

  const handleToggleItem = (itemId) => {
    setCheckedItems((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(itemId)) {
        newSet.delete(itemId);
      } else {
        newSet.add(itemId);
      }
      return newSet;
    });
  };

  const handleAddPantryItem = async (itemData) => {
    try {
      console.log('Adding pantry item:', itemData);
      await addPantryItem(itemData);
      await refreshShopping();
      console.log('Pantry item added successfully');
    } catch (err) {
      console.error('Error adding pantry item:', err);
      throw err; // Re-throw to let PantryManager handle it
    }
  };

  const handleUpdatePantryItem = async (id, itemData) => {
    try {
      console.log('Updating pantry item:', id, itemData);
      await updatePantryItem(id, itemData);
      await refreshShopping();
      console.log('Pantry item updated successfully');
    } catch (err) {
      console.error('Error updating pantry item:', err);
      throw err;
    }
  };

  const handleDeletePantryItem = async (id) => {
    try {
      console.log('Deleting pantry item:', id);
      await deletePantryItem(id);
      await refreshShopping();
      console.log('Pantry item deleted successfully');
    } catch (err) {
      console.error('Error deleting pantry item:', err);
      throw err;
    }
  };

  if (loading) {
    return <Loader text="Loading shopping data..." />;
  }

  if (error) {
    return <ErrorMessage message={error} onRetry={refreshShopping} />;
  }

  const totalCost = Array.isArray(shoppingList)
    ? shoppingList.reduce((sum, item) => sum + (item.totalCost || item.cost || 0), 0)
    : 0;
  
  const currentSpending = totalCost;

  const itemsWithChecked = Array.isArray(shoppingList)
    ? shoppingList.map((item) => ({
        ...item,
        checked: checkedItems.has(item.id || item.name),
      }))
    : [];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <h1 className="text-3xl font-bold">Shopping & Pantry</h1>

      <div className="shopping-layout">
        <div>
          <ShoppingList
            items={itemsWithChecked}
            onToggleItem={handleToggleItem}
            totalCost={totalCost}
          />
        </div>
        <div>
          <BudgetTracker
            weeklyBudget={student?.weeklyBudget || 0}
            currentSpending={currentSpending}
          />
        </div>
      </div>

      <PantryManager
        pantryItems={pantry || []}
        onAdd={handleAddPantryItem}
        onUpdate={handleUpdatePantryItem}
        onDelete={handleDeletePantryItem}
      />
    </div>
  );
};

export default ShoppingPage;