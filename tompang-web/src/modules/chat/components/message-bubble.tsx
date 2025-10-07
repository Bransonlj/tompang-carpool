import type { SenderTitle } from "../../../api/services/chat/types";

interface MessageBubbleProps extends React.HtmlHTMLAttributes<HTMLDivElement> {
  senderName: string;
  date: Date;
  senderTitle: SenderTitle;
  message: string;
  variant?: "left" | "right";
}

export default function MessageBubble({
  senderName,
  senderTitle,
  date,
  message,
  variant="left",
  className,
  ...props
}: MessageBubbleProps) {
  return (
    <div className={`flex flex-col min-w-48 p-2 rounded-md ${variant === "right" ? "ml-auto bg-green-400" : "mr-auto bg-blue-400"}`} 
      {...props}>
      <div>
        <h2>{ senderName }</h2>
        <h3>{ senderTitle}</h3>
      </div>
      <p>{ message }</p>
      <h4>{ date.toLocaleString() }</h4>
    </div>
  )
}