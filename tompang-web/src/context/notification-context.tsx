import { createContext, useContext, useEffect, type ReactNode } from "react";
import { useSocket } from "./socket-context";
import toast from "react-hot-toast";
import IconButton from "@mui/material/IconButton";
import { X } from "lucide-react";

const NotificationContext = createContext({});

// not needed?
export function useNotification() {
  return useContext(NotificationContext);
}

type NotificationPayload = {
  createdAt: string; //iso-date string
  notificationId: string;
  message: string;
}

function NotificationToast({ payload, onClick }: { payload: NotificationPayload, onClick?:() => void }) {
  return (
    <div className="w-64">
      <div className="flex">
        <h2 className="text-lg mr-auto">Notification</h2>
        <IconButton onClick={onClick}><X /></IconButton>
      </div>
      <p className="mr-auto break-words line-clamp-3">{payload.message}</p>
      <p className="text-sm font-thin text-gray-600">{new Date(payload.createdAt).toLocaleString()}</p>
    </div>
  )
}

export function NotificationProvider({ children }: { children: ReactNode}) {
  const { isConnected, socket } = useSocket();

  useEffect(() => {
    if (isConnected) {
      socket?.on("notification", (notif: NotificationPayload) => {
        console.log("notification received", notif);
        toast((t) => <NotificationToast payload={notif} onClick={() => toast.dismiss(t.id)} />, {
          toasterId: "notification"
        });
      });
    }

    return () => {
      socket?.off("notification");
    }
  }, [isConnected]);


  return (
    <NotificationContext.Provider value={{}}>
      {children}
    </NotificationContext.Provider>
  )
}