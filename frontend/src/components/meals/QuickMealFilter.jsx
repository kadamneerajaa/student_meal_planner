const QuickMealFilter = ({ selectedTime, onTimeChange }) => {
  const timeOptions = [
    { value: 5, label: '5 min' },
    { value: 15, label: '15 min' },
    { value: 30, label: '30 min' },
  ];

  return (
    <div>
      <label className="input-label">Quick Meals</label>
      <div className="flex" style={{ gap: '0.5rem' }}>
        {timeOptions.map((option) => (
          <button
            key={option.value}
            type="button"
            onClick={() => onTimeChange(option.value)}
            className="btn"
            style={{
              backgroundColor: selectedTime === option.value ? 'var(--color-primary)' : '#f3f4f6',
              color: selectedTime === option.value ? 'white' : '#374151',
            }}
          >
            {option.label}
          </button>
        ))}
        {selectedTime && (
          <button
            type="button"
            onClick={() => onTimeChange(null)}
            className="btn btn-secondary"
          >
            Clear
          </button>
        )}
      </div>
    </div>
  );
};

export default QuickMealFilter;