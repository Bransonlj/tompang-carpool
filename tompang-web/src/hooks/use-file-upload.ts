import { useEffect, useState } from "react";

const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

interface UseSingleFileUploadParams {
  fileType?: "image"
  /**
   * @default 5MB
   */
  maxFileSize?: number;
}

interface UseSingleFileUploadHook {
  file: File | null,
  uploadFile: (uploadedFile: File) => void;
  preview: string | null;
  uploadError: string | null;
}

export function formatFileSize(bytes: number) {
  if (bytes === 0) return "0 B";
  const units = ["B", "KB", "MB", "GB", "TB", "PB"];
  const k = 1024; // base
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  const size = bytes / Math.pow(k, i);
  return `${size.toFixed(1)} ${units[i]}`;
}

export function useSingleFileUpload({
  fileType,
  maxFileSize=MAX_FILE_SIZE
}: UseSingleFileUploadParams = {}): UseSingleFileUploadHook {
    const [file, setFile] = useState<File | null>(null)
    const [preview, setPreview] = useState<string | null>(null);
    const [uploadError, setUploadError] = useState<string | null>(null);
    /**
     * updates *updateError* if exceed max filesize or invalid filetype
     * @param uploadedFile 
     */
    function uploadFile(uploadedFile: File) {
      if (uploadedFile.size > maxFileSize) {
        setUploadError("File is too large! Max size is 5 MB.");
        return;
      }

      if (!!fileType && !uploadedFile.type.startsWith(`${fileType}/`)) {
        setUploadError("Invalid filetype.");
        return;
      }
      
      setFile(uploadedFile);
      if (preview) {
        // cleanup
        URL.revokeObjectURL(preview);
      }
      const objectUrl = URL.createObjectURL(uploadedFile);
      setPreview(objectUrl);
      setUploadError(null);
    };
  
    // Clean up object URL to avoid memory leaks
    useEffect(() => {
      return () => {
        if (preview) {
          URL.revokeObjectURL(preview);
        }
      };
    }, [preview]);

    return {
      file,
      uploadFile,
      preview,
      uploadError,
    }
}