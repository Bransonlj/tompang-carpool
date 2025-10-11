import { useQuery } from "@tanstack/react-query";
import { useNavigate, useParams } from "react-router";
import { getRideRequestById } from "../../../api/services/ride-request/ride-request.service";
import Divider from "@mui/material/Divider";
import TripCard from "../components/trip-card";
import RideRequestStatusLabel from "../components/ride-request-status-label";
import { useAuth } from "../../../context/auth-context";
import RoutePreview from "../components/route-preview";
import { LatLng } from "leaflet";
import IconButton from "@mui/material/IconButton";
import { MessageCircleMore } from "lucide-react";

export default function RideRequestDetailPage() {
  
  const { id } = useParams();
  const { isAuthenticated, authToken } = useAuth();
  const navigate = useNavigate();

  const {
    data,
    isPending,
    isError,
    error,
  } = useQuery({
    queryKey: ["ride-request-id", id],
    queryFn: () => {
      if (!id || !isAuthenticated) {
        throw new Error("Ride Request Id required");
      }

      return getRideRequestById(id, authToken);
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
      <RoutePreview
        origin={new LatLng(data.originLatLng.lat, data.originLatLng.lng)} 
        destination={new LatLng(data.destinationLatLng.lat, data.destinationLatLng.lng)}
        className="w-full h-72"
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
            options={
              <IconButton onClick={() => navigate(`/chat/${data.assignedCarpool?.id}`, { state: { from: "ride-request", id } })}>
                <MessageCircleMore />
              </IconButton>
            }
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