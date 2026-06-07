// src/main.tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import {ClerkProvider} from '@clerk/react';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ClerkProvider>
      <App />
    </ClerkProvider>
  </React.StrictMode>
);
