import Divider from "@mui/material/Divider";
import { useAuth } from "../../../context/auth-context"
import { DriverRegistrationSettings } from "../component/driver-registration-setting";
import ProfilePictureSettings from "../component/profile-picture-settings";

export default function AccountSettingsPage() {

  const { currentUserId } = useAuth();


  return (
    <div>
      {currentUserId} account
      <ProfilePictureSettings />
      <Divider />
      <DriverRegistrationSettings />
    </div>

  )
}