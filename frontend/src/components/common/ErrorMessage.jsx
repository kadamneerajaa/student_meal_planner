import { AlertCircle } from 'lucide-react';

const ErrorMessage = ({ message, onRetry }) => {
  return (
    <div className="error-message">
      <div className="error-message-content">
        <AlertCircle className="error-message-icon" size={20} />
        <div className="error-message-body">
          <p className="error-message-title">Error</p>
          <p className="error-message-text">{message}</p>
          {onRetry && (
            <button onClick={onRetry} className="error-message-retry-btn">
              Try Again
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default ErrorMessage;