// src/components/ErrorPage.tsx
import React, { useEffect } from 'react';
import { Container, Typography } from '@mui/material';

const ErrorPage: React.FC = () => {
  useEffect(() => {
    alert('An error occurred. Redirecting back.');
    window.history.back();
  }, []);

  return (
    <Container>
      <Typography variant="h4">Error Page</Typography>
    </Container>
  );
};

export default ErrorPage;
