import type { ChangeEvent } from "react";
import { formatFileSize, useSingleFileUpload } from "../hooks/use-file-upload";
import Alert from "@mui/material/Alert";

interface FileInputProps {
  fileType?: "image";
  onFileChange?: (file: File) => void;
}

export default function FileInput({
  fileType="image",
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