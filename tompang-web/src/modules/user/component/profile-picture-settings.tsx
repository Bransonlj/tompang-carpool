import { useState } from "react";
import { useAuth } from "../../../context/auth-context";
import { useMutation, useQuery } from "@tanstack/react-query";
import { getUserById, uploadProfilePicture } from "../../../api/services/user/user.service";
import Button from "@mui/material/Button";
import Alert from "@mui/material/Alert";
import FileInput from "../../../components/file-input";

export default function ProfilePictureSettings() {
  
  const { isAuthenticated, currentUserId, authToken } = useAuth();
  const [file, setFile] = useState<File | null>(null);

  const uploadMutation = useMutation({
    mutationFn: uploadProfilePicture,
    onSuccess: () => alert("Upload successful")
  })

  const handleUpload = () => {
    if (file && isAuthenticated) {
      uploadMutation.mutate({
        file,
        userId: currentUserId,
        token:authToken,
      });
    }
  }
  
  const {
    data: userData,
    isPending: isUserDataPending,
    isError: isUserDataError,
    error: userDataError,
  } = useQuery({
    queryKey: ["user-id", currentUserId],
    queryFn: () => {
      if (!currentUserId || !isAuthenticated) {
        throw new Error("User Id required");
      }

      return getUserById(currentUserId, authToken)
    }
  })

  if (isUserDataPending) {
    return <div>Loading</div>
  }

  if (isUserDataError) {
    return <div>{userDataError.message}</div>
  }

  return (
    <div>
      <img
        src={userData.profileImgUrl}
        alt="profile picture"
        className="mt-2 max-w-xs rounded shadow"
      />
      <FileInput onFileChange={setFile} />
      <Button onClick={handleUpload}>Upload</Button>
      {
        uploadMutation.isError && <Alert severity="error">{ uploadMutation.error.message }</Alert>
      }
    </div>

  )
}