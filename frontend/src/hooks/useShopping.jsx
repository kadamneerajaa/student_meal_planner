import { useEffect, useState } from 'react';
import { usePlan } from '../context/PlanContext';
import { fetchPantry } from '../utils/api';

const useShopping = () => {
  const { weeklyPlan } = usePlan();
  const [shoppingList, setShoppingList] = useState([]);
  const [pantry, setPantry] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadShoppingData();
  }, [weeklyPlan]);

  const loadShoppingData = async () => {
    try {
      setLoading(true);
      
      const pantryData = await fetchPantry();
      console.log('🏠 Pantry loaded:', pantryData);
      setPantry(pantryData);
      
      const generatedList = generateShoppingListFromPlan(weeklyPlan, pantryData);
      console.log('🛒 Shopping list generated:', generatedList);
      setShoppingList(generatedList);
      
      setError(null);
    } catch (err) {
      console.error('Error loading shopping data:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Helper: Convert different units to a common base for comparison
  const convertToBaseUnit = (quantity, unit, ingredientName) => {
    const name = ingredientName.toLowerCase();
    const unitLower = unit.toLowerCase();
    
    console.log(`  🔄 Converting: ${quantity} ${unit} of ${ingredientName}`);
    
    // Egg conversions
    if (name.includes('egg')) {
      if (unitLower === 'dozen') {
        const converted = quantity * 12;
        console.log(`    ✓ Dozen → Count: ${quantity} dozen = ${converted} eggs`);
        return converted;
      }
      if (unitLower === 'count') {
        console.log(`    ✓ Already in count: ${quantity}`);
        return quantity;
      }
    }
    
    // Milk conversions
    if (name.includes('milk')) {
      if (unitLower === 'gallon') {
        const converted = quantity * 16;
        console.log(`     Gallon → Cup: ${quantity} gallon = ${converted} cups`);
        return converted;
      }
      if (unitLower === 'cup') {
        console.log(`    Already in cups: ${quantity}`);
        return quantity;
      }
    }
    
    // Bread conversions
    if (name.includes('bread')) {
      if (unitLower === 'loaf') {
        const converted = quantity * 20;
        console.log(`     Loaf → Slices: ${quantity} loaf = ${converted} slices`);
        return converted;
      }
      if (unitLower === 'slices' || unitLower === 'slice') {
        console.log(`     Already in slices: ${quantity}`);
        return quantity;
      }
    }
    
    // Rice conversions
    if (name.includes('rice')) {
      if (unitLower === 'lbs' || unitLower === 'lb') {
        const converted = quantity * 2;
        console.log(`     Lbs → Cup: ${quantity} lbs = ${converted} cups`);
        return converted;
      }
      if (unitLower === 'cup') {
        console.log(`    Already in cups: ${quantity}`);
        return quantity;
      }
    }
    
    // Default: no conversion
    console.log(`     No conversion needed: ${quantity} ${unit}`);
    return quantity;
  };

  const generateShoppingListFromPlan = (plan, pantryItems) => {
    console.log('\n═══════════════════════════════════════════════════');
    console.log('🛒 GENERATING SHOPPING LIST');
    console.log('═══════════════════════════════════════════════════\n');
    
    if (!plan || !plan.days) {
      console.log(' No plan available');
      return [];
    }

    // Collect all meals from the weekly plan
    const allMeals = [];
    plan.days.forEach(day => {
      if (day.breakfast) allMeals.push(day.breakfast);
      if (day.lunch) allMeals.push(day.lunch);
      if (day.dinner) allMeals.push(day.dinner);
      if (day.quickMeals && day.quickMeals.length > 0) {
        allMeals.push(...day.quickMeals);
      }
    });

    console.log(` Total meals in plan: ${allMeals.length}`);
    allMeals.forEach((m, i) => console.log(`  ${i + 1}. ${m.name}`));
    console.log('');

    if (allMeals.length === 0) {
      console.log(' No meals in plan - shopping list will be empty\n');
      return [];
    }

    // Aggregate ingredients from all meals
    console.log(' STEP 1: Aggregating ingredients from all meals\n');
    const ingredientNeeds = {};
    
    allMeals.forEach((meal, mealIndex) => {
      console.log(`\nMeal ${mealIndex + 1}: ${meal.name}`);
      
      if (meal.ingredients && Array.isArray(meal.ingredients) && meal.ingredients.length > 0) {
        console.log(`  Ingredients (${meal.ingredients.length}):`);
        
        meal.ingredients.forEach(ing => {
          const key = `${ing.name.toLowerCase()}_${ing.unit.toLowerCase()}`;
          
          if (!ingredientNeeds[key]) {
            ingredientNeeds[key] = {
              name: ing.name,
              unit: ing.unit,
              quantity: 0,
              costPerUnit: ing.costPerUnit || 0
            };
          }
          
          ingredientNeeds[key].quantity += ing.quantity;
          console.log(`     ${ing.name}: +${ing.quantity} ${ing.unit} (Total now: ${ingredientNeeds[key].quantity})`);
        });
      } else {
        console.log(`   No ingredients found`);
      }
    });

    console.log('\n\n📋 STEP 2: Total ingredients needed across all meals\n');
    Object.values(ingredientNeeds).forEach(need => {
      console.log(`  • ${need.name}: ${need.quantity} ${need.unit} @ $${need.costPerUnit}/${need.unit}`);
    });

    // Check pantry and generate shopping list
    console.log('\n\n🏠 STEP 3: Checking pantry and calculating what to buy\n');
    const shoppingList = [];
    
    Object.values(ingredientNeeds).forEach(need => {
      console.log(`\n━━━ ${need.name.toUpperCase()} ━━━`);
      console.log(`📦 Recipe needs: ${need.quantity} ${need.unit}`);
      
      // Find matching pantry item by name (case-insensitive)
      const pantryItem = pantryItems.find(
        p => p.name.toLowerCase() === need.name.toLowerCase()
      );
      
      if (pantryItem) {
        console.log(`🏠 Pantry has: ${pantryItem.quantity} ${pantryItem.unit}`);
        
        // Convert both to base units for comparison
        const pantryBaseQty = convertToBaseUnit(pantryItem.quantity, pantryItem.unit, need.name);
        const needBaseQty = convertToBaseUnit(need.quantity, need.unit, need.name);
        
        console.log(`\n  📐 Conversion to base units:`);
        console.log(`    Pantry: ${pantryItem.quantity} ${pantryItem.unit} = ${pantryBaseQty} base units`);
        console.log(`    Need: ${need.quantity} ${need.unit} = ${needBaseQty} base units`);
        
        if (pantryBaseQty >= needBaseQty) {
          const surplus = pantryBaseQty - needBaseQty;
          console.log(`\n  ✅ SUFFICIENT! You have ${surplus} base units extra`);
          console.log(`  ➡️ NOT adding to shopping list`);
        } else {
          const deficit = needBaseQty - pantryBaseQty;
          console.log(`\n  ⚠️ INSUFFICIENT! You need ${deficit} more base units`);
          
          // Convert deficit back to recipe's unit
          const unitConversionFactor = convertToBaseUnit(1, need.unit, need.name);
          const deficitInRecipeUnit = deficit / unitConversionFactor;
          
          console.log(`  📐 Deficit in recipe units: ${deficitInRecipeUnit} ${need.unit}`);
          console.log(`  💰 Cost: ${deficitInRecipeUnit} × $${need.costPerUnit} = $${(deficitInRecipeUnit * need.costPerUnit).toFixed(2)}`);
          console.log(`  ➡️ ADDING to shopping list`);
          
          shoppingList.push({
            id: `${need.name}-${need.unit}`,
            name: need.name,
            quantity: Math.round(deficitInRecipeUnit * 10) / 10,
            unit: need.unit,
            unitCost: need.costPerUnit,
            totalCost: Math.round(deficitInRecipeUnit * need.costPerUnit * 100) / 100
          });
        }
      } else {
        console.log(`❌ NOT in pantry at all`);
        console.log(`  💰 Cost: ${need.quantity} × $${need.costPerUnit} = $${(need.quantity * need.costPerUnit).toFixed(2)}`);
        console.log(`  ➡️ ADDING full amount to shopping list`);
        
        shoppingList.push({
          id: `${need.name}-${need.unit}`,
          name: need.name,
          quantity: Math.round(need.quantity * 10) / 10,
          unit: need.unit,
          unitCost: need.costPerUnit,
          totalCost: Math.round(need.quantity * need.costPerUnit * 100) / 100
        });
      }
    });

    console.log('\n\n═══════════════════════════════════════════════════');
    console.log('✅ FINAL SHOPPING LIST');
    console.log('═══════════════════════════════════════════════════');
    
    if (shoppingList.length === 0) {
      console.log('🎉 Nothing to buy! You have everything in your pantry!\n');
    } else {
      console.log(`\n📝 Items to buy: ${shoppingList.length}\n`);
      shoppingList.forEach((item, i) => {
        console.log(`  ${i + 1}. ${item.name}: ${item.quantity} ${item.unit} @ $${item.unitCost}/${item.unit} = $${item.totalCost}`);
      });
      const total = shoppingList.reduce((sum, item) => sum + item.totalCost, 0);
      console.log(`\n💵 Total shopping cost: $${total.toFixed(2)}\n`);
    }

    console.log('═══════════════════════════════════════════════════\n');
    
    return shoppingList;
  };

  return {
    shoppingList,
    pantry,
    loading,
    error,
    refreshShopping: loadShoppingData,
  };
};

export default useShopping;