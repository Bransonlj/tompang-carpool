import { useMutation } from "@tanstack/react-query";
import RoutePicker from "../components/route-picker";
import CarpoolService from "../../../api/services/carpool/carpool.service";
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

export default function CreateCarpoolPage() {

  const { currentUserId, isAuthenticated, authToken } = useAuth();
  const navigate = useNavigate();

  const [seats, setSeats] = useState<number | null>(null);
  const [origin, setOrigin] = useState<LatLng>(SINGAPORE);
  const [destination, setDestination] = useState<LatLng>(SINGAPORE);
  const [arrivalTime, setArrivalTime] = useState<Dayjs | null>(dayjs());

  const mutation = useMutation({
    mutationFn: () => {
      if (!isAuthenticated) throw new Error("Must be authenticated");
      if (!seats) throw new Error("Seats is missing");
      if (!arrivalTime) throw new Error("Arrival time is required");
      return CarpoolService.createCarpool({
        seats,
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
        arrivalTime: arrivalTime.toDate(),
        driverId: currentUserId,
      }, authToken)
    },
    onSuccess() {
      toast.success("Carpool created successfully");
      navigate(`/carpool`);
    }
  })

  return (
    <div>
      <TextField label="No. Seats" type="number" value={seats} onChange={(e) => setSeats(Number(e.target.value))} />
      <RoutePicker origin={origin} onOriginChange={setOrigin} destination={destination} onDestinationChange={setDestination} />
      <DateTimePicker label="Arrival Time" value={arrivalTime} onChange={setArrivalTime} />
      <Button disabled={mutation.isPending} onClick={() => mutation.mutate()} variant="contained">Create</Button>
    </div>
  )
}