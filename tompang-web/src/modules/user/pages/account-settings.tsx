import { useAuth } from "../../../context/auth-context"

export default function AccountSettingsPage() {

  const {currentUserId} = useAuth();

  return (
    <div>
      {currentUserId} account
    </div>
  )
}