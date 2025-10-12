import type { ReactNode } from "react";
import type { DriverRegistrationResponseDto, DriverRegistrationStatus } from "../../../api/services/driver/types";
import Typography from "@mui/material/Typography";

interface DriverRegistrationCardProps {
  registration: DriverRegistrationResponseDto;
  children?: ReactNode;
}

function DriverRegistrationStatus({ status, reason }: { status: DriverRegistrationStatus, reason?: string }) {
  return (
    <Typography
    className="whitespace-pre-line flex-1/3"
      color={status === "FAILED" ? "error" : status === "INACTIVE" ? "textDisabled" : status === "SUCCESS" ? "success" : "primary"}
    >
      { 
        status === "FAILED" 
          ? "Failed:" 
          : status === "INACTIVE"
          ? "Inactive"
          : status === "SUCCESS"
          ? "Success"
          : "Pending"
      }
      { reason && `\n${reason}` }
    </Typography>
  )
}

export default function DriverRegistrationCard({
  registration,
  children,
}: DriverRegistrationCardProps) {
  return (
    <div className="w-full flex flex-row p-2 bg-gray-50 rounded-sm">
      <img 
        className="m-2 border-2 border-black rounded-md"
        src={registration.imageUrl} 
        width={100} 
        alt="vehicle image" />
      <div className="flex flex-row mr-auto flex-auto">
        <span className="flex-1/3">Registration no. : {registration.vehicleRegistrationNumber}</span>
        <span className="flex-1/3">Make : {registration.vehicleMake}</span>
        <span className="flex-1/3">Model : {registration.vehicleModel}</span>
        <DriverRegistrationStatus status={registration.status} reason={registration.rejectedReason} />
        <span className="flex-1/3">Created at. : {new Date(registration.createdAt).toLocaleString()}</span>
      </div>
      { children }
    </div>
  )
}