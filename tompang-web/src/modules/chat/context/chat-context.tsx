import { createContext, useContext, useEffect, type ReactNode } from "react";
import toast from "react-hot-toast";
import IconButton from "@mui/material/IconButton";
import { X } from "lucide-react";
import { SocketEvent, useSocket } from "../../../context/socket-context";
import { Link } from "react-router";

type ChatMessagePayload = {
  groupChatId: string;
  senderUserId: string;
  createdAt: string; //iso-date string
  messageId: string;
  message: string;
}

interface ChatContextValue {
  /**
   * Register a callback when chat-message socket event is received. 
   * Returns a function to deregister the socket event callback.
   * @param callback 
   * @returns 
   */
  registerCallback: (
    callback: (payload: ChatMessagePayload) => void
  ) => () => void; // returns a deregister function
}

const ChatContext = createContext<ChatContextValue | undefined>(undefined);

export function useChat() {
  const context = useContext(ChatContext);
  if (context === undefined) {
    throw new Error('useChat must be used within an ChatProvider');
  }
  return context;
}

function ChatMessageToast({ payload, onClick }: { payload: ChatMessagePayload, onClick?:() => void }) {
  return (
    <div className="w-64">
      <div className="flex">
        <Link to={`/chat/${payload.groupChatId}`} className="text-lg mr-auto hover:cursor-pointer">Message</Link>
        <IconButton onClick={onClick}><X /></IconButton>
      </div>
      <p className="mr-auto break-words line-clamp-3">{payload.message}</p>
      <p className="text-sm font-thin text-gray-600">{new Date(payload.createdAt).toLocaleString()}</p>
    </div>
  )
}

export function ChatProvider({ children }: { children: ReactNode}) {
  const { isConnected, socket } = useSocket();

  const registerCallback = (callback: (payload: ChatMessagePayload) => void) => {
    if (!socket) return () => {};
    socket.on(SocketEvent.CHAT_MESSAGE, callback);

    // Return the unregister function
    return () => {
      socket.off(SocketEvent.CHAT_MESSAGE, callback);
    };
  };

  useEffect(() => {
    if (isConnected) {
      socket?.on(SocketEvent.CHAT_MESSAGE, (message: ChatMessagePayload) => {
        toast((t) => <ChatMessageToast payload={message} onClick={() => toast.dismiss(t.id)} />, {
          toasterId: "notification"
        });
      });
    }

    return () => {
      socket?.off(SocketEvent.CHAT_MESSAGE);
    }
  }, [isConnected]);


  return (
    <ChatContext.Provider value={{ registerCallback }}>
      {children}
    </ChatContext.Provider>
  )
}