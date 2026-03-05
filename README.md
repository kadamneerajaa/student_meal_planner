# Student Meal Planner 
## Overview

The Student Meal Planner is a full-stack application that combines a Java backend with a React frontend to provide students with an intelligent meal planning system. Students can manage their meal library, plan weekly meals, track pantry inventory, generate shopping lists, and monitor nutrition goals and spending.

**Problem Statement:**
Many students struggle with meal planning, leading to poor dietary choices, food waste, and budget overruns. This application solves these challenges by providing a centralized system to manage recipes, track ingredients, generate shopping lists, and maintain nutritional balance.

## Features

### 1. Meal Management
- Browse meal library with search and filtering
- Create custom meals with nutritional information
- Edit existing meals
- Delete meals with confirmation
- Quick meal filtering (5, 15, 30 minutes)
- Sort by calories, cost, protein, or name

### 2. Weekly Planning
- Visual weekly grid (Monday-Sunday)
- Assign meals to breakfast, lunch, dinner, or quick meals
- Daily detail view with summary
- Remove meals from plan
- Real-time calorie and cost tracking per day

### 3. Shopping & Pantry
- **Pantry Inventory Management:**
  - Add/edit/delete pantry items
  - Track quantity and cost per unit
  - Case-insensitive duplicate checking
  
- **Smart Shopping List Generation:**
  - Auto-generates from weekly plan
  - Aggregates ingredients across all meals
  - Compares with pantry inventory
  - Unit conversion (dozen ↔ count, gallon ↔ cup)
  - Shows only missing ingredients
  - Budget tracking with progress bar

### 4. Reports & Analytics
- **Daily Summary:**
  - Total calories, protein, carbs, fat
  - Goal achievement indicators
  - Daily cost tracking
  
- **Weekly Summary:**
  - Aggregated nutrition data for the week
  - Budget vs. spending comparison
  - Goal progress visualization
  
- **Interactive Charts:**
  - Nutrition bar chart (daily breakdown)
  - Cost pie chart (spending by day)

### 5. Student Profile
- Personal information
- Dietary preference settings
- Daily nutrition goals (calories, macros)
- Weekly budget configuration

---

##  Technology Stack
### Frontend
- **Framework:** React 18
- **Build Tool:** Vite
- **Routing:** React Router DOM
- **HTTP Client:** Axios
- **Charts:** Recharts
- **Icons:** Lucide React
- **Styling:** Custom CSS with CSS Variables

### Backend
- **Language:** Java
- **Server:** Java HttpServer
- **Data Storage:** CSV Files
- **Architecture:** Layered (Handler → Service → Repository)

### Integration
- **API:** RESTful architecture
- **CORS:** Custom CORS handler
- **Proxy:** Vite dev server proxy
- **Data Format:** JSON

```
final-project-final-project-team-3/
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/          # Reusable UI components
│   │   │   │   ├── Button.jsx
│   │   │   │   ├── Card.jsx
│   │   │   │   ├── Input.jsx
│   │   │   │   ├── Modal.jsx
│   │   │   │   ├── Loader.jsx
│   │   │   │   └── ErrorMessage.jsx
│   │   │   ├── meals/           # Meal-related components
│   │   │   │   ├── MealCard.jsx
│   │   │   │   ├── MealForm.jsx
│   │   │   │   ├── MealList.jsx
│   │   │   │   ├── MealSearchBar.jsx
│   │   │   │   ├── MealSortDropdown.jsx
│   │   │   │   └── QuickMealFilter.jsx
│   │   │   ├── planning/        # Planning components
│   │   │   │   ├── DailyView.jsx
│   │   │   │   ├── WeeklyPlanGrid.jsx
│   │   │   │   └── DayPlanCard.jsx
│   │   │   ├── shopping/        # Shopping components
│   │   │   │   ├── ShoppingList.jsx
│   │   │   │   ├── ShoppingItem.jsx
│   │   │   │   ├── PantryManager.jsx
│   │   │   │   └── BudgetTracker.jsx
│   │   │   └── reports/         # Report components
│   │   │       ├── DailySummaryCard.jsx
│   │   │       ├── WeeklySummaryCard.jsx
│   │   │       ├── GoalProgress.jsx
│   │   │       ├── NutritionChart.jsx
│   │   │       └── CostBreakdown.jsx
│   │   ├── context/             # State management
│   │   │   ├── MealContext.jsx
│   │   │   ├── PlanContext.jsx
│   │   │   └── StudentContext.jsx
│   │   ├── hooks/               # Custom hooks
│   │   │   ├── useShopping.jsx
│   │   │   └── useReports.jsx
│   │   ├── pages/               # Main pages
│   │   │   ├── HomePage.jsx
│   │   │   ├── MealLibraryPage.jsx
│   │   │   ├── CreateMealPage.jsx
│   │   │   ├── WeeklyPlanPage.jsx
│   │   │   ├── ShoppingPage.jsx
│   │   │   ├── ReportsPage.jsx
│   │   │   └── ProfilePage.jsx
│   │   ├── utils/               # Utilities
│   │   │   ├── api.js           # API integration layer
│   │   │   ├── calculations.js
│   │   │   ├── formatters.js
│   │   │   └── validation.js
│   │   ├── styles/
│   │   │   ├── global.css
│   │   │   └── variables.css
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── public/
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
└── src/                         # Backend (Java)
    ├── main/java/edu/neu/csye6200/
    │   ├── data/                # CSV Repositories
    │   │   ├── IngredientCostRepository.java
    │   │   ├── MealCsvRepository.java
    │   │   ├── PantryCsvRepository.java
    │   │   └── PlanCsvRepository.java
    │   ├── factory/             # Factory pattern
    │   │   └── MealFactory.java
    │   ├── model/               # Data models
    │   │   ├── DailySummary.java
    │   │   ├── DayPlan.java
    │   │   ├── Ingredient.java
    │   │   ├── Meal.java
    │   │   ├── Pantry.java
    │   │   ├── QuickMeal.java
    │   │   ├── RegularMeal.java
    │   │   ├── ShoppingItem.java
    │   │   ├── ShoppingList.java
    │   │   ├── Student.java
    │   │   ├── WeeklyPlan.java
    │   │   └── WeeklySummary.java
    │   ├── server/              # HTTP server
    │   │   ├── handlers/
    │   │   │   ├── MealHandler.java
    │   │   │   ├── PlanHandler.java
    │   │   │   ├── ReportHandler.java
    │   │   │   ├── ShoppingHandler.java
    │   │   │   └── StudentHandler.java
    │   │   └── CorsHandler.java
    │   └── service/             # Business logic
    │       ├── MealService.java
    │       ├── PlanService.java
    │       ├── ReportService.java
    │       └── ShoppingService.java
    └── data/                    # CSV data files
        ├── meals.csv
        ├── pantry.csv
        ├── weekly_plans.csv
        └── ingredient_costs.csv
```

---

## Setup Instructions

### Prerequisites
- Java JDK 11 or higher
- Node.js 16 or higher
- npm or yarn

### Backend Setup

1. **Navigate to backend directory:**
```bash
   cd src/main/java/edu/neu/csye6200
```

2. **Compile and run the Java server:**
```bash
   javac -d bin src/**/*.java
   java -cp bin edu.neu.csye6200.server.Server
```
   
   The server will start on `http://localhost:8080`

3. **Verify backend is running:**
```bash
   curl http://localhost:8080/api/meals
```

### Frontend Setup

1. **Navigate to frontend directory:**
```bash
   cd frontend
```

2. **Install dependencies:**
```bash
   npm install
```

3. **Start development server:**
```bash
   npm run dev
```
   
   The app will open at `http://localhost:3000`

4. **Build for production (optional):**
```bash
   npm run build
```

## Usage Guide

### Getting Started

1. **Launch both servers:**
   - Backend: `http://localhost:8080`
   - Frontend: `http://localhost:3000`

2. **Set up your profile:**
   - Navigate to Profile page
   - Set dietary preferences, nutrition goals, and budget

3. **Browse meals:**
   - Go to Meals page
   - Search, filter, or create custom meals

4. **Plan your week:**
   - Go to Plan page
   - Click on a day
   - Add meals to breakfast, lunch, dinner

5. **Check shopping list:**
   - Go to Shopping page
   - Add items to pantry
   - Shopping list auto-generates based on plan

6. **View analytics:**
   - Go to Reports page
   - See daily and weekly nutrition summaries
   - Track goal progress

---

## Application Flow
```
1. MEALS → Create/Browse meal library
            ↓
2. PLAN  → Assign meals to days
            ↓
3. SHOPPING → Auto-generate shopping list
              (checks pantry, converts units)
            ↓
4. REPORTS → View nutrition & budget analytics
```

---

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Endpoints

#### Meals
- `GET /meals` - Get all meals
- `GET /meals/:id` - Get meal by ID
- `POST /meals` - Create new meal
- `PUT /meals/:id` - Update meal
- `DELETE /meals/:id` - Delete meal
- `GET /meals/search?name={name}` - Search meals
- `GET /meals/quick?prepTime={time}` - Get quick meals

#### Plans
- `GET /plans` - Get weekly plan
- `POST /plans` - Create/update weekly plan
- `GET /plans/day/:day` - Get specific day plan
- `PUT /plans/day/:day` - Update day plan

#### Shopping
- `GET /shopping/list` - Get shopping list
- `GET /shopping/pantry` - Get pantry items
- `POST /shopping/pantry` - Add pantry item
- `PUT /shopping/pantry/:name` - Update pantry item
- `DELETE /shopping/pantry/:name` - Delete pantry item

#### Reports
- `GET /reports/daily?day={day}` - Get daily summary
- `GET /reports/weekly` - Get weekly summary

#### Student
- `GET /student` - Get student profile
- `PUT /student` - Update student profile

---

##  Design Patterns Used

### Backend
- **Singleton Pattern:** AppManager for centralized service access
- **Factory Pattern:** MealFactory for creating Meal objects
- **Repository Pattern:** CSV data access abstraction
- **Service Layer Pattern:** Business logic separation

### Frontend
- **Context Pattern:** Global state management
- **Custom Hooks Pattern:** Reusable stateful logic
- **Component Composition:** Building complex UIs from simple components
- **Container/Presentational:** Separation of logic and UI


## User Workflow

### Complete Journey

1. **Setup Profile**
   - Navigate to Profile
   - Enter name, dietary preference
   - Set calorie goal: 2000/day
   - Set macro goals: Protein 150g, Carbs 250g, Fat 65g
   - Set weekly budget: $100

2. **Manage Meals**
   - Go to Meals page
   - Browse existing meals or create new ones
   - Search for "Chicken" or filter by "5 min" quick meals
   - Edit or delete meals as needed

3. **Plan Your Week**
   - Go to Plan page
   - Click Monday → Add Meal → Select "Oatmeal with Berries"
   - Repeat for other days and meal slots
   - See daily summaries update in real-time

4. **Manage Pantry**
   - Go to Shopping page
   - Click "Add Item" in Pantry section
   - Add: Eggs (1 dozen), Milk (0.5 gallon)

5. **Generate Shopping List**
   - Shopping list automatically shows ingredients needed
   - Items already in pantry are excluded
   - See total cost and budget status

6. **Track Progress**
   - Go to Reports page
   - Select a day to see daily summary
   - View charts showing weekly trends
   - Check if nutrition goals are being met

---

## Input Validation

### Frontend Validation
- Required fields enforcement
- No negative values (calories, cost, macros)
- Case-insensitive duplicate checking (pantry)
- Number format validation
- Empty field handling

### Backend Validation
- Null checks
- Data type validation
- Business rule enforcement


##  Error Handling

### Frontend
- Try-catch blocks on all API calls
- User-friendly error messages
- Retry buttons for failed operations
- Loading states during async operations
- Graceful fallbacks

### Backend
- Exception handling in all handlers
- Appropriate HTTP status codes
- Detailed error messages in responses

---

## Performance Optimizations

- **Optimistic UI Updates:** Update local state before backend confirmation
- **Lazy Loading:** Components load only when needed
- **Memoization:** Prevent unnecessary re-renders
- **Dependency Tracking:** useEffect only runs when data changes
- **Efficient Filtering:** Client-side filtering for instant results

---

## 🧮 Business Logic Examples

### Shopping List Calculation
```javascript
// Pseudocode for shopping list generation
function generateShoppingList(weeklyPlan, pantry) {
  // 1. Collect all meals from plan
  const allMeals = weeklyPlan.days.flatMap(day => 
    [day.breakfast, day.lunch, day.dinner, ...day.quickMeals]
  );
  
  // 2. Aggregate ingredients
  const ingredientNeeds = {};
  allMeals.forEach(meal => {
    meal.ingredients.forEach(ing => {
      ingredientNeeds[ing.name] = (ingredientNeeds[ing.name] || 0) + ing.quantity;
    });
  });
  
  // 3. Compare with pantry
  const shoppingList = [];
  for (const [name, needed] of Object.entries(ingredientNeeds)) {
    const pantryItem = pantry.find(p => p.name === name);
    const have = convertToBaseUnit(pantryItem.quantity, pantryItem.unit);
    const need = convertToBaseUnit(needed, unit);
    
    if (need > have) {
      shoppingList.push({ name, quantity: need - have });
    }
  }
  
  return shoppingList;
}
```

### Daily Summary Calculation
```javascript
function calculateDailySummary(dayPlan) {
  const meals = [dayPlan.breakfast, dayPlan.lunch, dayPlan.dinner, ...dayPlan.quickMeals];
  
  return {
    totalCalories: meals.reduce((sum, m) => sum + m.calories, 0),
    totalProtein: meals.reduce((sum, m) => sum + m.protein, 0),
    totalCarbs: meals.reduce((sum, m) => sum + m.carbs, 0),
    totalFat: meals.reduce((sum, m) => sum + m.fat, 0),
    totalCost: meals.reduce((sum, m) => sum + m.cost, 0)
  };
}
```
---

## Object-Oriented Concepts Demonstrated

### Backend (Java)
- **Encapsulation:** Private fields with getters/setters
- **Inheritance:** Meal → RegularMeal, QuickMeal
- **Polymorphism:** Working with Meal abstraction
- **Abstraction:** Service layer hides implementation details
- **Factory Pattern:** MealFactory creates appropriate meal types
- **Singleton Pattern:** Centralized service management

### Frontend (React)
- **Component-Based Architecture:** Reusable, composable components
- **State Encapsulation:** Context providers hide implementation
- **Separation of Concerns:** Components, logic, and data layers
- **Composition over Inheritance:** Building complex UIs from simple parts

---

## 📝 Data Storage

### CSV File Format

**meals.csv:**
```csv
id,name,mealType,dietaryPreference,calories,protein,carbs,fat,cost,ingredients,favoriteFlag,prepTimeCategory
B-001,Oatmeal with Berries,BREAKFAST,Vegetarian,350,15,55,8,4.5,Oats|Berries|Milk,false,
```

**pantry.csv:**
```csv
name,unit,quantity,costPerUnit
Eggs,dozen,1,3.5
Milk,gallon,0.5,4.0
```

**weekly_plans.csv:**
```csv
studentId,dayName,breakfastId,lunchId,dinnerId,quickMealIds
S001,Monday,B-001,L-001,D-001,
```