const Card = ({ children, title, className = '', onClick }) => {
  const classes = `card ${onClick ? 'card-clickable' : ''} ${className}`;

  return (
    <div className={classes} onClick={onClick}>
      {title && <h3 className="card-title">{title}</h3>}
      <div className="card-body">{children}</div>
    </div>
  );
};

export default Card;