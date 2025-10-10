import { useNavigate, useParams } from "react-router";
import { getCarpoolById } from "../../../api/services/carpool/carpool.service";
import { useQuery } from "@tanstack/react-query";
import TripCard from "../components/trip-card";
import Divider from "@mui/material/Divider";
import { useAuth } from "../../../context/auth-context";
import IconButton from "@mui/material/IconButton";
import { MessageCircleMore } from "lucide-react";
import { LatLng } from "leaflet";
import CarpoolRequestAction from "../components/carpool-request-action";
import RoutePreview from "../components/route-preview";

export default function CarpoolDetailPage() {

  const { id } = useParams();
  const { isAuthenticated, authToken } = useAuth();
  const navigate = useNavigate();

  const {
    data,
    isPending,
    isError,
    error,
    refetch,
  } = useQuery({
    queryKey: ["carpool-id", id],
    queryFn: () => {
      if (!id || !isAuthenticated) {
        throw new Error("Carpool Id required");
      }

      return getCarpoolById(id, authToken)
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
      <div className="flex">
        <div className="flex flex-col mr-auto">
          <h2 className="text-lg font-semibold" >
            {data.destinationAddress}
          </h2>
          <span className="block ">From: {data.originAddress}</span>
          <span className="block text-sm text-gray-500">
            {new Date(data.arrivalTime).toLocaleString()}
          </span>
        </div>
        <Divider orientation="vertical" flexItem />
        <IconButton onClick={() => navigate(`/chat/${data.id}`)}><MessageCircleMore /></IconButton>
      </div>
      <Divider />
      <div>
        <h2 className="font-semibold text-lg flex">
          <span className="mr-auto">Confirmed Riders</span> 
          <span>{data.seatsAssigned}/{data.totalSeats} passengers</span>
        </h2>
        {data.confirmedRides.map(ride => (
          <TripCard 
            key={ride.id}
            tripData={{
              originAddress: ride.originAddress,
              destinationAddress: ride.destinationAddress,
              startTime: new Date(ride.startTime),
              endTime: new Date(ride.endTime),
              seats: ride.passengers,
              owner: ride.rider,
            }}
          />
        ))}
      </div>
      <Divider />
      <div>
        <h2 className="font-semibold text-lg">Pending Requests</h2>
        {data.pendingRequests.map(ride => (
          <div className="flex gap-2">
            <TripCard 
              key={ride.id}
              tripData={{
                originAddress: ride.originAddress,
                destinationAddress: ride.destinationAddress,
                startTime: new Date(ride.startTime),
                endTime: new Date(ride.endTime),
                seats: ride.passengers,
                owner: ride.rider,
              }}
              options={<CarpoolRequestAction carpoolId={data.id} requestId={ride.id} onSuccess={refetch} />}
            />
          </div>
        ))}
      </div>
    </div>
  )
}