import { useQuery } from "@tanstack/react-query";
import { getUserById } from "../api/services/user/user.service";
import UserAvatar from "./user-avatar";
import { useState } from "react";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Avatar from "@mui/material/Avatar";
import Divider from "@mui/material/Divider";
import ListItemIcon from "@mui/material/ListItemIcon";
import { LogOut } from "lucide-react";
import ListSubheader from "@mui/material/ListSubheader";
import { useAuth } from "../context/auth-context";
import { useNavigate } from "react-router";

interface ToolbarProfile {
  userId: string;
  token: string;
  onLogout?: () => void;
}

export default function ToolbarProfile({
  userId,
  token,
  onLogout,
}: ToolbarProfile) {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const navigate = useNavigate();
  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  const handleLogout = () => {
    handleClose();
    onLogout?.();
  }
  const handleClickProfile = () => {
    handleClose();
    navigate(`/u/${userId}`);
  }  
  const handleClickAccount = () => {
    handleClose();
    navigate(`/u/account`);
  }
  const {} = useAuth()

  const {
    data,
    isPending,
    isError,
    error,
  } = useQuery({
    queryKey: ["user-id", userId],
    queryFn: () => getUserById(userId, token),
  })

  if (isPending) {
    return <div>Loading</div>
  }

  if (isError) {
    return <div>{error.message}</div>
  }

  return (
    <>
      <UserAvatar 
        className="hover:cursor-pointer" 
        sx={{ width: 48, height: 48 }}
        onClick={handleClick} 
        name={data.name} 
        src={data.profileImgUrl} />
      <Menu
        anchorEl={anchorEl}
        id="account-menu"
        open={open}
        onClose={handleClose}
        onClick={handleClose}
        slotProps={{
          paper: {
            elevation: 0,
            sx: {
              overflow: 'visible',
              filter: 'drop-shadow(0px 2px 8px rgba(0,0,0,0.32))',
              mt: 1.5,
              '& .MuiAvatar-root': {
                width: 32,
                height: 32,
                ml: -0.5,
                mr: 1,
              },
              '&::before': {
                content: '""',
                display: 'block',
                position: 'absolute',
                top: 0,
                right: 14,
                width: 10,
                height: 10,
                bgcolor: 'background.paper',
                transform: 'translateY(-50%) rotate(45deg)',
                zIndex: 0,
              },
            },
          },
        }}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
      >
        <ListSubheader>{data.name}</ListSubheader>
        <MenuItem onClick={handleClickProfile}>
          <Avatar /> Profile
        </MenuItem>
        <MenuItem onClick={handleClickAccount}>
          <Avatar /> My account
        </MenuItem>
        <Divider />
        <MenuItem onClick={handleLogout}>
          <ListItemIcon>
            <LogOut fontSize="small" />
          </ListItemIcon>
          Logout
        </MenuItem>
      </Menu>
    </>
  )
}