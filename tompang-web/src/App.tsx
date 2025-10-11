import { BrowserRouter } from 'react-router';
import { SocketProvider } from './context/socket-context'
import Router from './router';
import {
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query';
import { AuthProvider } from './context/auth-context';
import { Toaster } from 'react-hot-toast';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { NotificationProvider } from './context/notification-context';

function App() {

  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        refetchOnWindowFocus: false,
      }
    }
  });

  return (
    <>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <QueryClientProvider client={queryClient}>
          <SocketProvider>
            <AuthProvider>
              <NotificationProvider>
                <BrowserRouter>
                  <Router />
                </BrowserRouter>
                <Toaster />
                <Toaster toasterId="notification" position="bottom-right" reverseOrder={false} toastOptions={{duration: 8000}} />
              </NotificationProvider>
            </AuthProvider>
          </SocketProvider>
        </QueryClientProvider>
      </LocalizationProvider>
    </>
  )
}

export default App
