import type { ReactNode } from "react";
import type { DriverRegistrationResponseDto } from "../../../api/services/driver/types";

interface DriverRegistrationCardProps {
  registration: DriverRegistrationResponseDto;
  children?: ReactNode;
}

export default function DriverRegistrationCard({
  registration,
  children,
}: DriverRegistrationCardProps) {
  return (
    <div className="flex flex-row p-2 bg-gray-50 rounded-sm">
      <img 
        className="m-2 border-2 border-black rounded-md"
        src={registration.imageUrl} 
        width={100} 
        alt="vehicle image" />
      <div className="flex flex-row justify-around mr-auto">
        <span className="flex-1/3">Registration no. : {registration.vehicleRegistrationNumber}</span>
        <span className="flex-1/3">Make : {registration.vehicleMake}</span>
        <span className="flex-1/3">Model : {registration.vehicleModel}</span>
        <span className="flex-1/3">Status : {registration.status}</span>
        <span className="flex-1/3">Created at. : {new Date(registration.createdAt).toLocaleString()}</span>
      </div>
      { children }
    </div>
  )
}