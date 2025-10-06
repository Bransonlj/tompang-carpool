import type { ChangeEvent } from "react";
import { formatFileSize, useSingleFileUpload } from "../../../hooks/use-file-upload";
import Button from "@mui/material/Button";
import Alert from "@mui/material/Alert";

interface FileInputProps {
  fileType?: "image";
  onUpload: (file: File) => void;
  uploadDisabled?: boolean;
}

export default function FileInput({
  fileType="image",
  onUpload,
  uploadDisabled,
}: FileInputProps) {
  const { file, preview, uploadFile, uploadError } = useSingleFileUpload({
    fileType,
  });

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0] ?? null;
    if (selectedFile) {
      uploadFile(selectedFile);
    }
  };

  const handleUpload = () => {
    if (file) {
      onUpload(file);
    }
  }

  return (
    <div>
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
      <Button onClick={handleUpload} disabled={!file || uploadDisabled}>Upload</Button>
      {
        uploadError && <Alert severity="error">{ uploadError }</Alert>
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