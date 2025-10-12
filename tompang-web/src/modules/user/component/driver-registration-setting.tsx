import { useQuery } from "@tanstack/react-query";
import { useAuth } from "../../../context/auth-context";
import DriverRegistrationForm from "./driver-registration-form";
import DriverRegistrationCard from "./driver-registration-card";
import DriverService from "../../../api/services/driver/driver.service";
import Divider from "@mui/material/Divider";

export function DriverRegistrationSettings() {
  const { isAuthenticated, currentUserId, authToken } = useAuth();

  const {
    data: registrations,
    isPending,
    isError,
    error,
    refetch,
  } = useQuery({
    queryKey: ["driver-registration-user", currentUserId],
    queryFn: () => {
      if (!currentUserId || !isAuthenticated) {
        throw new Error("User Id required");
      }

      return DriverService.getDriverRegistrationsByUserId(currentUserId, authToken)
    },
  });

  if (isPending) {
    return <div>Loading</div>
  }

  if (isError) {
    return <div>{error.message}</div>
  }

  return (
    <div className="flex flex-col gap-2">
      <h2 className="text-lg text-gray-800">Past Registrations</h2>
      <div className="flex flex-col">
        {
          registrations.map(registration => (
            <DriverRegistrationCard key={registration.id} registration={registration} />
          ))   
        }
      </div>
      <Divider />
      <DriverRegistrationForm onRegistered={refetch} />
    </div>
  )

}