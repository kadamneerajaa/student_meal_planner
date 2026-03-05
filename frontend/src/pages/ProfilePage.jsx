import { useState } from 'react';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import Input from '../components/common/Input';
import { useStudent } from '../context/StudentContext';
import { validateStudent } from '../utils/validation';

const ProfilePage = () => {
  const { student, updateStudentProfile } = useStudent();
  const [formData, setFormData] = useState({
    name: student?.name || '',
    dietaryPreference: student?.dietaryPreference || '',
    dailyCalorieGoal: student?.dailyCalorieGoal || 2000,
    proteinGoal: student?.proteinGoal || 0,
    carbsGoal: student?.carbsGoal || 0,
    fatsGoal: student?.fatsGoal || 0,
    weeklyBudget: student?.weeklyBudget || 0,
  });
  const [errors, setErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState('');

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'number' ? parseFloat(value) || 0 : value,
    }));
    setSuccessMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationErrors = validateStudent(formData);

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    try {
      await updateStudentProfile(formData);
      setErrors({});
      setSuccessMessage('Profile updated successfully!');
    } catch (err) {
      setErrors({ submit: 'Failed to update profile. Please try again.' });
    }
  };

  return (
    <div className="profile-sections" style={{ maxWidth: '64rem', margin: '0 auto' }}>
      <h1 className="text-3xl font-bold">Student Profile</h1>

      {successMessage && (
        <div className="success-banner">
          <p style={{ color: '#065f46', fontWeight: 500 }}>✓ {successMessage}</p>
        </div>
      )}

      {errors.submit && (
        <div className="error-banner">
          <p style={{ color: '#991b1b', fontWeight: 500 }}>{errors.submit}</p>
        </div>
      )}

      <Card title="Personal Information">
        <form onSubmit={handleSubmit}>
          <Input
            label="Name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            error={errors.name}
            required
          />

          <div className="input-group">
            <label className="input-label">
              Dietary Preference
            </label>
            <select
              name="dietaryPreference"
              value={formData.dietaryPreference}
              onChange={handleChange}
              className="input-field"
            >
              <option value="">None</option>
              <option value="vegetarian">Vegetarian</option>
              <option value="vegan">Vegan</option>
              <option value="pescatarian">Pescatarian</option>
              <option value="keto">Keto</option>
              <option value="paleo">Paleo</option>
            </select>
          </div>
        </form>
      </Card>

      <Card title="Daily Nutrition Goals">
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 grid-md-cols-2">
            <Input
              label="Daily Calorie Goal"
              type="number"
              name="dailyCalorieGoal"
              value={formData.dailyCalorieGoal}
              onChange={handleChange}
              error={errors.dailyCalorieGoal}
              required
            />

            <Input
              label="Protein Goal (g)"
              type="number"
              name="proteinGoal"
              value={formData.proteinGoal}
              onChange={handleChange}
              error={errors.proteinGoal}
            />

            <Input
              label="Carbs Goal (g)"
              type="number"
              name="carbsGoal"
              value={formData.carbsGoal}
              onChange={handleChange}
              error={errors.carbsGoal}
            />

            <Input
              label="Fats Goal (g)"
              type="number"
              name="fatsGoal"
              value={formData.fatsGoal}
              onChange={handleChange}
              error={errors.fatsGoal}
            />
          </div>
        </form>
      </Card>

      <Card title="Budget">
        <form onSubmit={handleSubmit}>
          <Input
            label="Weekly Budget ($)"
            type="number"
            step="0.01"
            name="weeklyBudget"
            value={formData.weeklyBudget}
            onChange={handleChange}
            error={errors.weeklyBudget}
            required
          />

          <div className="flex justify-end mt-3">
            <Button type="submit" variant="primary" onClick={handleSubmit}>
              Save Changes
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};

export default ProfilePage;