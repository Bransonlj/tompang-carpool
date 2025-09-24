export interface ErrorResponseDto {
  timestamp: string; // ISO datetime format string
  status: number;
  error: string;
  message: string;
  path: string;
}