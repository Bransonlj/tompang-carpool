import type { LatLng } from "leaflet"
import { MapContainer, Marker, TileLayer } from "react-leaflet";

interface RoutePreviewProps {
  origin: LatLng;
  destination: LatLng;
  className?: string;
}

export default function RoutePreview({
  origin,
  destination,
  className,
}: RoutePreviewProps) {
  return (
    <MapContainer center={destination} zoom={13} scrollWheelZoom={false} className={`${className}`}>
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <Marker position={origin} />
      <Marker position={destination} />
    </MapContainer>
  )
}