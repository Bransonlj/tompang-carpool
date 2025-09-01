import { createContext, useContext, useMemo, useRef, useState, type ReactNode } from "react";
import { io, Socket } from 'socket.io-client';

const URL = 'http://localhost:4500'; // TODO: Update this

interface SocketContextInterface {
  socket: Socket | null;
  isConnected: boolean;
  connect: (userId: string) => void;
  disconnect: () => void;
}

const SocketContext = createContext<SocketContextInterface>({
  socket: null,
  isConnected: false,
  connect: () => {},
  disconnect: () => {}
});

export function useSocket() {
  return useContext(SocketContext);
}

export function SocketProvider({ children }: { children: ReactNode}) {
  const [isConnected, setIsConnected] = useState<boolean>(false);
  const socketRef = useRef<Socket>(null);

  function handleConnect() {
    console.log("connected");
    setIsConnected(true);
  }
  function handleDisonnect() {
    console.log("disconnected");
    setIsConnected(false);
  }

  function handleError(err: any) {
    console.log(`connection error: ${err.message}`);
  }

  function connect(token: string) {
    socketRef.current = io(URL, {
      autoConnect: false, // connect on login
      extraHeaders: {
        Authorization: `Bearer ${token}`, // pass jwt
      },
    });

    socketRef.current?.on("connect_error", handleError)
    socketRef.current?.on("connect", handleConnect)
    socketRef.current?.on("disconnect", handleDisonnect)
    socketRef.current.connect();
  }

  function disconnect() {
    socketRef.current?.off("connect", handleConnect)
    socketRef.current?.off("connect_error", handleError)
    socketRef.current?.off("disconnect", handleDisonnect)
    socketRef.current?.disconnect();
    socketRef.current = null;
  }

  const value = useMemo(() => ({
    socket: socketRef.current,
    isConnected,
    connect,
    disconnect,
  }), [socketRef.current, isConnected, connect, disconnect]);

  return (
    <SocketContext.Provider value={value}>
      {children}
    </SocketContext.Provider>
  )
}