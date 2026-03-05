import { Edit2, Plus, Trash2 } from 'lucide-react';
import { useState } from 'react';
import Button from '../common/Button';
import Card from '../common/Card';
import Input from '../common/Input';
import Modal from '../common/Modal';

const PantryManager = ({ pantryItems, onAdd, onUpdate, onDelete }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    unit: '',
    quantity: 0,
    costPerUnit: 0,
  });
  const [error, setError] = useState('');

  const handleOpenModal = (item = null) => {
    if (item) {
      setEditingItem(item);
      setFormData({
        name: item.name,
        unit: item.unit,
        quantity: item.quantity,
        costPerUnit: item.costPerUnit
      });
    } else {
      setEditingItem(null);
      setFormData({ name: '', unit: '', quantity: 0, costPerUnit: 0 });
    }
    setError('');
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingItem(null);
    setFormData({ name: '', unit: '', quantity: 0, costPerUnit: 0 });
    setError('');
  };

  const checkDuplicateName = (name) => {
    // Case-insensitive duplicate check
    const normalizedName = name.trim().toLowerCase();
    
    return pantryItems.some(item => {
      const itemName = item.name.trim().toLowerCase();
      // If editing, exclude the current item from duplicate check
      if (editingItem && item.id === editingItem.id) {
        return false;
      }
      return itemName === normalizedName;
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    // Validate name
    if (!formData.name || formData.name.trim() === '') {
      setError('Please enter an item name');
      return;
    }

    // Check for duplicates (only when adding new items)
    if (!editingItem && checkDuplicateName(formData.name)) {
      setError(`Item "${formData.name}" already exists! Please edit the existing item instead.`);
      return;
    }

    // Validate numbers
    if (formData.quantity <= 0) {
      setError('Quantity must be greater than 0');
      return;
    }

    if (formData.costPerUnit < 0) {
      setError('Cost per unit cannot be negative');
      return;
    }

    try {
      if (editingItem) {
        await onUpdate(editingItem.id, formData);
      } else {
        await onAdd(formData);
      }
      handleCloseModal();
    } catch (err) {
      setError(err.message || 'Failed to save item');
    }
  };

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'number' ? parseFloat(value) || 0 : value,
    }));
    // Clear error when user starts typing
    if (error) setError('');
  };

  const handleDeleteWithConfirm = async (item) => {
    if (window.confirm(`Are you sure you want to delete "${item.name}"?`)) {
      try {
        await onDelete(item.id);
      } catch (err) {
        alert('Failed to delete item: ' + err.message);
      }
    }
  };

  return (
    <>
      <Card title="Pantry Inventory">
        <Button onClick={() => handleOpenModal()} variant="primary" className="mb-3">
          <Plus size={20} />
          Add Item
        </Button>

        <div>
          {pantryItems && pantryItems.length > 0 ? (
            pantryItems.map((item) => (
              <div
                key={item.id}
                className="flex items-center justify-between"
                style={{ 
                  padding: '0.75rem',
                  backgroundColor: '#f9fafb',
                  borderRadius: 'var(--radius-lg)',
                  marginBottom: '0.5rem'
                }}
              >
                <div>
                  <p className="font-medium">{item.name}</p>
                  <p className="text-sm" style={{ color: 'var(--text-secondary)' }}>
                    {item.quantity} {item.unit} • ${item.costPerUnit}/{item.unit}
                  </p>
                </div>
                <div className="flex" style={{ gap: '0.5rem' }}>
                  <button
                    onClick={() => handleOpenModal(item)}
                    style={{ 
                      padding: '0.5rem',
                      color: 'var(--color-primary)',
                      background: 'none',
                      border: 'none',
                      cursor: 'pointer',
                      borderRadius: 'var(--radius-md)'
                    }}
                    title="Edit item"
                  >
                    <Edit2 size={18} />
                  </button>
                  <button
                    onClick={() => handleDeleteWithConfirm(item)}
                    style={{ 
                      padding: '0.5rem',
                      color: 'var(--color-danger)',
                      background: 'none',
                      border: 'none',
                      cursor: 'pointer',
                      borderRadius: 'var(--radius-md)'
                    }}
                    title="Delete item"
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
              </div>
            ))
          ) : (
            <div className="text-center" style={{ padding: '2rem', color: '#9ca3af' }}>
              No items in pantry. Click "Add Item" to get started!
            </div>
          )}
        </div>
      </Card>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={editingItem ? 'Edit Pantry Item' : 'Add Pantry Item'}
        footer={
          <>
            <Button variant="secondary" onClick={handleCloseModal}>
              Cancel
            </Button>
            <Button variant="primary" onClick={handleSubmit}>
              {editingItem ? 'Update' : 'Add'}
            </Button>
          </>
        }
      >
        <form onSubmit={handleSubmit}>
          {error && (
            <div style={{
              padding: '12px',
              marginBottom: '16px',
              backgroundColor: '#fee',
              border: '1px solid #fcc',
              borderRadius: '6px',
              color: '#c33'
            }}>
              {error}
            </div>
          )}

          <Input
            label="Item Name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="e.g., Rice, Milk, Eggs"
            required
            disabled={editingItem !== null} // Disable name editing
          />
          
          {editingItem && (
            <p style={{ fontSize: '0.875rem', color: '#6b7280', marginTop: '-8px', marginBottom: '12px' }}>
              Note: Item name cannot be changed. Delete and create new if needed.
            </p>
          )}

          <div className="grid grid-cols-2" style={{ gap: '1rem' }}>
            <Input
              label="Quantity"
              type="number"
              name="quantity"
              value={formData.quantity}
              onChange={handleChange}
              min="0.01"
              step="0.01"
              required
            />
            <Input
              label="Unit"
              name="unit"
              value={formData.unit}
              onChange={handleChange}
              placeholder="e.g., lbs, oz, count"
              required
            />
          </div>
          <Input
            label="Cost Per Unit"
            type="number"
            step="0.01"
            name="costPerUnit"
            value={formData.costPerUnit}
            onChange={handleChange}
            min="0"
            required
          />
        </form>
      </Modal>
    </>
  );
};

export default PantryManager;