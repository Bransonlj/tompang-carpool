import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router";
import { getUserById } from "../../../api/services/user/user.service";
import UserAvatar from "../../../components/user-avatar";
import Divider from "@mui/material/Divider";
import { useAuth } from "../../../context/auth-context";

export default function UserProfilePage() {
  const { id } = useParams();
  const { isAuthenticated, authToken } = useAuth();

  const {
    data,
    isPending,
    isError,
    error,
  } = useQuery({
    queryKey: ["user-id", id],
    queryFn: () => {
      if (!id || !isAuthenticated) {
        throw new Error("User Id required");
      }

      return getUserById(id, authToken)
    }
  })

  if (isPending) {
    return <div>Loading</div>
  }

  if (isError) {
    return <div>{error.message}</div>
  }

  return (
    <div className="flex gap-4">
      <UserAvatar name={data.name} src={data.profileImgUrl} sx={{ width: 200, height: 200 }}/>
      <Divider orientation="vertical" flexItem  />
      <div>
        <h2 className="font-bold text-xl">{data.name}</h2>
        <span>Some details: buh</span>
      </div>
    </div>
  )
}