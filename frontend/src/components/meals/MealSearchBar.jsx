import { Search } from 'lucide-react';
import { useState } from 'react';

const MealSearchBar = ({ onSearch }) => {
  const [query, setQuery] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSearch(query);
  };

  return (
    <div className="search-wrapper">
      <form onSubmit={handleSubmit} style={{ position: 'relative' }}>
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search meals by name..."
          className="input-field"
          style={{ paddingLeft: '2.5rem' }}
        />
        <Search 
          className="search-icon"
          size={20} 
        />
      </form>
    </div>
  );
};

export default MealSearchBar;