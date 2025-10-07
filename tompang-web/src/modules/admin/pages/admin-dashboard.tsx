import type { ReactNode } from "react";
import { useNavigate } from "react-router"

interface DashboardCardProps {
  title: string;
  onClick: () => void;
  children?: ReactNode;
}

function DashboardCard({
  title,
  onClick,
  children,
}: DashboardCardProps) {
  return (
    <div 
      onClick={onClick} 
      className="p-2 rounded-lg border-2 border-blue-200 shadow-lg bg-gray-50 hover:cursor-pointer"
    >
      <h2 className="text-lg text-blue-700 font-medium">{ title }</h2>
      <p className="text-sm text-blue-400 font-thin">
        { children }
      </p>
    </div>
  )
}

export default function AdminDashboard() {

  const navigate = useNavigate();

  const goto = (link: string) => () => navigate(link);

  return (
    <div className="flex flex-wrap gap-2">
      <DashboardCard title="Driver Registration" onClick={goto("/admin/driver-registration")}>
        Manual review pending driver registrations
      </DashboardCard>
    </div>
  )
}