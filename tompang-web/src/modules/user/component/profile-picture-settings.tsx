import type { ChangeEvent } from "react";
import { useAuth } from "../../../context/auth-context";
import { formatFileSize, useSingleFileUpload } from "../../../hooks/use-file-upload";
import { useMutation, useQuery } from "@tanstack/react-query";
import { getUserById, uploadProfilePicture } from "../../../api/services/user/user.service";
import Button from "@mui/material/Button";
import Alert from "@mui/material/Alert";

export default function ProfilePictureSettings() {
  
  const { isAuthenticated, currentUserId, authToken } = useAuth();

  const { file, preview, uploadFile, uploadError } = useSingleFileUpload({
    fileType: "image",
  })

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0] ?? null;
    if (selectedFile) {
      uploadFile(selectedFile);
    }
  };

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
      <input 
        className="bg-white rounded-lg border-2 border-gray-400"
        type="file" 
        onChange={handleFileChange}
      ></input>
      {
        file && 
          <div>
            <span>{file.name}</span>
            <span>{formatFileSize(file.size)}</span>
            <span>Last Modified: {new Date(file.lastModified).toLocaleString()}</span>
          </div>
      }
      <Button onClick={handleUpload} disabled={!file || uploadMutation.isPending}>Upload</Button>
      {
        uploadError && <Alert severity="error">{ uploadError }</Alert>
      }
      {
        uploadMutation.isError && <Alert severity="error">{ uploadMutation.error.message }</Alert>
      }
      {
        preview && <img
          src={preview}
          alt="preview"
          className="mt-2 max-w-xs rounded shadow"
        />
      }
    </div>

  )
}