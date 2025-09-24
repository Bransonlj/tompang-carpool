import { useNavigate } from "react-router";
import type { UserData } from "../types";

interface UserLinkProps {
  user: UserData;
}

export default function UserLink({
  user,
}: UserLinkProps) {
  const navigate = useNavigate();

  return (
    <span 
      className="font-semibold text-black hover:cursor-pointer hover:text-indigo-800"
      onClick={() => navigate(`/u/${user.id}`)}
      >{ user.name }</span>
  )
}