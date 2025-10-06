import { useQuery } from "@tanstack/react-query";
import { useAuth } from "../../../context/auth-context";
import { getDriverRegistrationByUserId } from "../../../api/services/driver/driver.service";
import DriverRegistrationForm from "./driver-registration-form";

export function DriverRegistrationSettings() {
  const { isAuthenticated, currentUserId, authToken } = useAuth();

  const {
    data: registrations,
    isPending,
    isError,
    error,
  } = useQuery({
    queryKey: ["driver-registration-user", currentUserId],
    queryFn: () => {
      if (!currentUserId || !isAuthenticated) {
        throw new Error("User Id required");
      }

      return getDriverRegistrationByUserId(currentUserId, authToken)
    },
  });

  if (isPending) {
    return <div>Loading</div>
  }

  if (isError) {
    return <div>{error.message}</div>
  }

  return (
    <div>
      <div>
        {
          registrations?.map(registration => (
            <div>
              <span>Registration no. : {registration.vehicleRegistrationNumber}</span>
              <span>Make : {registration.vehicleMake}</span>
              <span>Model : {registration.vehicleModel}</span>
              <span>Status : {registration.status}</span>
              <span>Created at. : {new Date(registration.createdAt).toLocaleString()}</span>
            </div>
          ))
            
        }
      </div>
      <DriverRegistrationForm />
    </div>
  )

}