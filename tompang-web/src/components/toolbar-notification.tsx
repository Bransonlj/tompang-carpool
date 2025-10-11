import IconButton from "@mui/material/IconButton";
import Popover from "@mui/material/Popover";
import { useQuery } from "@tanstack/react-query";
import { Bell } from "lucide-react";
import { useState } from "react";
import NotificationService from "../api/services/notification/notification.service";
import CircularProgress from "@mui/material/CircularProgress";
import Alert from "@mui/material/Alert";

interface ToolbarNotificationProps {
  userId: string;
  authToken: string;
}

export default function ToolbarNotification({
  userId,
  authToken,
}: ToolbarNotificationProps) {
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);
  const id = open ? 'simple-popover' : undefined;

  const {
    data: notifications,
    isPending,
    isError,
    error,
  } = useQuery({
    queryKey: ["user-notification", userId],
    queryFn: () => NotificationService.getUserNotifications(userId, authToken),
    enabled: open
  })

  return (
    <>
      <IconButton onClick={handleClick}><Bell /></IconButton>
      <Popover
        id={id}
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'center',
        }}
        transformOrigin={{
          vertical: 'top',
          horizontal: 'center',
        }}
      >
        <div className="max-h-96 overflow-y-auto w-64 flex flex-col">
          {
            isPending 
              ? <CircularProgress />
              : isError
              ? <Alert color="error">{ error.message }</Alert>
              : notifications.length === 0
              ? <h2 className="text-center p-2">No notifications</h2>
              : notifications.map((notification, index) => (
                <div key={notification.notificationId} className={`${index % 2 === 0 ? "bg-gray-50" : "bg-gray-200"} p-2`}>
                  <h2>{ notification.message }</h2>
                  <h3>{ new Date(notification.createdAt).toLocaleString() }</h3>
                </div>
              ))
          }
        </div>
      </Popover>
    </>
  )
}