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
          <AuthProvider>
            <SocketProvider>
              <BrowserRouter>
                <Router />
              </BrowserRouter>
              <Toaster />
            </SocketProvider>
          </AuthProvider>
        </QueryClientProvider>
      </LocalizationProvider>
    </>
  )
}

export default App
