import UserAvatar from "../../../components/user-avatar";

interface MessageBubbleProps extends React.HtmlHTMLAttributes<HTMLDivElement> {
  sender?: {
    senderName: string;
    senderTitle: string;
    senderProfilePicture?: string;
  }
  date: Date;
  message: string;
  variant?: "left" | "right";
}

export default function MessageBubble({
  sender={ senderName: "Uknown", senderTitle: "unknown" },
  date,
  message,
  variant="left",
  className,
  ...props
}: MessageBubbleProps) {
  return (
    <div className={`flex flex-col min-w-48 p-2 rounded-md ${variant === "right" ? "ml-auto bg-green-400" : "mr-auto bg-blue-400"}`} 
      {...props}>
      <div className="flex items-start">
        <UserAvatar src={sender.senderProfilePicture} name={sender.senderName} />
        <div>
          <h2>{ sender.senderName }</h2>
          <h3>{ sender.senderTitle }</h3>
        </div>
      </div>
      <p>{ message }</p>
      <h4>{ date.toLocaleString() }</h4>
    </div>
  )
}