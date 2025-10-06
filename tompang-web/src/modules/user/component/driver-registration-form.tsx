import { useState } from "react";
import TextField from "@mui/material/TextField";
import FileInput from "./file-input";
import { useMutation } from "@tanstack/react-query";
import { registerDriver } from "../../../api/services/driver/driver.service";
import { useAuth } from "../../../context/auth-context";
import Alert from "@mui/material/Alert";

export default function DriverRegistrationForm() {

  const { isAuthenticated, currentUserId, authToken } = useAuth();
  const [vehicleRegistrationNumber, setVehicleRegistrationNumber] = useState<string>("");
  const [vehicleMake, setVehicleMake] = useState<string>("");
  const [vehicleModel, setVehicleModel] = useState<string>("");

  const uploadMutation = useMutation({
    mutationFn: registerDriver,
    onSuccess: () => alert("Registration created")
  })

  const handleUpload = (file: File) => {
    if (isAuthenticated) {
      uploadMutation.mutate({
        file,
        vehicleMake,
        vehicleModel,
        vehicleRegistrationNumber,
        userId: currentUserId,
        authToken: authToken,
      });
    }
  }

  return (
    <div>
      <TextField
        required
        variant="standard"
        label="Registration no."
        value={vehicleRegistrationNumber}
        onChange={(e) => setVehicleRegistrationNumber(e.target.value)}
      />
      <TextField
        required
        variant="standard"
        label="Make"
        value={vehicleMake}
        onChange={(e) => setVehicleMake(e.target.value)}
      />
      <TextField
        required
        variant="standard"
        label="Model"
        value={vehicleModel}
        onChange={(e) => setVehicleModel(e.target.value)}
      />
      <FileInput onUpload={handleUpload} uploadDisabled={uploadMutation.isPending}/>
      {
        uploadMutation.isError && <Alert severity="error">{ uploadMutation.error.message }</Alert>
      }
    </div>
  )
}