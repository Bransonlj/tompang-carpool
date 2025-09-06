import { useEffect, useState } from "react";
import { useSocket } from "./context/socket-context"


/**
 * Dummy page to log websocket data to console
 */
export default function TestSocket() {

  const { connect, isConnected, socket } = useSocket();
  const [token, setToken] = useState<string>("");
  const [notifications, setNotifications] = useState<any[]>([]);
  const [chatMessages, setChatMessages] = useState<any[]>([]);

  useEffect(() => {
    if (isConnected) {
      socket?.on("notification", (notif) => setNotifications(prev => [...prev, notif]));
      socket?.on("chat-message", (message) => setChatMessages(prev => [...prev, message]));
    }

    return () => {
      socket?.off("notification");
      socket?.off("chat-message");
    }
  }, [isConnected]);

  return (
    <div>
      <h2>Connection: {isConnected ? "true" : "false"}</h2>
      <label>Token</label>
      <input onChange={e => setToken(e.target.value)} />
      <button onClick={() => connect(token)}>Connect</button>
      <div className="flex">
        <div className="flex flex-col">
          <h2>Notifications</h2>
          {
            notifications.map(notif => (<span>{JSON.stringify(notif)}</span>))
          }
        </div>
        <div className="flex flex-col">
          <h2>Chat</h2>
          {
            chatMessages.map(chat => (<span>{JSON.stringify(chat)}</span>))
          }
        </div>
      </div>
    </div>
  )
}