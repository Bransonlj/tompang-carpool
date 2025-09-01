import { useEffect, useState } from "react";
import { useSocket } from "./context/socket-context"


/**
 * Dummy page to log websocket data to console
 */
export default function TestSocket() {

  const { connect, isConnected, socket } = useSocket();
  const [token, setToken] = useState<string>("");

  useEffect(() => {
    if (isConnected) {
      socket?.on("notification", (notif) => console.log(notif));
    }

    return () => {
      socket?.off("notification")
    }
  }, [isConnected]);

  return (
    <div>
      <h2>Connection: {isConnected ? "true" : "false"}</h2>
      <label>Token</label>
      <input onChange={e => setToken(e.target.value)} />
      <button onClick={() => connect(token)}>Connect</button>
    </div>
  )
}