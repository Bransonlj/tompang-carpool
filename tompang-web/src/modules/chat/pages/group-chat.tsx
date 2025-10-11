import { useQuery } from "@tanstack/react-query"
import { useAuth } from "../../../context/auth-context"
import ChatService from "../../../api/services/chat/chat.service";
import { useLocation, useNavigate, useParams } from "react-router";
import MessageBubble from "../components/message-bubble";
import IconButton from "@mui/material/IconButton";
import { ArrowLeft } from "lucide-react";
import MessageInput from "../components/message-input";
import { useEffect, useRef, useState } from "react";
import { type ChatMessage } from "../../../api/services/chat/types";
import { useChat } from "../context/chat-context";

export default function GroupChatPage() {

  const { gid: groupId } = useParams()
  const { currentUserId, isAuthenticated, authToken } = useAuth();
  const { registerCallback } = useChat();
  const location = useLocation();
  const navigate = useNavigate();
  const optimisticMessageId = useRef<number>(1);
  const [optimisticMessages, setOptimisticMessages] = useState<ChatMessage[]>([])

  function handleMessageSent(message: string): void {
    setOptimisticMessages(prev => [...prev, {
      messageId: optimisticMessageId.current.toString(),
      senderId: currentUserId ?? "",
      message,
      createdAt: new Date().toLocaleString(),
    }]);
    optimisticMessageId.current += 1;
  }

  const handleBack = () => {
    const from = location.state?.from;
    const id = location.state?.id;

    if (from === "carpool") {
      navigate(`/carpool/${id}`);
    } else if (from === "ride-request") {
      navigate(`/carpool/ride/${id}`);
    } else {
      navigate(-1); // fallback if state missing, goes back 1 step in history
    }
  };

  const {
    data: groupChatData,
    isPending,
    isError,
    error,
  } = useQuery({
    queryKey: ["chat-group", groupId],
    queryFn: () => {
      if (!isAuthenticated) throw new Error("Must be authenticated");
      if (!groupId) throw new Error("GroupId url param is missing");
      return ChatService.getGroupChatData(groupId, authToken);
    }
  })

  useEffect(() => {
    const unregister = registerCallback((message) => {
      if (message.groupChatId === groupId) {
        setOptimisticMessages(prev => [...prev, {
          messageId: message.messageId,
          senderId: message.senderUserId,
          message: message.message,
          createdAt: message.createdAt,
        }]);
        optimisticMessageId.current += 1;
      }
    });

    return unregister;
  });

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
        <IconButton onClick={handleBack}>
          <ArrowLeft />
          Back
        </IconButton>
        <h2 className="text-lg font-semibold">Carpool Chat</h2>
      </div>
      <div className="h-[70vh] flex flex-col bg-gray-50 rounded-lg p-2 overflow-y-scroll">
        {
          [...groupChatData.messages, ...optimisticMessages].map(message => (
            <MessageBubble 
              key={message.messageId}
              sender={groupChatData.members[message.senderId]}
              date={new Date(message.createdAt)}
              message={message.message}
              variant={message.senderId === currentUserId ? "right" : "left"}
            />
          ))
        }
      </div>
      <MessageInput className="" targetGroupId={groupId} onMessageSent={handleMessageSent}/>
    </div>
  )
}