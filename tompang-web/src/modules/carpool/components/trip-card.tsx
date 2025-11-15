import type { UserData } from "../../../types";
import UserLink from "../../../components/user-link";
import RideRequestStatusLabel from "./ride-request-status-label";
import type { RideRequestStatus } from "../../../api/services/ride-request/types";
import Divider from "@mui/material/Divider";
import type { ReactNode } from "react";

type TripData = {
  originAddress: string;
  destinationAddress: string;
  startTime: Date;
  endTime?: Date;
  seats: number | { total: number; current: number; };
  status?: RideRequestStatus;
  owner?: UserData;
  originImageUrl: string | undefined;
  destinationImageUrl: string | undefined;
}

interface TripCardProps {
  tripData: TripData;
  onClick?: () => void;
  options?: ReactNode;
}

function formatSeats(seats: number | { total: number; current: number; }) {
  if (typeof seats === "number") {
    return `${seats} Passengers`;
  } else {
    return `${seats.current}/${seats.total} Seats`;
  }
}

export default function TripCard({
  tripData,
  onClick,
  options,
}: TripCardProps) {
  return (
    <div className="flex p-4 bg-stone-50 shadow-lg rounded-sm w-full max-w-4xl">
      {/* Map Snapshot */}
      <div className="w-1/2 flex">
        <img
          src={tripData.originImageUrl}
          alt="origin snapshot"
          className="w-full h-full object-cover"
        />
        <img
          src={tripData.destinationImageUrl}
          alt="destination snapshot"
          className="w-full h-full object-cover"
        />
      </div>

      <div className={`w-1/2 p-4 relative flex flex-col gap-2 ${onClick && "hover:cursor-pointer"}`} onClick={onClick}>
        {/* Title + Utility Button */}
        <div className="flex items-start gap-2">
          <div className="flex flex-col max-w-[80%] mr-auto">
            <h2 className="text-lg font-semibold break-words" >
              {tripData.destinationAddress}
            </h2>
            <span className="block break-words">From: {tripData.originAddress}</span>
          </div>
          { options }
        </div>
        <span className="block text-sm text-gray-500">
          {tripData.startTime.toLocaleString()} {tripData.endTime && `- ${tripData.endTime.toLocaleString()}`}
        </span>
        <Divider />
        {/* Details */}
        <div className="mt-3 flex gap-1 text-sm text-gray-700">
          {
            tripData.status &&
            <RideRequestStatusLabel className="mr-auto" status={tripData.status} />
          }
          <span className="font-medium">{ formatSeats(tripData.seats) }</span>
          {
            tripData.owner &&
            <UserLink user={tripData.owner} />
          }
        </div>
      </div>
    </div>
  )
}