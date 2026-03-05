const MealSortDropdown = ({ sortBy, onSortChange }) => {
  return (
    <div className="input-group">
      <label className="input-label">Sort By</label>
      <select
        value={sortBy}
        onChange={(e) => onSortChange(e.target.value)}
        className="input-field"
      >
        <option value="name">Name</option>
        <option value="calories">Calories</option>
        <option value="cost">Cost</option>
        <option value="protein">Protein</option>
      </select>
    </div>
  );
};

export default MealSortDropdown;