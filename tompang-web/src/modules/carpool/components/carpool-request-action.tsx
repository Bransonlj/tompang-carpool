import { useMutation } from "@tanstack/react-query";
import CarpoolService from "../../../api/services/carpool/carpool.service";
import toast from "react-hot-toast";
import Button from "@mui/material/Button";
import CircularProgress from "@mui/material/CircularProgress";
import Alert from "@mui/material/Alert";
import { useAuth } from "../../../context/auth-context";

interface CarpoolRequestAction {
  carpoolId: string;
  requestId: string;
  onSuccess?: () => void;
}

export default function CarpoolRequestAction({
  carpoolId,
  requestId,
  onSuccess
}: CarpoolRequestAction) {
  const { isAuthenticated, authToken } = useAuth();

  const mutation = useMutation({
    mutationFn(isAccept: boolean) {
      if (!isAuthenticated) throw new Error("Must be authenticated");
      if (isAccept) {
        return CarpoolService.acceptCarpoolRequest({ carpoolId, requestId }, authToken)
      } else {
        return CarpoolService.declineCarpoolRequest({ carpoolId, requestId }, authToken)
      }
    },
    onSuccess(_data, isAccept) {
      toast.success(`Ride Request ${isAccept ? "accepted" : "declined"} successfully!`);
      onSuccess?.();
    },
  })
  return (
    <div>
      <div className="flex items-center gap-2">
        <Button disabled={mutation.isPending} onClick={() => mutation.mutate(true)} color="success" variant="contained">Accept</Button>
        <Button disabled={mutation.isPending} onClick={() => mutation.mutate(false)} color="error" variant="contained">Decline</Button>
        {
          mutation.isPending && <CircularProgress size={20} />
        }
      </div>
      {
        mutation.isError && <Alert color="error">{ mutation.error.message }</Alert>
      }
    </div>
  )
}