import type { ChangeEvent } from "react";
import { formatFileSize, useSingleFileUpload } from "../hooks/use-file-upload";
import Alert from "@mui/material/Alert";
import IconButton from "@mui/material/IconButton";
import { X } from "lucide-react";

interface FileInputProps {
  fileType?: "image";
  onFileChange?: (file: File | null) => void;
}

export default function FileInput({
  fileType="image",
  onFileChange,
}: FileInputProps) {
  const { file, preview, uploadFile, uploadError, clearFile } = useSingleFileUpload({
    fileType,
  });

  const handleFileChange = (e: ChangeEvent<HTMLInputElement> | null) => {
    const selectedFile = e?.target?.files?.[0] ?? null;
    onFileChange?.(selectedFile);
    if (selectedFile) {
      uploadFile(selectedFile);
    } else {
      clearFile();
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
            <IconButton onClick={() => handleFileChange(null)}><X /></IconButton>
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