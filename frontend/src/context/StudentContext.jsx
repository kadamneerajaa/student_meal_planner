import { createContext, useContext, useEffect, useState } from 'react';
import { fetchStudent, updateStudent } from '../utils/api';

const StudentContext = createContext();

export const useStudent = () => {
  const context = useContext(StudentContext);
  if (!context) {
    throw new Error('useStudent must be used within StudentProvider');
  }
  return context;
};

export const StudentProvider = ({ children }) => {
  const [student, setStudent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadStudent();
  }, []);

  const loadStudent = async () => {
    try {
      setLoading(true);
      const data = await fetchStudent();
      setStudent(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('Error loading student:', err);
    } finally {
      setLoading(false);
    }
  };

  const updateStudentProfile = async (studentData) => {
    try {
      const updated = await updateStudent(studentData);
      setStudent(updated);
      return updated;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const value = {
    student,
    loading,
    error,
    updateStudentProfile,
    refreshStudent: loadStudent,
  };

  return <StudentContext.Provider value={value}>{children}</StudentContext.Provider>;
};