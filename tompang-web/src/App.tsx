import { SocketProvider } from './context/socket-context'
import TestSocket from './TestSocket'

function App() {

  return (
    <>
      <SocketProvider>
        <TestSocket />
      </SocketProvider>
    </>
  )
}

export default App
