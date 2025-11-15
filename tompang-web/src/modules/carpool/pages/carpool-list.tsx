import { getCarpoolsByUser } from "../../../api/services/carpool/carpool.service";
import { useAuth } from "../../../context/auth-context";
import { getRideRequestsByUser } from "../../../api/services/ride-request/ride-request.service";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Box from "@mui/material/Box";
import TripCard from "../components/trip-card";
import { useNavigate, useSearchParams } from "react-router";
import { useQuery } from "@tanstack/react-query";
import Button from "@mui/material/Button";
import { Plus } from "lucide-react";

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function CustomTabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`simple-tabpanel-${index}`}
      aria-labelledby={`simple-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

export default function CarpoolListPage() {
  const { isAuthenticated, currentUserId, authToken } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams({ view: "carpool" });

  const navigate = useNavigate();
  const mode: number = searchParams.get("view") === "ride-request" ? 1 : 0 // 0 = carpool, 1 = ride request
  const handleChange = (_event: React.SyntheticEvent, newValue: number) => {
    setSearchParams((searchParams) => {
    searchParams.set("view", newValue === 0 ? "carpool" : "ride-request");
    return searchParams;
  });

  };

  const {
    data: carpools,
    isPending: isCarpoolsPending,
    isError: isCarpoolsError,
    error: carpoolsError,
  } = useQuery({
    queryKey: ["carpool-user", currentUserId],
    queryFn: () => {
      if (!isAuthenticated) {
        throw new Error("Must login to view carpools");
      }

      return getCarpoolsByUser(currentUserId, authToken);
    },
  });

  const {
    data: rideRequests,
    isPending: isRideRequestsPending,
    isError: isRideRequestsError,
    error: rideRequestsError,
  } = useQuery({
    queryKey: ["ride-request-user", currentUserId],
    queryFn: () => {
      if (!isAuthenticated) {
        throw new Error("Must login to view requests");
      }

      return getRideRequestsByUser(currentUserId, authToken);
    }
  });

  return (
    <div className="w-full">
      <Tabs value={mode} onChange={handleChange}>
          <Tab label="Carpools" />
          <Tab label="Ride Reqeusts"/>
      </Tabs>
      <CustomTabPanel value={mode} index={0}>
        <Button variant="contained" onClick={() => navigate("/carpool/new")}>New <Plus /></Button>
        <div>
          {
            isCarpoolsPending 
              ? "Loading"
              : isCarpoolsError
              ? carpoolsError.message
              : carpools.length === 0
              ? "No Carpools"
              : carpools.map(carpool => <TripCard
                key={carpool.id} 
                tripData={{
                  originAddress: carpool.originAddress,
                  destinationAddress: carpool.destinationAddress,
                  startTime: new Date(carpool.arrivalTime),
                  seats: { current: carpool.seatsAssigned, total: carpool.totalSeats },
                  originImageUrl: carpool.originImageUrl,
                  destinationImageUrl: carpool.destinationImageUrl,
                }}
                onClick={() => navigate(`/carpool/${carpool.id}`)}
              />
            )
          }
        </div>
      </CustomTabPanel>
      <CustomTabPanel value={mode} index={1}>
        <Button variant="contained" onClick={() => navigate("/carpool/ride/new")}>New <Plus /></Button>
        <div>
          {
            isRideRequestsPending 
              ? "Loading"
              : isRideRequestsError
              ? rideRequestsError.message
              : rideRequests.length === 0
              ? "No Ride Requests"
              : rideRequests.map(rideRequest => <TripCard 
                  key={rideRequest.id}
                  tripData={{
                    originAddress: rideRequest.originAddress,
                    destinationAddress: rideRequest.destinationAddress,
                    startTime: new Date(rideRequest.startTime),
                    endTime: new Date(rideRequest.endTime),
                    seats: rideRequest.passengers,
                    status: rideRequest.status,
                    originImageUrl: rideRequest.originImageUrl,
                    destinationImageUrl: rideRequest.destinationImageUrl,
                  }}
                  onClick={() => navigate(`/carpool/ride/${rideRequest.id}`)}
                />
              )
          }
        </div>
      </CustomTabPanel>
    </div>
  )
}