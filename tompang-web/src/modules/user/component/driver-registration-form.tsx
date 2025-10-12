import { useRef, useState } from "react";
import TextField from "@mui/material/TextField";
import FileInput, { type FileInputRef } from "../../../components/file-input";
import { useMutation } from "@tanstack/react-query";
import { useAuth } from "../../../context/auth-context";
import Alert from "@mui/material/Alert";
import Button from "@mui/material/Button";
import DriverService from "../../../api/services/driver/driver.service";
import toast from "react-hot-toast";
import DevOnly from "../../../components/dev-only";
import Checkbox from "@mui/material/Checkbox";
import FormControlLabel from "@mui/material/FormControlLabel";

interface DriverRegistrationFormProps {
  onRegistered?:() => void;
}

export default function DriverRegistrationForm({
  onRegistered,
}: DriverRegistrationFormProps) {

  const { isAuthenticated, currentUserId, authToken } = useAuth();
  const [ file, setFile ] = useState<File | null>(null);
  const [vehicleRegistrationNumber, setVehicleRegistrationNumber] = useState<string>("");
  const [vehicleMake, setVehicleMake] = useState<string>("");
  const [vehicleModel, setVehicleModel] = useState<string>("");
  // extra field for dev debugging
  const [manualReview, setManualReview] = useState<boolean>(false);
  const fileInputRef = useRef<FileInputRef>(null);

  const restoreDefaults = () => {
    setVehicleRegistrationNumber("");
    setVehicleMake("");
    setVehicleModel("");
    setManualReview(false);
    setFile(null);
    fileInputRef.current?.clearFile();
  };

  const uploadMutation = useMutation({
    mutationFn: DriverService.registerDriver,
    onSuccess: () => {
      toast.success("Registration created!");
      onRegistered?.();
      // restore fields to default
      restoreDefaults();
    }
  })

  const handleUpload = () => {
    if (file && isAuthenticated) {
      uploadMutation.mutate({
        file,
        vehicleMake,
        vehicleModel,
        vehicleRegistrationNumber,
        userId: currentUserId,
        authToken: authToken,
        requireManualReview: manualReview,
      });
    }
  }

  return (
    <div className="flex flex-col gap-2">
      <h2 className="text-lg text-gray-800">New Registration</h2>
      <div className="flex gap-2">
        <TextField
          disabled={uploadMutation.isPending}
          required
          variant="standard"
          label="Registration no."
          value={vehicleRegistrationNumber}
          onChange={(e) => setVehicleRegistrationNumber(e.target.value)}
        />
        <TextField
          disabled={uploadMutation.isPending}
          required
          variant="standard"
          label="Make"
          value={vehicleMake}
          onChange={(e) => setVehicleMake(e.target.value)}
        />
        <TextField
          disabled={uploadMutation.isPending}
          required
          variant="standard"
          label="Model"
          value={vehicleModel}
          onChange={(e) => setVehicleModel(e.target.value)}
        />
        <DevOnly>
          <FormControlLabel 
            className="text-gray-600"
            control={
              <Checkbox 
                disabled={uploadMutation.isPending} 
                value={manualReview} 
                onChange={(e) => setManualReview(e.target.checked)} />
            } 
            label="Manual review" />
        </DevOnly>
      </div>
      <FileInput disabled={uploadMutation.isPending} ref={fileInputRef} onFileChange={setFile} />
      <Button className="w-fit" variant="contained" onClick={handleUpload} disabled={uploadMutation.isPending}>Submit</Button>
      {
        uploadMutation.isError && <Alert severity="error">{ uploadMutation.error.message }</Alert>
      }
    </div>
  )
}