import { useState } from "react";
import TextField from "@mui/material/TextField";
import FileInput from "../../../components/file-input";
import { useMutation } from "@tanstack/react-query";
import { registerDriver } from "../../../api/services/driver/driver.service";
import { useAuth } from "../../../context/auth-context";
import Alert from "@mui/material/Alert";
import Button from "@mui/material/Button";

export default function DriverRegistrationForm() {

  const { isAuthenticated, currentUserId, authToken } = useAuth();
  const [ file, setFile ] = useState<File | null>(null);
  const [vehicleRegistrationNumber, setVehicleRegistrationNumber] = useState<string>("");
  const [vehicleMake, setVehicleMake] = useState<string>("");
  const [vehicleModel, setVehicleModel] = useState<string>("");

  const uploadMutation = useMutation({
    mutationFn: registerDriver,
    onSuccess: () => alert("Registration created")
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
      <FileInput onFileChange={setFile} />
      <Button onClick={handleUpload} disabled={uploadMutation.isPending}>Submit</Button>
      {
        uploadMutation.isError && <Alert severity="error">{ uploadMutation.error.message }</Alert>
      }
    </div>
  )
}