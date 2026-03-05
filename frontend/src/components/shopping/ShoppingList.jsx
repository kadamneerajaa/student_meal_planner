import { formatCurrency } from '../../utils/formatters';
import Card from '../common/Card';
import ShoppingItem from './ShoppingItem';

const ShoppingList = ({ items, onToggleItem, totalCost }) => {
  const checkedItems = items?.filter(item => item.checked) || [];
  const uncheckedItems = items?.filter(item => !item.checked) || [];

  return (
    <Card title="Shopping List">
      <div className="shopping-list-header">
        <div className="shopping-list-total">
          <span className="font-semibold">Total Cost:</span>
          <span className="shopping-list-total-amount">
            {formatCurrency(totalCost || 0)}
          </span>
        </div>
        <div className="text-sm mt-2" style={{ color: 'var(--text-secondary)' }}>
          {checkedItems.length} of {items?.length || 0} items checked
        </div>
      </div>

      <div>
        {uncheckedItems.length > 0 && (
          <div>
            <h4 className="font-semibold mb-2" style={{ color: '#374151' }}>To Buy</h4>
            {uncheckedItems.map((item) => (
              <ShoppingItem
                key={item.id}
                item={item}
                onToggle={onToggleItem}
              />
            ))}
          </div>
        )}

        {checkedItems.length > 0 && (
          <div className="mt-4">
            <h4 className="font-semibold mb-2" style={{ color: '#374151' }}>Checked Off</h4>
            {checkedItems.map((item) => (
              <ShoppingItem
                key={item.id}
                item={item}
                onToggle={onToggleItem}
              />
            ))}
          </div>
        )}

        {(!items || items.length === 0) && (
          <div className="text-center" style={{ padding: '2rem', color: '#9ca3af' }}>
            No items in shopping list
          </div>
        )}
      </div>
    </Card>
  );
};

export default ShoppingList;