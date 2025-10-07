import { useQuery } from "@tanstack/react-query"
import { useAuth } from "../../../context/auth-context"
import ChatService from "../../../api/services/chat/chat.service";
import { useNavigate, useParams } from "react-router";
import MessageBubble from "../components/message-bubble";
import IconButton from "@mui/material/IconButton";
import { ArrowLeft } from "lucide-react";
import MessageInput from "../components/message-input";

export default function GroupChatPage() {

  const { gid: groupId } = useParams()
  const { currentUserId, isAuthenticated, authToken } = useAuth();
  const navigate = useNavigate();

  const {
    data: messages,
    isPending,
    isError,
    error,
  } = useQuery({
    queryKey: ["chat-group", groupId],
    queryFn: () => {
      if (!isAuthenticated) throw new Error("Must be authenticated");
      if (!groupId) throw new Error("GroupId url param is missing");
      return ChatService.getGroupMessages(groupId, authToken);
    }
  })

  if (!groupId) {
    return <div>gid param not found</div>
  }

  if (isPending) {
    return <div>Loading</div>
  }

  if (isError) {
    return <div>{error.message}</div>
  }

  return (
    <div className="flex flex-col w-full items-stretch">
      <div className="flex items-center">
        <IconButton onClick={() => navigate(`/carpool/${groupId}`)}>
          <ArrowLeft />
          Back
        </IconButton>
        <h2 className="text-lg font-semibold">Carpool Chat</h2>
      </div>
      <div className="max-h-[70vh] flex flex-col bg-gray-50 rounded-lg p-2 overflow-y-scroll">
        {
          messages.map(message => (
            <MessageBubble 
              senderName={message.senderName}
              senderTitle={message.senderTitle}
              date={new Date(message.createdAt)}
              message={message.message}
              variant={message.senderId === currentUserId ? "right" : "left"}
            />
          ))
        }
      </div>
      <MessageInput className="" targetGroupId={groupId} />
    </div>
  )
}