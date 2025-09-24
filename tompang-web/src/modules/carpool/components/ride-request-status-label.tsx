import type { RideRequestStatus } from "../../../api/services/ride-request/types";

interface RideRequestStatusLabelProps {
  className?: string;
  status: RideRequestStatus;
}

export default function RideRequestStatusLabel({
  className,
  status,
}: RideRequestStatusLabelProps) {
  return (
    <span className={`${className} font-semibold text-lg p-1`}>{status}</span>
  )
}