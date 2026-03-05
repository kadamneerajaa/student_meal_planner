import { useState } from 'react';
import Button from '../common/Button';
import Input from '../common/Input';

const MealForm = ({ meal, onSubmit, onCancel }) => {
  // Convert ingredients to comma-separated string for editing
  const formatIngredientsForForm = (ingredients) => {
    console.log('📝 Formatting ingredients:', ingredients);
    
    if (!ingredients) return '';
    
    // If it's an array
    if (Array.isArray(ingredients)) {
      if (ingredients.length === 0) return '';
      
      const formatted = ingredients.map(ing => {
        // If it's an object with name property
        if (typeof ing === 'object' && ing !== null) {
          return ing.name || '';
        }
        // If it's just a string
        return String(ing);
      }).filter(Boolean).join(', ');
      
      console.log('  Formatted from array:', formatted);
      return formatted;
    }
    
    // If it's a pipe-separated string from CSV
    if (typeof ingredients === 'string') {
      const formatted = ingredients.split('|').map(s => s.trim()).filter(Boolean).join(', ');
      console.log('  Formatted from string:', formatted);
      return formatted;
    }
    
    return '';
  };

  const [formData, setFormData] = useState({
  name: meal?.name || '',
  mealType: meal?.mealType?.toLowerCase() || 'breakfast',
  dietaryPreference: meal?.dietaryPreference || '',
  calories: meal?.calories || 0,
  protein: meal?.protein || 0,
  carbs: meal?.carbs || 0,
  fat: meal?.fat || 0,
  cost: meal?.cost || 0,
  ingredients: formatIngredientsForForm(meal?.ingredients),
  prepTimeCategory: (meal?.prepTimeCategory && meal.prepTimeCategory > 0) ? meal.prepTimeCategory : '',
});

  console.log('📋 Form initialized with data:', formData);

  const handleChange = (e) => {
  const { name, value, type } = e.target;
  
  let processedValue = value;
  
  if (type === 'number') {
    const numValue = parseFloat(value);
    
    // Don't allow negative values for these fields
    if (['calories', 'protein', 'carbs', 'fat', 'cost', 'prepTimeCategory'].includes(name)) {
      processedValue = numValue < 0 ? 0 : numValue;
    } else {
      processedValue = numValue || 0;
    }
  }
  
  setFormData((prev) => ({
    ...prev,
    [name]: processedValue,
  }));
};

  const handleSubmit = (e) => {
  e.preventDefault();
  
  console.log('📤 Submitting form data:', formData);
  
  const ingredientsStr = formData.ingredients
    ? formData.ingredients.split(',').map(name => name.trim()).filter(Boolean).join('|')
    : '';
  
  console.log('📦 Ingredients converted to:', ingredientsStr);
  
  const mealData = {
    name: formData.name,
    mealType: formData.mealType.toUpperCase(),
    dietaryPreference: formData.dietaryPreference || '',
    calories: parseInt(formData.calories) || 0,
    protein: parseFloat(formData.protein) || 0,
    carbs: parseFloat(formData.carbs) || 0,
    fat: parseFloat(formData.fat) || 0,
    cost: parseFloat(formData.cost) || 0,
    ingredients: ingredientsStr,
    favoriteFlag: false,
    prepTimeCategory: (formData.prepTimeCategory && formData.prepTimeCategory > 0) 
      ? parseInt(formData.prepTimeCategory) 
      : null  // SEND NULL instead of -1
  };
  
  console.log('📮 Final meal data being sent:', mealData);
  onSubmit(mealData);
};

  return (
    <form onSubmit={handleSubmit}>
      <Input
        label="Meal Name"
        name="name"
        value={formData.name}
        onChange={handleChange}
        required
      />

      <div className="grid grid-cols-2" style={{ gap: '1rem' }}>
        <div className="input-group">
          <label className="input-label input-label-required">
            Meal Type
          </label>
          <select
            name="mealType"
            value={formData.mealType}
            onChange={handleChange}
            className="input-field"
            required
          >
            <option value="breakfast">Breakfast</option>
            <option value="lunch">Lunch</option>
            <option value="dinner">Dinner</option>
            <option value="snack">Snack</option>
          </select>
        </div>

        <Input
          label="Dietary Preference"
          name="dietaryPreference"
          value={formData.dietaryPreference}
          onChange={handleChange}
          placeholder="e.g., Vegetarian, Vegan"
        />
      </div>

      <div className="grid grid-cols-2" style={{ gap: '1rem' }}>
        <Input
          label="Calories"
          type="number"
          name="calories"
          value={formData.calories}
          onChange={handleChange}
          min="0" 
          required
        />

        <Input
          label="Cost ($)"
          type="number"
          step="0.01"
          name="cost"
          value={formData.cost}
          onChange={handleChange}
          min="0"
          required
        />
      </div>

      <div className="grid grid-cols-3" style={{ gap: '1rem' }}>
        <Input
          label="Protein (g)"
          type="number"
          step="0.1"
          name="protein"
          value={formData.protein}
          onChange={handleChange}
          min="0"
        />

        <Input
          label="Carbs (g)"
          type="number"
          step="0.1"
          name="carbs"
          value={formData.carbs}
          onChange={handleChange}
          min="0"
        />

        <Input
          label="Fat (g)"
          type="number"
          step="0.1"
          name="fat"
          value={formData.fat}
          onChange={handleChange}
          min="0"
        />
      </div>

      <Input
        label="Prep Time (minutes)"
        type="number"
        name="prepTimeCategory"
        value={formData.prepTimeCategory}
        onChange={handleChange}
        min="0"
        placeholder="e.g., 5, 15, 30 (leave empty for regular meals)"
      />

      <div className="input-group">
        <label className="input-label">Ingredients</label>
        <textarea
          name="ingredients"
          value={formData.ingredients}
          onChange={handleChange}
          rows="4"
          className="input-field"
          placeholder="List ingredients separated by commas (e.g., Eggs, Bread, Butter)"
        />
      </div>

      <div className="flex justify-end" style={{ gap: '0.75rem', paddingTop: '1rem' }}>
        <Button variant="secondary" onClick={onCancel} type="button">
          Cancel
        </Button>
        <Button variant="primary" type="submit">
          {meal ? 'Update Meal' : 'Create Meal'}
        </Button>
      </div>
    </form>
  );
};

export default MealForm;