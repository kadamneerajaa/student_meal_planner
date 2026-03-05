import { Check } from 'lucide-react';
import { formatCurrency } from '../../utils/formatters';

const ShoppingItem = ({ item, onToggle }) => {
  // Use totalCost first, then fall back to cost
  const itemCost = item.totalCost || item.cost || 0;
  
  return (
    <div className={`shopping-item ${item.checked ? 'checked' : ''}`}>
      <div className="shopping-item-content">
        <button
          onClick={() => onToggle(item.id)}
          className={`shopping-item-checkbox ${item.checked ? 'checked' : ''}`}
        >
          {item.checked && <Check size={16} style={{ color: 'white' }} />}
        </button>

        <div className="shopping-item-info">
          <p className="shopping-item-name">{item.name}</p>
          <p className="shopping-item-quantity">
            {item.quantity} {item.unit}
          </p>
        </div>
      </div>

      <div className={`shopping-item-cost ${item.checked ? 'checked' : ''}`} style={{ color: 'var(--color-success)' }}>
        {formatCurrency(itemCost)}
      </div>
    </div>
  );
};

export default ShoppingItem;