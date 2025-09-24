import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router";
import { getRideRequestById } from "../../../api/services/ride-request/ride-request.service";
import Divider from "@mui/material/Divider";
import TripCard from "../components/trip-card";
import RideRequestStatusLabel from "../components/ride-request-status-label";

export default function RideRequestDetailPage() {
  
  const { id } = useParams();

  const {
    data,
    isPending,
    isError,
    error,
  } = useQuery({
    queryKey: ["ride-request-id", id],
    queryFn: () => {
      if (!id) {
        throw new Error("Ride Request Id required");
      }

      return getRideRequestById(id)
    }
  })

  if (isPending) {
    return <div>Loading</div>
  }

  if (isError) {
    return <div>{error.message}</div>
  }

  return (
    <div className="flex flex-col gap-4">
      <img
        src={"buh"}
        alt="Map snapshot"
        className="w-full h-full object-cover"
      />
      <div className="flex flex-col">
        <h2 className="text-lg font-semibold" >
          {data.destinationAddress}
        </h2>
        <span className="block ">From: {data.originAddress}</span>
        <span className="block text-sm text-gray-500">
          {new Date(data.startTime).toLocaleString()} - {new Date(data.endTime).toLocaleString()}
        </span>
        <span>{data.passengers} passengers</span>
      </div>
      <RideRequestStatusLabel status={data.status} />
      {
        data.assignedCarpool && <>
          <Divider />
          <TripCard
            tripData={{
              originAddress: data.assignedCarpool.originAddress,
              destinationAddress: data.assignedCarpool.destinationAddress,
              startTime: new Date(data.assignedCarpool.arrivalTime),
              seats: {
                current: data.assignedCarpool.seatsAssigned,
                total: data.assignedCarpool.totalSeats,
              },
              owner: data.assignedCarpool.driver
            }}
          />
        </>
      }
      {
        data.pendingCarpools.length !== 0 && <>
          <Divider />
          <div>
            {
              data.pendingCarpools.map(carpool => (
                <TripCard
                  key={carpool.id}
                  tripData={{
                    originAddress: carpool.originAddress,
                    destinationAddress: carpool.destinationAddress,
                    startTime: new Date(carpool.arrivalTime),
                    seats: {
                      current: carpool.seatsAssigned,
                      total: carpool.totalSeats,
                    },
                    owner: carpool.driver
                  }}
                />
              ))
            }
          </div>
        </>
      }
    </div>
  )
}