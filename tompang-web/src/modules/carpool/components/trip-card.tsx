import { MoreVertical } from "lucide-react";
import type { UserData } from "../../../types";
import UserLink from "../../../components/user-link";
import RideRequestStatusLabel from "./ride-request-status-label";
import type { RideRequestStatus } from "../../../api/services/ride-request/types";
import Divider from "@mui/material/Divider";

type TripData = {
  originAddress: string;
  destinationAddress: string;
  startTime: Date;
  endTime?: Date;
  seats: number | { total: number; current: number; };
  status?: RideRequestStatus;
  owner?: UserData;
}

interface TripCardProps {
  tripData: TripData;
  onClick?: () => void;
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
}: TripCardProps) {
  return (
    <div className="flex p-4 bg-stone-50 shadow-lg rounded-sm w-full max-w-4xl">
      {/* Map Snapshot */}
      <div className="w-1/4">
        <img
          src={"buh"}
          alt="Map snapshot"
          className="w-full h-full object-cover"
        />
      </div>

      <div className={`w-3/4 p-4 relative flex flex-col gap-2 ${onClick && "hover:cursor-pointer"}`} onClick={onClick}>
        {/* Title + Utility Button */}
        <div className="flex justify-between items-start">
          <div className="flex flex-col truncate max-w-[80%]">
            <h2 className="text-lg font-semibold" >
              {tripData.destinationAddress}
            </h2>
            <span className="block ">From: {tripData.originAddress}</span>
            <span className="block text-sm text-gray-500">
              {tripData.startTime.toLocaleString()} {tripData.endTime && `- ${tripData.endTime.toLocaleString()}`}
            </span>
          </div>
          <button className="p-2 rounded-full hover:bg-gray-100">
            <MoreVertical className="w-5 h-5 text-gray-600" />
          </button>
        </div>
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