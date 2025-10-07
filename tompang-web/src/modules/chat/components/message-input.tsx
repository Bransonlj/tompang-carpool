import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import { useMutation } from "@tanstack/react-query";
import ChatService from "../../../api/services/chat/chat.service";
import { useState } from "react";
import { useAuth } from "../../../context/auth-context";
import toast from "react-hot-toast";

interface MessageInputProps extends React.HtmlHTMLAttributes<HTMLDivElement> {
  targetGroupId: string;
}

export default function MessageInput({
  targetGroupId,
  className,
  ...props
}: MessageInputProps) {

  const [message, setMessage] = useState<string>("");
  const { currentUserId, isAuthenticated, authToken } = useAuth();

  const mutation = useMutation({
    mutationFn: () => {
      if (!isAuthenticated) throw new Error("Must be authentitcated");
      return ChatService.sendChatMessage({ groupId: targetGroupId, senderId: currentUserId, message }, authToken);
    },
    onError: (err) => {
      toast.error(err.message)
    },
  });

  return (
    <div className={`${className} flex`} {...props} > 
      <TextField 
        className="bg-gray-50 flex-1"
        value={message} 
        onChange={(e) => setMessage(e.target.value)} 
        multiline 
        minRows={2} 
        maxRows={5}/>
      <Button 
      className="flex-none "
        onClick={() => mutation.mutate()} 
        disabled={mutation.isPending || !message} 
        variant="contained"
        color={mutation.isError ? "error" : "primary"}
      >Send</Button>
    </div>
  )
}