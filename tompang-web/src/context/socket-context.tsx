import { createContext, useContext, useMemo, useRef, useState, type ReactNode } from "react";
import { io, Socket } from 'socket.io-client';

const URL =  import.meta.env.VITE_WEBSOCKET_URL;

interface SocketContextInterface {
  socket: Socket | null;
  isConnected: boolean;
  connect: (authToken: string) => void;
  disconnect: () => void;
}

const SocketContext = createContext<SocketContextInterface | undefined>(undefined);

export const SocketEvent = {
  NOTIFICATION: "notification",
  CHAT_MESSAGE: "chat-message",
} as const;

export function useSocket() {
  const context = useContext(SocketContext);
  if (context === undefined) {
    throw new Error('useSocket must be used within an SocketProvider');
  }
  return context;
}

export function SocketProvider({ children }: { children: ReactNode}) {
  const [isConnected, setIsConnected] = useState<boolean>(false);
  const socketRef = useRef<Socket>(null);

  function handleConnect() {
    console.log("socket connected");
    setIsConnected(true);
  }
  function handleDisonnect() {
    console.log("socket disconnected");
    setIsConnected(false);
  }

  function handleError(err: any) {
    console.log(`socket connection error: ${err.message}`);
  }

  function connect(authToken: string) {
    console.log("Connect called")
    socketRef.current = io(URL, {
      autoConnect: false, // connect manually on login
      extraHeaders: {
        Authorization: `Bearer ${authToken}`, // pass jwt
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