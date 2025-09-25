import { useAuth } from "../../../context/auth-context"
import ProfilePictureSettings from "../component/profile-picture-settings";

export default function AccountSettingsPage() {

  const { currentUserId } = useAuth();


  return (
    <div>
      {currentUserId} account
      <ProfilePictureSettings />
    </div>

  )
}