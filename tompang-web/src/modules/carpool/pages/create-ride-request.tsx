import { useMutation } from "@tanstack/react-query";
import RoutePicker from "../components/route-picker";
import { useState } from "react";
import type { LatLng } from "leaflet";
import { SINGAPORE } from "../const";
import TextField from "@mui/material/TextField";
import { useAuth } from "../../../context/auth-context";
import Button from "@mui/material/Button";
import { DateTimePicker } from "@mui/x-date-pickers";
import { Dayjs } from "dayjs";
import dayjs from "dayjs";
import toast from "react-hot-toast";
import { useNavigate } from "react-router";
import RideRequestService from "../../../api/services/ride-request/ride-request.service";
import Divider from "@mui/material/Divider";

export default function CreateRideRequestPage() {

  const { currentUserId, isAuthenticated, authToken } = useAuth();
  const navigate = useNavigate();

  const [passengers, setPassengers] = useState<number | null>(null);
  const [origin, setOrigin] = useState<LatLng>(SINGAPORE);
  const [destination, setDestination] = useState<LatLng>(SINGAPORE);
  const [startTime, setStartTime] = useState<Dayjs | null>(dayjs());
  const [endTime, setEndTime] = useState<Dayjs | null>(dayjs());

  const mutation = useMutation({
    mutationFn: () => {
      if (!isAuthenticated) throw new Error("Must be authenticated");
      if (!passengers) throw new Error("Passengers is missing");
      if (!startTime) throw new Error("Start time is required");
      if (!endTime) throw new Error("End time is required");
      return RideRequestService.createRideRequest({
        passengers,
        route: {
          origin: {
            latitude: origin.lat,
            longitude: origin.lng,
          },
          destination: {
            latitude: destination.lat,
            longitude: destination.lng,
          },
        },
        startTime: startTime.toDate(),
        endTime: endTime.toDate(),
        riderId: currentUserId,
      }, authToken)
    },
    onSuccess() {
      toast.success("Ride Request created successfully");
      navigate(`/carpool?view=ride-request`);
    }
  })

  return (
    <div className="flex flex-col gap-2 mt-2">
      <h2 className="text-lg font font-semibold text-blue-600 text-center">New Ride Request</h2>
      <Divider flexItem />
      <h2 className="text-blue-600 text-center">Route</h2>
      <RoutePicker origin={origin} onOriginChange={setOrigin} destination={destination} onDestinationChange={setDestination} />
      <Divider flexItem />
      <TextField className="bg-gray-50" label="Passengers" type="number" value={passengers} onChange={(e) => setPassengers(Number(e.target.value))} />
      <DateTimePicker className="bg-gray-50" label="Start Time" value={startTime} onChange={setStartTime} />
      <DateTimePicker className="bg-gray-50" label="End Time" value={endTime} onChange={setEndTime} />
      <Button disabled={mutation.isPending} onClick={() => mutation.mutate()} variant="contained">Create</Button>
    </div>
  )
}