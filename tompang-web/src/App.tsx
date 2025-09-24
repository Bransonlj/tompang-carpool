import { BrowserRouter } from 'react-router';
import { SocketProvider } from './context/socket-context'
import Router from './router';
import {
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query';
import { AuthProvider } from './context/auth-context';

function App() {

  const queryClient = new QueryClient();

  return (
    <>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <SocketProvider>
            <BrowserRouter>
              <Router />
            </BrowserRouter>
          </SocketProvider>
        </AuthProvider>
      </QueryClientProvider>
    </>
  )
}

export default App
