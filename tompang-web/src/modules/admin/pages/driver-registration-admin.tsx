import { useMutation, useQuery } from "@tanstack/react-query";
import { useAuth } from "../../../context/auth-context";
import DriverRegistrationCard from "../../user/component/driver-registration-card";
import Button from "@mui/material/Button";
import { useState } from "react";
import TextField from "@mui/material/TextField";
import DriverService from "../../../api/services/driver/driver.service";
import toast from "react-hot-toast";

export default function DriverRegistrationAdminPage() {
  const { isAuthenticated, authToken } = useAuth();
  const [rejectReason, setRejectReason] = useState<string>();
  const [rejectRegId, setRejectRegId] = useState<string | null>(null);

  const {
    data: pendingRegistrations,
    isPending,
    isError,
    error,
    refetch,
  } = useQuery({
    queryKey: ["admin-pending-driver-registration"],
    queryFn: () => {
      if (!isAuthenticated) throw new Error("Must be admin");
      return DriverService.getAdminPendingDriverRegistrations(authToken);
    }
  });

  function openDeclineOptions(regId: string) {
    setRejectRegId(prev => prev === regId ? null : regId);
    setRejectReason("");
  }

  const mutation = useMutation({
    mutationFn: async ( param: { isAccept: boolean, regId: string } ) => {
      if (!isAuthenticated) throw new Error("Must be authenticated");
      if (param.isAccept) {
        await DriverService.adminAcceptDriverRegistration(param.regId, authToken);
      } else {
        if (!rejectReason) throw new Error("Decline reason is required");
        await DriverService.adminRejectDriverRegistration(param.regId, rejectReason, authToken);
      }
    },
    onError(error) {
      toast.error(error.message);
    },
    onSuccess(_data, param) {
      toast.success(`Registration ${param.isAccept ? "accepted" : "declined"} successfully`);
      setRejectReason("");
      setRejectRegId(null);
      refetch();
    }
  })

  if (isPending) {
    return <div>Loading</div>
  }

  if (isError) {
    return <div>{error.message}</div>
  }

  return (
    <div>
      {
        pendingRegistrations.map(registration => (
          <DriverRegistrationCard key={registration.id} registration={registration}>
            <div className="flex flex-col w-48">
              <div className="flex items-center gap-2">
                <Button 
                  disabled={mutation.isPending}
                  onClick={() => mutation.mutate({ regId: registration.id, isAccept: true })} 
                  color="success" 
                  variant="contained"
                >Accept</Button>
                <Button 
                  disabled={mutation.isPending}
                  onClick={() => openDeclineOptions(registration.id)} 
                  color="error" 
                  variant={rejectRegId === registration.id ? "outlined" : "contained"}
                >Decline</Button>
              </div>
              {
                rejectRegId === registration.id && <div className="flex flex-col">
                  <TextField
                    disabled={mutation.isPending}
                    variant="standard"
                    label="Decline reason"
                    value={rejectReason}
                    onChange={(e) => setRejectReason(e.target.value)}
                  />
                  <Button disabled={mutation.isPending} onClick={() => mutation.mutate({ regId: registration.id, isAccept: false })}>Submit</Button>
                </div>
              }
            </div>
          </DriverRegistrationCard>
        ))
      }
    </div>
  )
}